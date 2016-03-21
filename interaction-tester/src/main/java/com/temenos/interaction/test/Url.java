package com.temenos.interaction.test;

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
