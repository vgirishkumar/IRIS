package com.temenos.interaction.core.command;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;

/**
 * A GET command that does nothing.  Can be useful for laying out a straw
 * man of resources and not needing to implement them all initially.
 * @author aphethean
 */
public final class NoopGETCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		RESTResource resource = ctx.getResource();
		if(resource != null) {
			ctx.setResource(resource);
		}
		else {
			ctx.setResource(new EntityResource<Object>());
		}
		return Result.SUCCESS;
	}

}
