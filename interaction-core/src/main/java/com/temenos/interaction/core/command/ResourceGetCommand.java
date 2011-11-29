package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;

import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.RESTResponse;

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
	 * @postcondition a valid {@link RESTResponse} will be returned and {@link RESTResponse#getStatus()} 
	 * will return a {@link Response.Status}.  A call to {@link RESTResponse#getResource()} will return
	 * a {@link RESTResource} if a the Response.Status is an OK (200) family of response
	 * @invariant return a non null Response.Status
	 */
	public RESTResponse get(String id);
	
}
