package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;

/**
 * A #ResourcePutCommand can be executed to replace a resource.
 * @author aphethean
 */
public interface ResourcePutCommand<RESOURCE> extends ResourceStateTransitionCommand {

	public Response.Status put(String id, RESOURCE resource);
	
}
