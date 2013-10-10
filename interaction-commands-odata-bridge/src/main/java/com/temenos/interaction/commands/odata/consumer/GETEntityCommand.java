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
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;

public class GETEntityCommand implements InteractionCommand {

	private ODataConsumer consumer;
	private EdmDataServices edmDataServices;

	public GETEntityCommand(ODataConsumer consumer) {
		this.consumer = consumer;
		this.edmDataServices = consumer.getMetadata();
	}
	
	/* Implement ResourceGetCommand (OEntity) */
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() == null);
		
		String entity = ctx.getCurrentState().getEntityName();
		EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(entity);
		if (entitySet == null)
			throw new RuntimeException("Entity set not found [" + entity + "]");
		Iterable<EdmEntityType> entityTypes = edmDataServices.getEntityTypes();
		assert(entity.equals(entitySet.getName()));

		//Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entity, ctx.getId());
		} catch(Exception e) {
			return Result.FAILURE;
		}
		
		OEntityGetRequest<OEntity> request = getConsumer().getEntity(entity, key);
		//request.expand(expand);
		//request.select(select);
		
		/*
		 * Execute request
		 */
		OEntity oEntity = request.execute();
		
		EntityResource<OEntity> oer = CommandHelper.createEntityResource(oEntity);
		ctx.setResource(oer);		
		return Result.SUCCESS;
	}

	protected ODataConsumer getConsumer() {
		return consumer;
	}
	
}
