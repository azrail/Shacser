package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import models.Info;
import models.Post;
import models.Site;
import models.User;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.modules.fbconnect.FBConnectPlugin;
import play.modules.fbconnect.FBConnectSession;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.Session;
import tags.fbconnect.FBConnectTags;

import com.google.gson.JsonObject;
import com.petebevin.markdown.MarkdownProcessor;

public class Application extends Controller {
	
	@Before
	static void addDefaults() {
		renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
		renderArgs.put("description", Play.configuration.getProperty("blog.description"));
		renderArgs.put("keywords", Play.configuration.getProperty("blog.keywords"));
		renderArgs.put("autor", Play.configuration.getProperty("blog.autor"));
		renderArgs.put("twitter", Play.configuration.getProperty("blog.twitter"));
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
	
	public static void show(Long id) {
		Post post = Post.findById(id);
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
//		if (!Play.id.equals("test")) {
//			validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
//		}
		if (validation.hasErrors()) {
			render("Application/show.html", post, randomID);
		}
		post.addComment(author, content, email, url);
		flash.success("Thanks for posting %s", author);
		Cache.delete(randomID);
		show(postId);
	}
	
	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText("#000000");
		Cache.set(id, code, "30mn");
		renderBinary(captcha);
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
	
	public static void markdowPreview() {
		String content = Application.request.params.get("data").toString();
		MarkdownProcessor m = new MarkdownProcessor();
		String html_content = m.markdown(content);
		render("Application/markdowPreview.html", html_content);
	}
}
