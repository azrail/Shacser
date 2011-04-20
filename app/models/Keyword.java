package models;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Keyword extends Model implements Comparable<Keyword> {
	
	@Required
	public String	name;
	
	private Keyword(String name) {
		this.name = name;
	}
	
	public static Keyword findOrCreateByName(String name) {
		Keyword keyword = Keyword.find("byName", name).first();
		if (keyword == null) {
			keyword = new Keyword(name);
		}
		return keyword;
	}
	
	public static List<Map> getCloud() {
		List<Map> result = Keyword.find("select new map(t.name as keyword, count(p.id) as pound) from Post p join p.keywords as t group by t.name").fetch();
		return result;
	}
	
	public String toString() {
		return name;
	}
	
	public int compareTo(Keyword otherkeyword) {
		return name.compareTo(otherkeyword.name);
	}
	
}