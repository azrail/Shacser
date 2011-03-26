package controllers;

import play.*;
import play.modules.elasticsearch.ElasticSearch;
import play.mvc.*;
import play.data.validation.*;
import play.libs.*;
import play.cache.*;

import java.util.*;

import org.elasticsearch.action.search.*;
import org.elasticsearch.client.*;
import org.elasticsearch.index.query.xcontent.FieldQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import static org.elasticsearch.index.query.xcontent.FilterBuilders.*;
import static org.elasticsearch.index.query.xcontent.QueryBuilders.*;

import models.*;

public class Application extends Controller {
	
	@Before
	static void addDefaults() {
		renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
	}
	
	public static void index() {
    	
    	Client client = ElasticSearch.client();
    	SearchResponse response = client.prepareSearch("models_post")
    	.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
    	.setQuery(termQuery("content", "mvc"))
    	.setFrom(0).setSize(60).setExplain(true)
    	.execute()
    	.actionGet();
    	
    	SearchHits sh = response.getHits();
    	
    	System.out.println(response.getSuccessfulShards());
    	
    	for (SearchHit searchHit : sh) {
			System.out.println(searchHit.getSource());
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
	
}