package com.temenos.interaction.example.mashup.twitter.model;

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
