package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Comment;
import models.Image;
import models.Info;
import models.Post;
import models.Site;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Router;
import play.mvc.Router.Route;
import utils.StringUtils;
import utils.UAgentInfo;

import com.petebevin.markdown.MarkdownProcessor;

public class Application extends Controller {

	static boolean	isIphone	= false;

	@Before
	static void addDefaults() {
		renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
		renderArgs.put("description", Play.configuration.getProperty("blog.description"));
		renderArgs.put("keywords", Play.configuration.getProperty("blog.keywords"));
		renderArgs.put("autor", Play.configuration.getProperty("blog.autor"));
		renderArgs.put("twitter", Play.configuration.getProperty("blog.twitter"));
		renderArgs.put("searchurl", Play.configuration.getProperty("elasticsearch.url"));

		String useragent = "";
		String accept = "";

		try {
			useragent = Request.current().headers.get("user-agent").value();
			accept = Request.current().headers.get("accept").value();
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
		
		
		renderArgs.put("header.user-agent", useragent);
		renderArgs.put("header.accept", accept);

		UAgentInfo uai = new UAgentInfo(useragent, accept);
		renderArgs.put("isIphone", uai.isTierIphone);

		Application.isIphone = uai.isTierGenericMobile;

//		if (isIphone && !Request.current().path.startsWith("/mobile")) {
//			redirect("/mobile" + Request.current().path);
//		}

	}

	public static void renderImage(Long id){ 
		Image f = Image.findById(id); 
	    renderBinary(f.file.get()); 
	}
	
	
	public static void rssFeedPosts() {
		List<Post> posts = Post.find("order by postedAt desc").fetch();
		response.contentType = "application/rss+xml; charset=utf-8";
		render("feeds/FeedPosts.rss", posts);
	}

	public static void index() {
		Post frontPost = Post.find("order by postedAt desc").first();
		List<Post> olderPosts = Post.find("order by postedAt desc").from(1).fetch(10);
		Info info = new Info();
		render(frontPost, olderPosts, info);
	}
	
	public static void show(String year, String month, String day, String slugurl, Long id) {
		Post post = Post.find("slugurl = ?",slugurl).first();
		String randomID = Codec.UUID();
		Info info = new Info();
		render(post, randomID, info);
	}
	
	public static void site(Long id) {
		Site site = Site.findById(id);
		String randomID = Codec.UUID();
		Info info = new Info();
		render(site, randomID, info);
	}

	public static void postComment(Long postId, @Required(message = "Author is required") String author, @Required(message = "A message is required") String content, String email, String url, String randomID) {
		Post post = Post.findById(postId);
		// if (!Play.id.equals("test")) {
		// validation.equals(code,
		// Cache.get(randomID)).message("Invalid code. Please type it again");
		// }
		if (validation.hasErrors()) {
			render("Application/show.html", post, randomID);
		}
		post.addComment(author, content, email, url);
		flash.success("Thanks for posting %s", author);
		Cache.delete(randomID);
		show(post.getYear(), post.getMonth(), post.getDay(), post.slugurl, postId);
	}

	public static void elasticSearchBeispiel() {
		render("Beispiele/elasticSearch.html");
	}
	
	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText("#000000");
		Cache.set(id, code, "30mn");
		renderBinary(captcha);
	}

	public static void gravatar(Long id) throws FileNotFoundException {
		Comment comment = Comment.findById(id);
		if (comment.email == null) {
			File gravatar = play.Play.getFile("public/images/gravatar.jpg");
			renderBinary(gravatar);
		} else {

			String email = comment.email.trim().toLowerCase();
			String gravatar = StringUtils.md5Hash(email);

			Map<String, File> files = new HashMap<String, File>();
			Map<String, Boolean> test = new HashMap<String, Boolean>();

			File jpegDir = new File(Play.tmpDir + File.separator + "gravatar" + File.separator + "jpg");
			File pngDir = new File(Play.tmpDir + File.separator + "gravatar" + File.separator + "png");
			File gifDir = new File(Play.tmpDir + File.separator + "gravatar" + File.separator + "gif");

			jpegDir.mkdirs();
			pngDir.mkdirs();
			gifDir.mkdirs();

			files.put("jpeg", new File(jpegDir.getAbsoluteFile() + File.separator + gravatar));
			files.put("png", new File(pngDir.getAbsoluteFile() + File.separator + gravatar));
			files.put("gif", new File(gifDir.getAbsoluteFile() + File.separator + gravatar));

			Set<String> set = files.keySet();
			for (String type : set) {
				File file = files.get(type);
				if (file.exists()) {
					test.put(type, true);
				} else {
					test.put(type, false);
				}
			}

			if (!test.containsValue(true) && gravatar != null) {
				WSRequest wsr = WS.url("http://www.gravatar.com/avatar/" + gravatar + "?s=50");
				HttpResponse hr = wsr.get();
				File file = files.get(hr.getContentType().substring(6));

				try {
					InputStream inputStream = hr.getStream();
					OutputStream out = new FileOutputStream(file);

					byte buf[] = new byte[1024];
					int len;
					while ((len = inputStream.read(buf)) > 0)
						out.write(buf, 0, len);
					out.close();
					inputStream.close();
					test.put(hr.getContentType().substring(6), true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// String basepath = Play.tmpDir + File.separator + "gravatar" +
			// File.separator;

			ArrayList<File> sfile = searchFile(new File(Play.tmpDir + File.separator + "gravatar" + File.separator), gravatar);

			// String gravpath =
			// sfile.get(0).getAbsoluteFile().toString().replace(basepath, "");
			// String[] type = gravpath.split("\\" + File.separator);

			renderBinary(sfile.get(0));
		}

	}

	public static ArrayList<File> searchFile(File dir, String find) {

		File[] files = dir.listFiles();
		ArrayList<File> matches = new ArrayList<File>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equalsIgnoreCase(find)) {
					matches.add(files[i]);
				}
				if (files[i].isDirectory()) {
					matches.addAll(searchFile(files[i], find));
				}
			}
		}
		return matches;
	}

	public static void listTagged(String tag) {
		List<Post> posts = Post.findTaggedWith(tag);
		Info info = new Info();
		render(tag, posts, info);
	}

	public static void robots() throws FileNotFoundException {
		File robots = play.Play.getFile("public/robots.txt");
		InputStream is = new FileInputStream(robots);
		response.setHeader("Content-Length", robots.length() + "");
		response.cacheFor("2h");
		response.contentType = "text/html";
		response.direct = is;
	}

	public static void rootFile(String file) throws FileNotFoundException {
		File rootFile = play.Play.getFile("public/files/" + file);
		renderBinary(rootFile);
	}

	public static void markdowPreview() {
		String content = Application.request.params.get("data").toString();
		MarkdownProcessor m = new MarkdownProcessor();
		String html_content = m.markdown(content);
		render("Application/markdowPreview.html", html_content);
	}
}
