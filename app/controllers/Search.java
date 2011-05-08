/**
 * 
 */
package controllers;

import static org.elasticsearch.index.query.xcontent.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.xcontent.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.List;

import models.Info;
import models.Post;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import play.modules.elasticsearch.ElasticSearch;
import play.mvc.Before;
import play.mvc.Controller;
import utils.LongUtils;
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
	
	public static void search(String suchString, Long autoCompleteId, String autoCompleteModel) {
		if (!StringUtils.nullCheck(autoCompleteModel) && !LongUtils.nullCheck(autoCompleteId)) {
			if (autoCompleteModel.equals("models_post")) {
				Post post = Post.findById(autoCompleteId);
				Application.show(post.getYear(), post.getMonth(), post.getDay(), post.slugurl);
			}
		} else {
			Client client = ElasticSearch.client();
			SearchResponse response = client.prepareSearch("models_post").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(boolQuery().should(termQuery("content", suchString)).should(termQuery("title", suchString))).setFrom(0).setSize(60).setExplain(true).execute().actionGet();
			SearchHit[] sh = response.getHits().hits();
			List<Post> posts = new ArrayList<Post>();
			for (SearchHit searchHit : sh) {
				Post post = Post.findById(new Long(searchHit.id()));
				posts.add(post);
			}

			if (posts.size() > 1 || posts.size() == 0) {
				Info info = new Info();
				render(suchString, posts, info);
			} else {
				Application.show(posts.get(0).getYear(), posts.get(0).getMonth(), posts.get(0).getDay(), posts.get(0).slugurl);
			}
		}
	}
}
