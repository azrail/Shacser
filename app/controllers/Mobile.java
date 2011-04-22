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

public class Mobile extends Controller {

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

		String useragent = Request.current().headers.get("user-agent").value();
		String accept = Request.current().headers.get("accept").value();

		renderArgs.put("header.user-agent", useragent);
		renderArgs.put("header.accept", accept);

		UAgentInfo uai = new UAgentInfo(useragent, accept);
		renderArgs.put("isIphone", uai.isTierIphone);

		Mobile.isIphone = uai.isTierIphone;

//		if (!isIphone && Request.current().path.startsWith("/mobile")) {
//			redirect(Request.current().path.replace("/mobile", ""));
//		}

	}

	public static void index() {
		Post frontPost = Post.find("order by postedAt desc").first();
		List<Post> posts = Post.find("order by postedAt desc").fetch();
		Info info = new Info();
		render(frontPost, posts, info);
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

}
