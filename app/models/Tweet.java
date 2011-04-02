package models;

import java.util.Date;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

//@ElasticSearchable
@Entity
public class Tweet extends Model {
	
	@Required
	public long		tweetId;
	
	@Required
	public String	text;
	
	@Required
	public Date		createdAt;
	
	@Required
	public String	user;
	
	public Tweet(long tweetId, String text, Date createdAt, String user) {
		this.tweetId = tweetId;
		this.text = text;
		this.createdAt = createdAt;
		this.user = user;
	}
	
	public Tweet previous() {
		return Post.find("createdAt < ? order by createdAt desc", createdAt).first();
	}
	
	public Tweet next() {
		return Post.find("createdAt > ? order by createdAt asc", createdAt).first();
	}
}
