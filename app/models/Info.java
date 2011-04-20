package models;

import java.util.List;

public class Info {
	public List<Post>	posts;
	public List<Tweet>	tweets;
	
	public Info() {
		this.posts = Post.find("order by postedAt desc").fetch();
		this.tweets = Tweet.find("order by createdAt desc").fetch(3);
	}
}
