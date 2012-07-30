package com.temenos.interaction.core.command;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.resource.EntityResource;

public class NoopPUTCommand implements ResourcePutCommand, InteractionCommand {

	@Override
	public StatusType put(String id, EntityResource<?> resource) {
		return HttpStatusTypes.METHOD_NOT_ALLOWED;
	}
	
	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		ctx.setResource(new EntityResource<Object>());
		return Result.SUCCESS;
	}

	@Override
	public String getMethod() {
		return HttpMethod.PUT;
	}
	
};

