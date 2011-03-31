package models;

import java.util.List;

public class Info {
	public String title = "Über das Blog";
	public String html_content = "test";
	public List<Post> posts;
	
	public Info() {
		this.posts = Post.find("order by postedAt desc").fetch();
	}
	
}
