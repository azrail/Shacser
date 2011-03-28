package controllers;

import java.util.Date;

import models.Post;
import models.Site;
import models.Tag;
import models.User;

import com.petebevin.markdown.MarkdownProcessor;

import play.*;
import play.mvc.*;

@Check("admin")
@With(Secure.class)
public class Sites extends CRUD {
	
}