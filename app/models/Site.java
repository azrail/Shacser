/**
 * 
 */
package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.search.Field;
import play.modules.search.Indexed;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author azrail
 * 
 */

@Indexed
@Entity
public class Site extends Model {
	
	@Field
	@Required
	public String	title;
	
	@Field
	@Required
	@As("yyyy-MM-dd")
	public Date		postedAt;
	
	@Field
	@Lob
	@Required
	@MaxSize(10000)
	public String	content;
	
	@Lob
	@MaxSize(10000)
	public String	description;
	
	@Field
	public String	keywords;
	
	@Lob
	public String	html_content;
	
	@Field
	@Required
	@ManyToOne
	public User		author;
	
	public Site(User author, String title, String content) {
		this.author = author;
		this.title = title;
		this.content = content;
		this.postedAt = new Date();
		
		MarkdownProcessor m = new MarkdownProcessor();
		html_content = m.markdown(content);
	}
	
	public String toString() {
		return title;
	}
	
}
