package models;
 
import javax.persistence.Entity;

import play.Logger;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.mvc.Scope.Session;

import com.google.gson.JsonObject;
 
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
    
    public static void facebookOAuthCallback(JsonObject data){
    	
    	String email = data.get("email").getAsString();    	
    	String firstName = data.get("first_name").getAsString();
    	String lastName = data.get("last_name").getAsString();
    	String gender = data.get("gender").getAsString();

    	
    	System.out.println(data);
    	
    	Logger.debug("Facebook: %s", email + " -- " + firstName + " " + lastName + " -- " + gender);
    	
    	User finguser = findByEmail(email);
    	if(finguser == null){
    		User user = new User(firstName, lastName, email, gender);
    		user.save();
    		Session.current().put("user", user.email);
    	} else {
    		Session.current().put("user", finguser.email);
		}
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