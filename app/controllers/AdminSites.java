package controllers;

import java.util.List;

import models.Site;
import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import com.petebevin.markdown.MarkdownProcessor;

@With(Secure.class)
public class AdminSites extends Controller {
	
	@Before
	static void addDefaults() {
		renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
		renderArgs.put("description", Play.configuration.getProperty("blog.description"));
		renderArgs.put("keywords", Play.configuration.getProperty("blog.keywords"));
		renderArgs.put("autor", Play.configuration.getProperty("blog.autor"));
		renderArgs.put("twitter", Play.configuration.getProperty("blog.twitter"));
	}
	
	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user.fullname);
		}
	}
	
	@Check("posteroradmin")
	public static void index() {
		List<Site> sites = Site.find("author.email", Security.connected()).fetch();
		render(sites);
	}
	
	public static void form(Long id) {
		if (id != null) {
			Site site = Site.findById(id);
			render(site);
		}
		render();
	}
	
	public static void save(Long id, String title, String content) {
		Site site;
		if (id == null) {
			// Create post
			User author = User.find("byEmail", Security.connected()).first();
			site = new Site(author, title, content);
		} else {
			// Retrieve post
			site = Site.findById(id);
			site.title = title;
			site.content = content;
			
			MarkdownProcessor m = new MarkdownProcessor();
			site.html_content = m.markdown(content);
			
		}
		// Validate
		validation.valid(site);
		if (validation.hasErrors()) {
			render("@form", site);
		}
		// Save
		site.save();
		index();
	}
}
