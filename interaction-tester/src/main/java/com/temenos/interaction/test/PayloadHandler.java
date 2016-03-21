package com.temenos.interaction.test;

import java.util.List;

import com.temenos.interaction.test.internal.EntityWrapper;

/**
 * Defines a handler for the payload for a media type.
 * 
 * @author ssethupathi
 *
 */
public interface PayloadHandler {

	/**
	 * Returns whether or not this payload contains collection of {@link Entity
	 * entities}.
	 * 
	 * @return true for collection of {@link Entity entities}, false otherwise
	 */
	boolean isCollection();

	/**
	 * Returns available {@link Link links} from the payload.
	 * 
	 * @return {@link Link links}
	 */
	List<Link> links();

	/**
	 * Returns the {@link EntityWrapper entities} in the payload which is a
	 * collection type.
	 * 
	 * @return
	 */
	List<EntityWrapper> entities();

	/**
	 * Returns the single {@link EntityWrapper entity} in the payload which is
	 * an item type.
	 * 
	 * @return
	 */
	EntityWrapper entity();

	/**
	 * Sets the payload to the handler.
	 * 
	 * @param payload
	 */
	void setPayload(String payload);

	/**
	 * Sets any parameter part of the media type associated to this handler.
	 * <p>
	 * For example, <i>type=entry</i> is the parameter in the media type
	 * <i>application/atom+xml;type=entry</i> which will bet for handler to use
	 * in any processing.
	 * </p>
	 * 
	 * @param parameter
	 */
	void setParameter(String parameter);

}
