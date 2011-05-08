package tags;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;

@FastTags.Namespace("helper") 
public class HelpTags extends FastTags {
	public static void _genPostLink(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
		System.out.println(args);
	}
}
