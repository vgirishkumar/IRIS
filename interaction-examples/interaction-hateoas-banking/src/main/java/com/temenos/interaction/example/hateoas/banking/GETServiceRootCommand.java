package com.temenos.interaction.example.hateoas.banking;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.entity.Entity;

/**
 * Implement GET service root.  (NOTE - this is identical to {@link com.temenos.interaction.core.command.NoopGETCommand}
 * @author aphethean
 */
public class GETServiceRootCommand implements InteractionCommand {

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		EntityResource<Entity> resource = createEntityResource(null);
		resource.setLinks(ctx.getResource().getLinks());
		ctx.setResource(resource);
		return Result.SUCCESS;
	}

	@SuppressWarnings("hiding")
	public static<Entity> EntityResource<Entity> createEntityResource(Entity e) 
	{
		return new EntityResource<Entity>(e) {};	
	}
}
