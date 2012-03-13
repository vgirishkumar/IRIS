package com.temenos.interaction.core.resource;

import javax.ws.rs.core.GenericEntity;

/**
 * A RESTResource is the base interface for all types of resources.
 * 
 * @author aphethean
 */
public interface RESTResource {
	/**
	 * Wrap this resource into a JAX-RS GenericEntity object
	 * @return GenericEntity object
	 */
	public GenericEntity<?> getGenericEntity();
}
