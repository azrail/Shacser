package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Tweet extends Model {
	
	@Required
	public long		tweetId;
	
	@Lob
	@Required
	@MaxSize(2000)
	public String	content;
	
	@Required
	public Date		createdAt;
	
	@Required
	public String	user;
	
	public Tweet(long tweetId, String content, Date createdAt, String user) {
		this.tweetId = tweetId;
		this.createdAt = createdAt;
		this.user = user;
		this.content = parseTweed(content);
	}
	
	private String parseTweed(String content) {
		String[] chunks = content.split(" ");
		String newText = "";
		for (int j = 0; j < chunks.length; j++) {
			String string = chunks[j];
			if (string.startsWith("@")) {
				
				String displayname = checkEndsWith(string);
				String username = displayname.substring(1, displayname.length());
				chunks[j] = "<a href=\"http://twitter.com/" + username + "\" target=\"_blank\">" + displayname + "</a>";
			}
			if (string.startsWith("#")) {
				String displayhash = checkEndsWith(string);
				String hash = displayhash.substring(1, displayhash.length());
				chunks[j] = "<a href=\"http://twitter.com/#!/search?q=%23" + hash + "\" target=\"_blank\">" + displayhash + "</a>";
			}
			if (string.startsWith("http")) {
				chunks[j] = "<a href=\"" + string + "\" target=\"_blank\">" + string + "</a>";
			}
			if (string.startsWith("www")) {
				chunks[j] = "<a href=\"http://" + string + "\" target=\"_blank\">" + string + "</a>";
			}
			newText += " " + chunks[j];
		}
		return newText;
	}
	
	private String checkEndsWith(String string) {
		if (string.length() == 1) {
			return string;
		}
		String ends = string.substring(string.length() - 1, string.length());
		if (ends.matches("\\W")) {
			if (ends.equals("_")) {
				return string;
			} else {
				return checkEndsWith(string.substring(0, string.length() - 1));
			}
		} else {
			return string;
		}
	}
}
