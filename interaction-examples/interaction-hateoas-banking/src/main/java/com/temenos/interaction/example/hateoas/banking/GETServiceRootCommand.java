package com.temenos.interaction.example.hateoas.banking;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;

/**
 * Implement GET service root.  (NOTE - this is identical to {@link com.temenos.interaction.core.command.NoopGETCommand}
 * @author aphethean
 */
public class GETServiceRootCommand implements InteractionCommand {

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		EntityResource<Object> resource = new EntityResource<Object>(null);
		ctx.setResource(resource);
		return Result.SUCCESS;
	}

}
