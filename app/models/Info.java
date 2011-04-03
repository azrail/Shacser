package models;

import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.TwitterException;

public class Info {
	public List<Post>	posts;
	public List<Tweet>	tweets;
	
	public Info() {
		this.posts = Post.find("order by postedAt desc").fetch();
		this.tweets = Tweet.find("order by createdAt desc").fetch(3);
	}
	

}
