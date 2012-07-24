package com.temenos.interaction.core.command;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.resource.EntityResource;

public 	class NoopPOSTCommand implements ResourcePostCommand, InteractionCommand {

	@Override
	public RESTResponse post(String id, EntityResource<?> resource) {
		return new RESTResponse(HttpStatusTypes.METHOD_NOT_ALLOWED, new EntityResource<String>(""));
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
		return HttpMethod.POST;
	}
	
};
