package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import models.Post;
import models.User;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.Session;

import com.google.gson.JsonObject;
import com.petebevin.markdown.MarkdownProcessor;

public class Application extends Controller {
	
	@Before
	static void addDefaults() {
		renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
	}
	
	public static void facebookLogin() {
		try {
			JsonObject profile = FbGraph.getObject("me");
			// or use the basic api method directly -> JsonObject profile =
			// FbGraph.api("me").getAsJsonObject();
			String email = profile.get("email").getAsString();
			User user = User.findByEmail(email);
			if (user == null) {
				String firstName = profile.get("first_name").getAsString();
				String lastName = profile.get("last_name").getAsString();
				String gender = profile.get("gender").getAsString();
				
				user = new User(firstName, lastName, email, gender);
				user.save();
			}
			Session.current().put("username", email);
			
			if (user.isAdmin || user.canPost) {
				redirect("/admin/");
			} else {
				redirect("/");
			}
		} catch (FbGraphException fbge) {
			if (fbge.getType().equals("OAuthException")) {
				flash.error("Facebook Authentication Failure", "");
			}
		}
		
	}
	
	public static void index() {
		// System.out.println("domain: " + request.domain);
		// Client client = ElasticSearch.client();
		// SearchResponse response = client.prepareSearch("models_post")
		// .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		// .setQuery(termQuery("content", "mvc"))
		// .setFrom(0).setSize(60).setExplain(true)
		// .execute()
		// .actionGet();
		//
		// SearchHits sh = response.getHits();
		//
		// System.out.println(response.getSuccessfulShards());
		//
		// for (SearchHit searchHit : sh) {
		// System.out.println(searchHit.getSource());
		// }
		
		try {
			JsonObject user = FbGraph.getObject("lethargicprince");
			System.out.println(user );
		} catch (FbGraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException  e) {
			// TODO: handle exception
		}
		
		
		Post frontPost = Post.find("order by postedAt desc").first();
		List<Post> olderPosts = Post.find("order by postedAt desc").from(1).fetch(10);
		render(frontPost, olderPosts);
	}
	
	public static void show(Long id) {
		Post post = Post.findById(id);
		String randomID = Codec.UUID();
		render(post, randomID);
	}
	
	public static void postComment(Long postId, @Required(message = "Author is required") String author, @Required(message = "A message is required") String content, @Required(message = "Please type the code") String code, String randomID) {
		Post post = Post.findById(postId);
		if (!Play.id.equals("test")) {
			validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
		}
		if (validation.hasErrors()) {
			render("Application/show.html", post, randomID);
		}
		post.addComment(author, content);
		flash.success("Thanks for posting %s", author);
		Cache.delete(randomID);
		show(postId);
	}
	
	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText("#E4EAFD");
		Cache.set(id, code, "30mn");
		renderBinary(captcha);
	}
	
	public static void listTagged(String tag) {
		List<Post> posts = Post.findTaggedWith(tag);
		render(tag, posts);
	}
	
	public static void robots() throws FileNotFoundException {
		File robots = play.Play.getFile("public/robots.txt");
		InputStream is = new FileInputStream(robots);
		response.setHeader("Content-Length", robots.length() + "");
		response.cacheFor("2h");
		response.contentType = "text/html";
		response.direct = is;
	}
	
	public static void markdowPreview() {		
		String content = Application.request.params.get("data").toString();
		MarkdownProcessor m = new MarkdownProcessor();
		String html_content = m.markdown(content);
		render("Application/markdowPreview.html", html_content);
	}
}
