package com.temenos.interaction.example.mashup.twitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.temenos.interaction.example.mashup.twitter.model.Tweet;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class Twitter4JConsumer {

	private final static String CONSUMER_KEY = "QYUNmSke0Q3BEo58gnvw";
	private final static String CONSUMER_SECRET = "mkbaqfBZtAyOzpLR55XhKhrbgyAriQWN9FQoZQtV79U";	
	
	public Twitter4JConsumer() {
	}
	
	public Collection<Tweet> requestTweetsByUser(String otherUser) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		try {
			// The factory instance is re-useable and thread safe.
			Twitter twitter = new TwitterFactory().getInstance();
		    AccessToken accessToken = loadAccessToken(1);
		    twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		    twitter.setOAuthAccessToken(accessToken);
			System.out.println("Fetching latest 100 tweets for [" + otherUser + "]");
		    // First param of Paging() is the page number, second is the number per page (this is capped around 200 I think.
		    Paging paging = new Paging(1, 100);
		    List<Status> statuses = twitter.getUserTimeline(otherUser, paging);
			for (Status status : statuses) {
				tweets.add(new Tweet(otherUser, status.getText(), (status.getGeoLocation() != null ? status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude() : "")));
			    System.out.println(status.getUser().getName() + "(" + status.getGeoLocation() + "):" +
			                       status.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	    return tweets;
	}

	private static AccessToken loadAccessToken(int useId) throws Exception {
		File accessTokenStore = new File("/tmp", "Twitter4jAccessToken.ser");
		if (!accessTokenStore.exists())
			throw new RuntimeException(
					"Access token not found, run OAuthRequester.main()");
		AccessToken at = (AccessToken) new ObjectInputStream(
				new FileInputStream(accessTokenStore)).readObject();
		return at;
	}

}
