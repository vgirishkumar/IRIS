package com.temenos.interaction.test;

import com.temenos.interaction.test.internal.EntityWrapper;

/**
 * Defines a session through which interactions to an IRIS service are
 * performed.
 * 
 * @author ssethupathi
 *
 */
public interface InteractionSession {

	/**
	 * Returns the header value for the name from the current session.
	 * 
	 * 
	 * @param name
	 *            of the header property
	 * @return value of the header property
	 */
	String header(String name);

	/**
	 * Returns the {@link Links links} associated to this session.
	 * 
	 * @return {@link Links links}
	 */
	Links links();

	/**
	 * Returns the {@link Entity entity} associated to this session.
	 * 
	 * @return {@link Entity entity}
	 */
	Entity entity();

	/**
	 * Returns the {@link Entities entities} associated to this session.
	 * 
	 * @return {@link Entities entities}
	 */
	Entities entities();

	/**
	 * Returns the {@link Result result} of the last interaction performed
	 * through this session.
	 * 
	 * @return {@link Result result}
	 */
	Result result();

	/**
	 * Sets a header name-values pair to this session.
	 * 
	 * @param name
	 *            of the header property
	 * @param values
	 *            to the header property
	 * @return this session
	 */
	InteractionSession header(String name, String... values);

	/**
	 * Registers a {@link PayloadHandler payload handler} to handle the payload
	 * for a media type.
	 * 
	 * @param mediaType
	 *            of the payload
	 * @param handler
	 *            to handle the payload of the media type
	 * @return this session
	 */
	InteractionSession registerHandler(String mediaType,
			Class<? extends PayloadHandler> handler);

	/**
	 * Sets the value to the property in the {@link Entity entity} associated to
	 * this session.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @return this session
	 */
	InteractionSession set(String propertyName, String propertyValue);

	/**
	 * Returns a {@link Url url} instance associated to this session with any
	 * pre-configuration applied.
	 * 
	 * @return a {@link Url url} instance
	 */
	Url url();

	/**
	 * Returns a {@link Url url} instance associated to this session with the
	 * set url string.
	 * 
	 * @param completeUrl
	 * @return a {@link Url url} instance
	 */
	Url url(String completeUrl);

	/**
	 * Enables reuse of this session for future interactions with the
	 * {@link Entity entity} from the recently completed interaction.
	 * 
	 * @return this session
	 */
	InteractionSession reuse();

	/**
	 * Uses the given {@link Entity entity} for future interactions in this
	 * session.
	 * 
	 * @param {@link Entity entity} to be used in future interactions
	 * @return this session
	 */
	InteractionSession use(EntityWrapper entity);

	/**
	 * Clears this session to reset to it's creation state.
	 * 
	 * @return this session
	 */
	InteractionSession clear();

	/**
	 * Sets user name for the Http Basic Authentication.
	 * 
	 * @param username
	 * @return this session
	 */
	InteractionSession basicAuth(String username, String password);
}
