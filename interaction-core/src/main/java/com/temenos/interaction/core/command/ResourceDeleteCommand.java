package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

/**
 * A #ResourceDeleteCommand can be executed to delete a resource.
 * @author aphethean
 */
public interface ResourceDeleteCommand extends ResourceStateTransitionCommand {

	/**
	 * Using the supplied id, DELETE a resource from the concrete implementations provider.
	 * 
	 * @precondition id not null
	 * @precondition id of resource should be found in implementing classes provider
	 * @postcondition a valid {@link Response.Status} will be returned and if there is
	 * no fatal error in the provider, the Response.Status should be an OK (200) family 
	 * of response whether the resource was actually deleted or not.
	 */
	public StatusType delete(String id);
	
}
