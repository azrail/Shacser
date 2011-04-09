/**
 * 
 */
package controllers;

import play.mvc.Before;
import play.mvc.Controller;

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
