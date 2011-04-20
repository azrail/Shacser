package models;

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
import play.modules.elasticsearch.annotations.ElasticSearchable;
import play.mvc.Http.Request;
import play.mvc.Router;
import plugins.Akismet;
import utils.StringUtils;

@Entity
@ElasticSearchable
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
		
		// >:-( :-) :-( :-P :-D :-O ;-) :-/ o.O 8-) <3 :heart:
		Whitelist whitelist = Whitelist.simpleText();
		whitelist.addTags("img");
		whitelist.addAttributes("img", "src", "title", "alt", "class", "height", "width");
		content = Jsoup.clean(content, whitelist);
		content = StringUtils.replaceSmilies(content, "/public/images/emoticons/blacy/", "emoticon", "height=\"16\" width=\"16\"");
		this.content = content;
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