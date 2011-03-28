/**
 * 
 */
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

import com.petebevin.markdown.MarkdownProcessor;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.elasticsearch.annotations.ElasticSearchable;

/**
 * @author azrail
 *
 */
@Entity
@ElasticSearchable
public class Site extends Model {

    @Required
    public String title;
    
    @Required @As("yyyy-MM-dd")
    public Date postedAt;
    
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @Lob
    public String html_content;
    
    @Required
    @ManyToOne
    public User author;
    
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
