package com.temenos.interaction.core.command;

import java.util.Set;

import javax.ws.rs.core.Response;

import com.temenos.interaction.core.RESTResource;

/**
 * A #ResourceGetCommand can be executed to retrieve a resource.
 * @author aphethean
 */
public interface ResourceGetCommand extends ResourceCommand {

	/**
	 * Using the supplied id, GET a resource from the concrete implementations provider.
	 * 
	 * @precondition id not null
	 * @precondition id of resource should be found in implementing classes provider
	 * @postcondition a valid Response.Status and {@link #getResource()} will return
	 * a RESTResource if a the Response.Status is an OK (200) family of response
	 * @invariant return a non null Response.Status
	 */
	public Response.Status get(String id);
	public RESTResource getResource();
	public Set<String> getValidNextStates();
	
}
