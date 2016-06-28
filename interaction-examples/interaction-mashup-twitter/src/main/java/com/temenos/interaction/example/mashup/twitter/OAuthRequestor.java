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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuthRequestor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthRequestor.class);

	/* The twitter dev tokens */
	private final static String CONSUMER_KEY = "QYUNmSke0Q3BEo58gnvw";
	private final static String CONSUMER_SECRET = "mkbaqfBZtAyOzpLR55XhKhrbgyAriQWN9FQoZQtV79U";	

	public static void main(String args[]) throws Exception {
		// The factory instance is re-useable and thread safe.
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Open the following URL and grant access to your account:");
			    LOGGER.debug(requestToken.getAuthorizationURL());
			    LOGGER.debug("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			}
			
			String pin = br.readLine();
			try {
				if (pin.length() > 0) {
					accessToken = twitter
							.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode() && LOGGER.isInfoEnabled()) {
				    LOGGER.info("Unable to get the access token.");
				} else {
				    LOGGER.error("Error writing the object.", te);
				}
			}
		}
		// persist to the accessToken for future reference.
		storeAccessToken(twitter.verifyCredentials().getId(), accessToken);

// Tweet!		
//		Status status = twitter.updateStatus(args[0]);
//		System.out.println("Successfully updated the status to [" + status.getText() + "].");
		System.exit(0);
	}

	private static void storeAccessToken(long useId, AccessToken accessToken) throws IOException {
	    if (LOGGER.isInfoEnabled()) {
	        LOGGER.info(accessToken.getToken() + " " + accessToken.getTokenSecret());
	    }
		File accessTokenStore = new File("/tmp", "Twitter4jAccessToken.ser");
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(new FileOutputStream(accessTokenStore));
			os.writeObject(accessToken);
		} catch (Exception e) {
		    LOGGER.error("Error writing the object.", e);
		} finally {
			if (os != null)
				os.close();
		}
	}
}
