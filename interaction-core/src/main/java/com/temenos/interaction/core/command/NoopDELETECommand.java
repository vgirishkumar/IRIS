package com.temenos.interaction.core.command;

public class NoopDELETECommand implements InteractionCommand {

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		ctx.setResource(null);
		return Result.SUCCESS;
	}

}

