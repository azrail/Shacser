package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;

import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

//@ElasticSearchable
@Entity
public class Tweet extends Model {
	
	@Required
	public long		tweetId;
	
	@Lob
	@Required
	@MaxSize(2000)
	public String	text;
	
	@Required
	public Date		createdAt;
	
	@Required
	public String	user;
	
	public Tweet(long tweetId, String text, Date createdAt, String user) {
		this.tweetId = tweetId;
		this.createdAt = createdAt;
		this.user = user;
		this.text = parseTweed(text);
	}

	private String parseTweed(String text) {
		String[] chunks = text.split(" ");
		String newText = "";
		for (int j = 0; j < chunks.length; j++) {
			String string = chunks[j];
			System.out.println(string);
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
				chunks[j] = "<a href=\""+string+"\" target=\"_blank\">" + string + "</a>";
			}
			if (string.startsWith("www")) {
				chunks[j] = "<a href=\"http://"+string+"\" target=\"_blank\">" + string + "</a>";
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
