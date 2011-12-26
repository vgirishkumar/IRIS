package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.EntityResource;

/**
 * A #ResourcePutCommand can be executed to replace a resource.
 * @author aphethean
 */
public interface ResourcePutCommand extends ResourceStateTransitionCommand {

	public StatusType put(String id, EntityResource resource);
	
}
