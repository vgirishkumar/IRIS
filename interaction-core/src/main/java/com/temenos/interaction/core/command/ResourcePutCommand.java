package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.EntityResource;

/**
 * A #ResourcePutCommand can be executed to replace a resource.
 * @author aphethean
 */
public interface ResourcePutCommand extends ResourceStateTransitionCommand {

	/**
	 * Using the supplied id, PUT a resource to the concrete implementations provider.
	 * 
	 * @precondition id not null
	 * @precondition EntityResource not null
	 * @precondition root resource should be found in implementing classes provider
	 * @postcondition a valid {@link Response.Status} will be returned
	 */
	public StatusType put(String id, EntityResource<?> resource);
	
}
