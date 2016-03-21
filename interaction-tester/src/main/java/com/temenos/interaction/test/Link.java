package com.temenos.interaction.test;

import com.temenos.interaction.test.internal.Payload;

/**
 * Defines a hypermedia link.
 * 
 * @author ssethupathi
 *
 */
public interface Link {

	/**
	 * Returns the title attribute value of this link.
	 * 
	 * @return title
	 */
	String title();

	/**
	 * Returns the href attribute value of this link.
	 * 
	 * @return href
	 */
	String href();

	/**
	 * Returns the rel attribute value of this link.
	 * 
	 * @return rel
	 */
	String rel();

	/**
	 * Returns the base url for this link.
	 * 
	 * @return base url
	 */
	String baseUrl();

	/**
	 * Returns the id of this link.
	 * 
	 * @return id
	 */
	String id();

	/**
	 * Returns whether or not this link has embedded payload.
	 * 
	 * @return true if this link has embedded payload, false otherwise
	 */
	boolean hasEmbeddedPayload();

	/**
	 * Returns the embedded payload.
	 * 
	 * @return embedded payload or null if this link has no embedded payload
	 */
	Payload embedded();

}
