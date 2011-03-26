package models;
 
import java.util.*;
import javax.persistence.*;

import com.google.gson.JsonObject;
 
import play.db.jpa.*;
import play.data.validation.*;
import play.mvc.Scope.Session;
 
@Entity
public class User extends Model {
 
    @Email
    @Required
    public String email;
    
    @Required
    public String password;
    
    public String fullname;
    
    public boolean isAdmin;
    
    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }
    
    public User(String email, String fullname) {
        this.email = email;
        this.fullname = fullname;
    }
    
    public static void facebookOAuthCallback(JsonObject data){
    	String email = data.get("email").getAsString();
    	String fullname = data.get("name").getAsString();
    	User user = findByEmail(email);
    	if(user == null){
    		user = new User(email, fullname);
    		user.save();
    	}
    	Session.current().put("user", user.email);
    }
    
    private static User findByEmail(String email) {
    	System.out.println(email);
    	return find("byEmail", email).first();
	}

	public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }
    
    public String toString() {
        return email;
    }
 
}