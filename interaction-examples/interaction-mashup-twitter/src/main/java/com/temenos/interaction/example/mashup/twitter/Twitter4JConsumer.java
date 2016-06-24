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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.temenos.interaction.example.mashup.twitter.model.Tweet;

public class Twitter4JConsumer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Twitter4JConsumer.class);

	private static final String CONSUMER_KEY = "QYUNmSke0Q3BEo58gnvw";
	private static final String CONSUMER_SECRET = "mkbaqfBZtAyOzpLR55XhKhrbgyAriQWN9FQoZQtV79U";	
	
	/**
	 * Empty constructor to be used by GETUserTwitterUpdatesCommand
	 */
	public Twitter4JConsumer() {
	    // Empty constructor to be used by GETUserTwitterUpdatesCommand
	}

	/**
	 * @param otherUser
	 * @return
	 */
	public Collection<Tweet> requestTweetsByUser(String otherUser) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		try {
			// The factory instance is re-useable and thread safe.
			Twitter twitter = new TwitterFactory().getInstance();
		    AccessToken accessToken = loadAccessToken(1);
		    twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		    twitter.setOAuthAccessToken(accessToken);
		    if (LOGGER.isInfoEnabled()) {
		        LOGGER.info("Fetching latest 100 tweets for [" + otherUser + "]");
		    }
		    // First param of Paging() is the page number, second is the number per page (this is capped around 200 I think.
		    Paging paging = new Paging(1, 100);
		    List<Status> statuses = twitter.getUserTimeline(otherUser, paging);
			for (Status status : statuses) {
				tweets.add(new Tweet(otherUser, status.getText(), (status.getGeoLocation() != null ? status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude() : "")));
				if (LOGGER.isInfoEnabled()) {
				    LOGGER.info(status.getUser().getName() + "(" + status.getGeoLocation() + "):" + status.getText());
				}
			}
		} catch (Exception e) {
		    LOGGER.error("Error on requestTweetsByUser", e);
			throw new TwitterMashupException(e);
		}
	    return tweets;
	}

	private static AccessToken loadAccessToken(int useId) throws IOException, ClassNotFoundException {
		File accessTokenStore = new File("/tmp", "Twitter4jAccessToken.ser");
		if (!accessTokenStore.exists())
			throw new TwitterMashupException("Access token not found, run OAuthRequester.main()");
		ObjectInputStream ois = null;
		AccessToken at = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(accessTokenStore));
			at = (AccessToken) ois.readObject();
		} catch (Exception e) {
		    LOGGER.error("Error reading the object", e);
		} finally {
			if(ois != null) {
			    ois.close();
			}
		}
		return at;
	}

}
