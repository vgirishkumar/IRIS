package com.temenos.interaction.test;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


/**
 * Defines a Http URL with convenient methods to execute Http actions on it.
 * 
 * @author ssethupathi
 *
 */
public interface Url {

	/**
	 * Returns the string representation of the URL.
	 * 
	 * @return URL.
	 */
	String url();

	/**
	 * Sets the base URI section of the URL which points up to the service root.
	 * 
	 * @param baseUri
	 * @return this {@link Url url}
	 */
	Url baseuri(String baseUri);

	/**
	 * Sets the path section of the URL.
	 * 
	 * @param path
	 * @return this {@link Url url}
	 */

	Url path(String path);

	/**
	 * Sets the encoded query parameters to be part of the URL.
	 * 
	 * @param queryParam
	 * @return this {@link Url url}
	 */
	Url queryParam(String queryParam);

	/**
	 * Defines that this {@link Url url} does not contain any request payload.
	 * 
	 * @return this {@link Url url}
	 */
	Url noPayload();

	/**
	 * Executes Http GET operation.
	 */
	void get();

	/**
	 * Executes Http POST operation.
	 */
	void post();

	/**
	 * Executes Http PUT operation.
	 */
	void put();
}
