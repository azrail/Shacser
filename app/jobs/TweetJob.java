package jobs;

import java.util.List;

import models.Tweet;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Every("30s")
public class TweetJob extends Job {
	public void doJob() {
		try {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey("plVnDxmytdmW4HJUZwQ03A").setOAuthConsumerSecret("JRirWTmypNeiWciylMefkdUszIatXQLqfJAbwSgVo").setOAuthAccessToken("275617481-A0N7Sb6HUhj8nXxko75fFDP6HxETSylROaYXvZ9z").setOAuthAccessTokenSecret("WE6MoRSD30eR96z7eLHyoPflJsK1tDTZl1ms5PxAA");
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();
			
			List<Status> statuses = twitter.getUserTimeline();
			for (Status status : statuses) {
				Tweet checktweet = Tweet.find("tweetId", status.getId()).first();
				if (checktweet == null) {
					Tweet tweet = new Tweet(status.getId(), status.getText(), status.getCreatedAt(), status.getUser().getName());
					Logger.debug("Tweet: %s", tweet.text + " -- " + tweet.tweetId + " --- " + tweet.createdAt + " --- " + tweet.user);
					tweet.save();
				}
			}
		} catch (TwitterException e) {
			Logger.error(e, "TwitterException: %s", e.getLocalizedMessage());
		}
		
	}
}
