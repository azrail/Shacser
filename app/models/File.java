/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.petebevin.markdown.MarkdownProcessor;

import play.Play;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.templates.JavaExtensions;

/**
 * @author prime
 * 
 */
@Entity
public abstract class File extends Model {

	public static final String	IMAGE	= "image";

	@Required
	public String				title;

	@Required
	public String				description;

	@Required
	public Date					postedAt;

	@Required
	@ManyToOne
	public User					author;

	@Required
	public String				type;

	@Required
	public Blob					file;

	public String				extension;
	public String				fullUrl;
	public String				slugUrl;
	public String				thumbUrl;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Keyword>			keywords;

	public String				name;

	public File(User author, String title, String description, Blob file, String type) {
		this.keywords = new TreeSet();
		this.author = author;
		this.title = title;
		this.type = type;
		this.description = description;
		this.file = file;
		this.postedAt = new Date();
		this.extension = this.file.type().substring(6);
		this.fullUrl = Play.configuration.getProperty("blog.url") + "/pictures/" + id + "/" + JavaExtensions.slugify(title, true) + "." + this.file.type().substring(6);
		this.thumbUrl = Play.configuration.getProperty("blog.url") + "/pictures/thumb/" + id + "/100/" + JavaExtensions.slugify(title, true) + "." + this.file.type().substring(6);
		this.slugUrl = JavaExtensions.slugify(title, true);
		this.name = JavaExtensions.slugify(title, true) + "." + this.file.type().substring(6);
	}


}
