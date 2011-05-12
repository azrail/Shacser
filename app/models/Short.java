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

import org.elasticsearch.common.joda.Joda;

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
public class Short extends Model {
	
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
	
	@Lob
	@MaxSize(15000)
	public String			html_content;
	
	@Required
	@ManyToOne
	public User				author;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag>			tags;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Keyword>		keywords;
	
	public Short(User author, String title, String content, String gistId) {
		this.tags = new TreeSet();
		this.keywords = new TreeSet();
		this.author = author;
		this.title = title;
		this.slugurl = JavaExtensions.slugify(title, true);
		this.content = content;
		this.postedAt = new Date();
		MarkdownProcessor m = new MarkdownProcessor();
		html_content = m.markdown(content);
	}
	
	public String getYear() {

		return JavaExtensions.format(this.postedAt,"yyyy");
	}
	
	public String getMonth() {
		return JavaExtensions.format(this.postedAt,"MM");
	}
	
	public String getDay() {
		return JavaExtensions.format(this.postedAt,"dd");
	}
	
	public Short previous() {
		return Short.find("postedAt < ? order by postedAt desc", postedAt).first();
	}
	
	public Short next() {
		return Short.find("postedAt > ? order by postedAt asc", postedAt).first();
	}
	
	public Short tagItWith(String name) {
		tags.add(Tag.findOrCreateByName(name));
		return this;
	}
	
	public static List<Short> findTaggedWith(String tag) {
		return Short.find("select distinct p from Short p join p.tags as t where t.name = ?", tag).fetch();
	}

	public static List<Short> findTaggedWith(String... tags) {
		return Short.find("select distinct p.id from Short p join p.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size").bind("tags", tags).bind("size", tags.length).fetch();
	}

	public Short keywordItWith(String name) {
		keywords.add(Keyword.findOrCreateByName(name));
		return this;
	}

	public static List<Short> findKeywordeddWith(String keyword) {
		return Short.find("select distinct p from Short p join p.keywords as t where t.name = ?", keyword).fetch();
	}

	public static List<Short> findKeywordedddWith(String... keywords) {
		return Short.find("select distinct p.id from Short p join p.keywords as t where t.name in (:tags) group by p.id having count(t.id) = :size").bind("keywords", keywords).bind("size", keywords.length).fetch();
	}

	public String toString() {
		return title;
	}
	
}
