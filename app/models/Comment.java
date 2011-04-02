package models;

import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import play.Play;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Http.Request;
import play.mvc.Router;
import plugins.Akismet;
import utils.StringUtils;

// @ElasticSearchable
@Entity
public class Comment extends Model {
	
	@Required
	public String	author;
	
	@Required
	public Date		postedAt;
	
	@Lob
	@Required
	@MaxSize(10000)
	public String	content;
	
	@ManyToOne
	@Required
	public Post		post;
	
	public String	url;
	public String	email;
	
	public boolean	spam;
	
	public Comment(Post post, String author, String content, String email, String url) {
		this.post = post;
		this.author = author;
		this.url = url;
		this.email = email;
		this.content = Jsoup.clean(content, Whitelist.simpleText());
		this.postedAt = new Date();

		Akismet akismet = new Akismet("8ef0252ed762", Play.configuration.getProperty("blog.url"));
		
		String ipAddress = Request.current().remoteAddress;
		String userAgent = Request.current().headers.get("user-agent").value();
		String referrer = Request.current().headers.get("referer").value();
		
		Map<String, Object> params = new HashMap();
		params.put("id", post.getId());
		
		String permalink = Router.getFullUrl("Application.show", params);
		String commentType = "models.Post";
		String authorEmail = email;
		String authorURL = url;
		String commentContent = content;
		
		this.spam = akismet.commentCheck(ipAddress, userAgent, referrer, permalink, commentType, author, authorEmail, authorURL, commentContent, null);
		
	}
	
	public boolean hasURL() {
		if (this.url == null || this.url.length() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean hasMail() {
		if (this.email == null || this.email.length() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public String toString() {
		return content.length() > 50 ? content.substring(0, 50) + "..." : content;
	}
	
}