/**
 * 
 */
package controllers;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import models.Info;
import models.Post;
import play.modules.search.Query;
import play.mvc.Before;
import play.mvc.Controller;
import utils.StringUtils;

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
		
		Query q = play.modules.search.Search.search("title:" + searchTerm + " OR content:" + searchTerm, Post.class);
		List<Post> posts = q.fetch();

		Gson gson =  new GsonBuilder().generateNonExecutableJson().create();
		Type listType = new TypeToken<List<String>>() {}.getType();
		gson.toJson(posts, listType);
		
		renderJSON(posts);
	}
	
	public static void search(String suchString) {
		
		if (StringUtils.nullCheck(suchString)) {
			suchString = request.params.allSimple().get("body");
		}
		
		System.out.println(suchString);
		
//		if (!StringUtils.nullCheck(autoCompleteModel) && !LongUtils.nullCheck(autoCompleteId)) {
//			if (autoCompleteModel.equals("models_post")) {
//				Post post = Post.findById(autoCompleteId);
//				Application.show(post.id);
//			}
//		} else {
//			Client client = ElasticSearch.client();
//			SearchResponse response = client.prepareSearch("models_post").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(boolQuery().should(termQuery("content", suchString)).should(termQuery("title", suchString))).setFrom(0).setSize(60).setExplain(true).execute().actionGet();
//			SearchHit[] sh = response.getHits().hits();
//			List<Post> posts = new ArrayList<Post>();
//			for (SearchHit searchHit : sh) {
//				Post post = Post.findById(new Long(searchHit.id()));
//				posts.add(post);
//			}
//
//			if (posts.size() > 1 || posts.size() == 0) {
//				Info info = new Info();
//				render(suchString, posts, info);
//			} else {
//				Application.show(posts.get(0).id);
//			}
//		}
	}
}
