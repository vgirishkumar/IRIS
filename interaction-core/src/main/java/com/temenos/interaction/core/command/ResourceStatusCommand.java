package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;

/**
 * A command when there is no resource, but we have a status.
 * @author aphethean
 */
public interface ResourceStatusCommand extends ResourceCommand {

	public Response.StatusType getStatus();
}
