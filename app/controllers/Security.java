package controllers;

import models.*;

public class Security extends Secure.Security {

    static boolean authentify(String username, String password) {
        return User.connect(username, password) != null;
    }
    
    static boolean check(String profile) {
        if("admin".equals(profile)) {
            return User.find("byEmail", connected()).<User>first().isAdmin;
        }
        if("poster".equals(profile)) {
            return User.find("byEmail", connected()).<User>first().canPost;
        }
        if("posteroradmin".equals(profile)) {
            return (User.find("byEmail", connected()).<User>first().canPost || User.find("byEmail", connected()).<User>first().isAdmin);
        }
        
        
        return false;
    }
    
    static void onDisconnected() {
        Application.index();
    }
    
    static void onAuthenticated() {
        Admin.index();
    }
    
}

