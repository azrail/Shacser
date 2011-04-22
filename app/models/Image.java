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

	public String getLink() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(file.get());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "<img width=\""+img.getWidth()+"\" height=\""+img.getHeight()+"\" style=\"float: left; margin-right:20px;\" title=\"" + title + "\" alt=\"" + description + "\" src=\""+Router.getFullUrl("Admin.renderImage")+"?id=" + id + "\">";
	}
}
