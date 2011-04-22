package controllers;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import models.GistFile;
import models.Image;
import models.Keyword;
import models.Post;
import models.Tag;
import models.User;
import play.Play;
import play.db.jpa.Blob;
import play.libs.Images;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.libs.ws.WSAsync.WSAsyncRequest;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.petebevin.markdown.MarkdownProcessor;

@With(Secure.class)
public class Admin extends Controller {
	
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
		List<Post> posts = Post.find("author.email", Security.connected()).fetch();
		List<Image> images = Image.findAll();
		render(posts,images);
	}
	
	@Check("posteroradmin")
	public static void media() { 
		List<Image> images = Image.findAll();
		render(images);
	} 
	
	public static void renderImage(Long id){ 
		Image f = Image.findById(id); 
	    renderBinary(f.file.get()); 
	} 
	public static void formImage(Long id) {
		if (id != null) {
			Image image = Image.findById(id);
			render(image);
		}
		render();
	}
	public static void form(Long id) {
		if (id != null) {
			Post post = Post.findById(id);
			render(post);
		}
		render();
	}
	
	public static void saveImage(Long id, String title, String keywords, String description, Blob file) {
		Image image;
		
		System.out.println(keywords);
		System.out.println(description);
		System.out.println(file);
		if (id == null) {
			User user = User.find("byEmail", Security.connected()).first();
			image = new Image(user, title, description, file);
		} else {
			image = Image.findById(id);
			image.title = title;
			image.description = description;
			if (file != null) {
				image.file = file;
			}
			image.keywords.clear();
		}

		for (String keyword : keywords.split("\\s+")) {
			if (keyword.trim().length() > 0) {
				image.keywords.add(Keyword.findOrCreateByName(keyword));
			}
		}
		
		// Validate
		validation.valid(image);
		if (validation.hasErrors()) {
			System.out.println(validation.errorsMap());
			render("@formImage", image);
		}
		// Save
		image.save();
		media();
	}
	
	public static void save(Long id, String title, String content, String description, String tags, String keywords, String gistId) {
		Post post;
		if (id == null) {
			// Create post
			User author = User.find("byEmail", Security.connected()).first();
			post = new Post(author, title, content, gistId);
		} else {
			// Retrieve post
			post = Post.findById(id);
			post.title = title;
			post.content = content;
			post.description = description;
			post.gistId = gistId;
			
			if (gistId != null && gistId.length() > 0) {
				
				WSRequest wsr = WS.url("https://gist.github.com/api/v1/json/" + gistId);
				JsonElement je = wsr.get().getJson();
				
				JsonObject jo = je.getAsJsonObject();
				JsonElement gist = jo.getAsJsonArray("gists").getAsJsonArray().get(0);
				JsonArray files = gist.getAsJsonObject().getAsJsonArray("files");
				//TODO add comments
				//JsonArray comments = gist.getAsJsonObject().getAsJsonArray("comments");
				
				for (JsonElement jsonElement : files) {
					String file = removeFirstLastChar(jsonElement.toString());
					GistFile gistFile = GistFile.find("gistId = ? and fileName = ?", gistId, file).first();
					
					if (gistFile == null) {
						String gistFileURL = "https://gist.github.com/899005.js?file=" + file;
						String fileContent = getGistFileContent(gistFileURL);
						post.addGistFile(gistId, file, fileContent, gistFileURL);
					}
				}
			}
			
			MarkdownProcessor m = new MarkdownProcessor();
			post.html_content = m.markdown(content);
			
			post.tags.clear();
			post.keywords.clear();
		}
		// Set tags list
		for (String tag : tags.split("\\s+")) {
			if (tag.trim().length() > 0) {
				post.tags.add(Tag.findOrCreateByName(tag));
			}
		}
		// Set keyword list
		for (String keyword : keywords.split("\\s+")) {
			if (keyword.trim().length() > 0) {
				post.keywords.add(Keyword.findOrCreateByName(keyword));
			}
		}
		// Validate
		validation.valid(post);
		if (validation.hasErrors()) {
			render("@form", post);
		}
		// Save
		post.save();
		index();
	}
	
	private static String getGistFileContent(String gistFileURL) {
		WSRequest ws = WS.url(gistFileURL);
		HttpResponse hr = ws.get();
		
		String lines[] = hr.getString().split("\n");
		TreeSet<String> sortContent = new TreeSet<String>();
		sortContent.addAll(Arrays.asList(lines));
		sortContent.pollFirst();
		
		String fileContent = sortContent.pollFirst();
		fileContent = fileContent.replace("\\\"", "\"").replace("\\'", "\"").replace("\\/", "/");
		fileContent = fileContent.substring(16);
		fileContent = fileContent.substring(0, fileContent.length() - 2);
		fileContent = fileContent.replace("\\n", "");
		fileContent = fileContent.replace('\n', ' ');
		System.out.println(fileContent);
		
		return fileContent;
	}
	
//	public static void fileUpload(String title, Blob file, String keywords, String description) {
//		User user = User.find("byEmail", Security.connected()).first();
//
//		Image img = new Image(user, title, description, file);
//		
//		for (String keyword : keywords.split("\\s+")) {
//			if (keyword.trim().length() > 0) {
//				img.keywords.add(Keyword.findOrCreateByName(keyword));
//			}
//		}
//		
//		img.save();
//	}
	
	private static String removeFirstLastChar(String string) {
		string = string.substring(1, string.length() - 1);
		return string;
	}
	
}
