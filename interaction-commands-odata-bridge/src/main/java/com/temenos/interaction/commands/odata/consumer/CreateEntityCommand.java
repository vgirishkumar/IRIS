package com.temenos.interaction.commands.odata.consumer;

/*
 * #%L
 * interaction-commands-odata-bridge
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


import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OCreateRequest;
import org.odata4j.core.OEntity;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;

public class CreateEntityCommand implements InteractionCommand {

	private ODataConsumer consumer;

	public CreateEntityCommand(ODataConsumer consumer) {
		this.consumer = consumer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() != null);
		
		// create the entity
		EntityResource<OEntity> entityResource = (EntityResource<OEntity>) ctx.getResource();
		OEntity entity = entityResource.getEntity();
		
		OCreateRequest<OEntity> createRequest = consumer.createEntity(ctx.getCurrentState().getEntityName());
		if (entity != null){
			createRequest.properties(entity.getProperties());
		}
		
		/*
		 * Execute request
		 */
		OEntity newEntity = createRequest.execute();
		ctx.setResource(CommandHelper.createEntityResource(newEntity));
		return Result.SUCCESS;
	}

}
