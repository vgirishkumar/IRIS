package com.temenos.interaction.core.command;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.StatusType;

public class NoopDELETECommand implements ResourceDeleteCommand, InteractionCommand {

	@Override
	public StatusType delete(String id) {
		return HttpStatusTypes.METHOD_NOT_ALLOWED;
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		ctx.setResource(null);
		return Result.SUCCESS;
	}

	@Override
	public String getMethod() {
		return HttpMethod.DELETE;
	}
};

