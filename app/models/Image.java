/**
 * 
 */
package models;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.persistence.Entity;

import play.Play;
import play.db.jpa.Blob;
import play.modules.elasticsearch.annotations.ElasticSearchable;
import play.mvc.Router;
import play.mvc.Router.Route;
import play.templates.JavaExtensions;
import utils.StringUtils;

/**
 * @author prime
 *
 */
@Entity
public class Image extends File {

	public String link; 
	
	public Image(User author, String title, String description, Blob file) {
		super(author, title, description, file, File.IMAGE);
	}
	
	public String getfullUrl() {
		return Play.configuration.getProperty("blog.url") +"/pictures/" + id + "/" + JavaExtensions.slugify(title, true) + "." + this.file.type().substring(6);
	}
	
	public String getName() {
		return JavaExtensions.slugify(title, true) + "." + this.file.type().substring(6);
	}
	
	public String getLink() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(file.get());
		} catch (IOException e) {
			e.printStackTrace();
		}//Router.getFullUrl("Application.renderImage")
		return "<img width=\""+img.getWidth()+"\" height=\""+img.getHeight()+"\" style=\"float: left; margin-right:20px;\" title=\"" + title + "\" alt=\"" + description + "\" src=\"" + getfullUrl() +"\">";
	}
}
