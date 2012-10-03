package com.temenos.interaction.core.command;

import com.temenos.interaction.core.resource.EntityResource;

/**
 * A command that does nothing.  Can be useful for laying out a straw
 * man of resources and not needing to implement them all initially.
 * @author aphethean
 */
public class NoopInteractionCommand implements InteractionCommand {

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		ctx.setResource(new EntityResource<Object>());
		return Result.SUCCESS;
	}

}
