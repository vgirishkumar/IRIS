package com.temenos.interaction.example.mashup.twitter.model;

public class Tweet {

	private String username = null;
	private String message = null;
	private String geolocation = null;
	
	public Tweet(String username, String message, String geolocation) {
		this.username = username;
		this.message = message;
		this.geolocation = geolocation;
	}
	
	public String getUsername() {
		return username;
	}

	public String getMessage() {
		return message;
	}

	public String getGeolocation() {
		return geolocation;
	}

	
}
