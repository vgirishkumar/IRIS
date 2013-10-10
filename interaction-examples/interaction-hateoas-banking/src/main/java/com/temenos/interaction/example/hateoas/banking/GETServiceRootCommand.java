package com.temenos.interaction.example.hateoas.banking;

/*
 * #%L
 * interaction-example-hateoas-banking
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
