package models;
 
import java.util.*;
import javax.persistence.*;

import com.google.gson.JsonObject;
 
import play.db.jpa.*;
import play.data.validation.*;
import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.mvc.Scope.Session;
 
@Entity
public class User extends Model {
 
    @Email
    @Required
    public String email;
    
    @Required
    public String password;
    
    public String firstName;
    public String lastName;
    public String gender;
    
    public String fullname;
    
    
    public boolean isAdmin;
    public boolean canPost;
    
    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }
    
    public User(String firstName, String lastName, String email, String gender) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullname = firstName + " " + lastName;
        this.gender = gender;
    }
        
    public static User findByEmail(String email) {
    	return find("byEmail", email).first();
	}

	public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }
    
    public String toString() {
        return email;
    }
 
}