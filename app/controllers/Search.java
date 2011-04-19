/**
 * 
 */
package controllers;

import java.lang.reflect.Type;
import java.util.List;

import models.Info;
import models.Post;
import play.modules.search.Query;
import play.mvc.Before;
import play.mvc.Controller;
import utils.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author azrail
 * 
 */
public class Search extends Controller {

	@Before
	static void addDefaults() {
		Application.addDefaults();
	}

	public static void searchPostsJson(String body) {
		String searchTerm = StringUtils.chopFirstLast(body);
		String searchQuery = "title:" + searchTerm + " OR content:" + searchTerm;
		
		Query q = play.modules.search.Search.search(searchQuery , Post.class);

		List<Post> posts = q.fetch();

		Gson gson = new GsonBuilder().generateNonExecutableJson().create();
		Type listType = new TypeToken<List<Post>>() {
		}.getType();
		gson.toJson(posts, listType);

		System.out.println(searchQuery);
		System.out.println("Hits:" + posts.size());
		renderJSON(posts);
	}

	public static void search(String suchString) {

		if (StringUtils.nullCheck(suchString)) {
			suchString = request.params.allSimple().get("body");
		}

		String searchTerm = suchString;

		Query q = play.modules.search.Search.search("title:\"*" + searchTerm + "*\" OR content:\"*" + searchTerm + "*\"", Post.class);

		List<Post> posts = q.fetch();

		if (posts.size() > 1 || posts.size() == 0) {
			Info info = new Info();
			render(suchString, posts, info);
		} else {
			Application.show(posts.get(0).id);
		}
	}
}
