package com.temenos.interaction.core.command;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.resource.EntityResource;

public 	class NoopPOSTCommand implements InteractionCommand {

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
