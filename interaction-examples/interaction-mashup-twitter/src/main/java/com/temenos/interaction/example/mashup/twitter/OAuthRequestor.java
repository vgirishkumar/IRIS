package com.temenos.interaction.example.mashup.twitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuthRequestor {

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
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out
					.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try {
				if (pin.length() > 0) {
					accessToken = twitter
							.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
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

	private static void storeAccessToken(long useId, AccessToken accessToken) throws Exception {
		System.out.println(accessToken.getToken() + " " + accessToken.getTokenSecret());
		File accessTokenStore = new File("/tmp", "Twitter4jAccessToken.ser");
		new ObjectOutputStream(new FileOutputStream(accessTokenStore)).writeObject(accessToken);
	}
}
