package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

// @ElasticSearchable
@Entity
public class GistFile extends Model {
	
	@Required
	public String	gistId;
	
	@Required
	public String	fileName;
	
	@Lob
	@Required
	public String	fileContent;
	
	@ManyToOne
	@Required
	public Post		post;
	
	public String	url;
	
	public GistFile(Post post, String gistId, String fileName, String fileContent, String url) {
		this.post = post;
		this.gistId = gistId;
		this.fileName = fileName;
		this.url = url;
		this.fileContent = fileContent;
	}
	
	public String toString() {
		return fileContent.length() > 50 ? fileContent.substring(0, 50) + "..." : fileContent;
	}
	
}