package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.elasticsearch.annotations.ElasticSearchIgnore;
import play.modules.elasticsearch.annotations.ElasticSearchable;
import play.templates.JavaExtensions;
import utils.StringUtils;

import com.petebevin.markdown.MarkdownProcessor;

@Entity
@ElasticSearchable
public class Post extends Model {
	
	@Required
	public String			title;

	public String			slugurl;
	
	@Required
	public Date				postedAt;
	
	@Lob
	@Required
	@MaxSize(10000)
	public String			content;
	
	@Lob
	@MaxSize(10000)
	public String			description;
	
	public String			gistId;
	
	@Lob
	@MaxSize(15000)
	public String			html_content;
	
	@Required
	@ManyToOne
	public User				author;
	
	@ElasticSearchIgnore
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	public List<Comment>	comments;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	public List<GistFile>	gistFiles;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag>			tags;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Keyword>		keywords;
	
	public Post(User author, String title, String content, String gistId) {
		this.comments = new ArrayList<Comment>();
		this.gistFiles = new ArrayList<GistFile>();
		this.tags = new TreeSet();
		this.keywords = new TreeSet();
		this.author = author;
		this.title = title;
		this.slugurl = JavaExtensions.slugify(title, true);
		this.content = content;
		this.postedAt = new Date();
		this.gistId = gistId;
		MarkdownProcessor m = new MarkdownProcessor();
		html_content = m.markdown(content);
	}
	
	public String getYear() {
		return Integer.toString(this.postedAt.getYear());
	}
	
	public String getMonth() {
		return Integer.toString(this.postedAt.getMonth());
	}
	
	public String getDay() {
		return Integer.toString(this.postedAt.getDay());
	}
	
	public Post addComment(String author, String content, String email, String url) {
		Comment newComment = new Comment(this, author, content, email, url);
		this.comments.add(newComment);
		this.save();
		return this;
	}
	
	public Post addGistFile(String gistId, String fileName, String fileContent, String url) {
		GistFile newGistFile = new GistFile(this, gistId, fileName, fileContent, url);
		this.gistFiles.add(newGistFile);
		this.save();
		return this;
	}
	
	public List<Comment> getComments() {
		return Comment.find("spam = ? and post_id = ? order by postedAt desc", false, this.id).fetch();
	}
	
	public Post previous() {
		return Post.find("postedAt < ? order by postedAt desc", postedAt).first();
	}
	
	public Post next() {
		return Post.find("postedAt > ? order by postedAt asc", postedAt).first();
	}
	
	public Post tagItWith(String name) {
		tags.add(Tag.findOrCreateByName(name));
		return this;
	}
	
	public static List<Post> findTaggedWith(String tag) {
		return Post.find("select distinct p from Post p join p.tags as t where t.name = ?", tag).fetch();
	}
	
	public static List<Post> findTaggedWith(String... tags) {
		return Post.find("select distinct p.id from Post p join p.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size").bind("tags", tags).bind("size", tags.length).fetch();
	}
	
	public Post keywordItWith(String name) {
		keywords.add(Keyword.findOrCreateByName(name));
		return this;
	}
	
	public static List<Post> findKeywordeddWith(String keyword) {
		return Post.find("select distinct p from Post p join p.keywords as t where t.name = ?", keyword).fetch();
	}
	
	public static List<Post> findKeywordedddWith(String... keywords) {
		return Post.find("select distinct p.id from Post p join p.keywords as t where t.name in (:tags) group by p.id having count(t.id) = :size").bind("keywords", keywords).bind("size", keywords.length).fetch();
	}
	
	public String toString() {
		return title;
	}
	
}
