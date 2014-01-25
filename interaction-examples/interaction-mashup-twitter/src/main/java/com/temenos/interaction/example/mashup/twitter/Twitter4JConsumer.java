package com.temenos.interaction.example.mashup.twitter;

/*
 * #%L
 * interaction-example-mashup-twitter
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.temenos.interaction.example.mashup.twitter.model.Tweet;

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
