package com.temenos.interaction.core.command;

import com.temenos.interaction.core.resource.EntityResource;

public class NoopPUTCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		ctx.setResource(new EntityResource<Object>());
		return Result.SUCCESS;
	}

};

