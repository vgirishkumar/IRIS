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
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class DeleteEntityCommand implements InteractionCommand {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteEntityCommand.class);

	private ODataConsumer consumer;
	private EdmDataServices edmDataServices;

	public DeleteEntityCommand(ODataConsumer consumer) {
		this.consumer = consumer;
		this.edmDataServices = consumer.getMetadata();
	}
	
	/* Implement ResourceDeleteCommand (OEntity) */
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

		// Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entity, ctx.getId());
		} catch(Exception e) {
		    LOGGER.warn("Failure to create the entity key.", e);
			return Result.FAILURE;
		}
		
		// delete the entity
		try {
			consumer.deleteEntity(entity, key).execute();
		} catch (Exception e) {
			LOGGER.warn("Failure to delete the entity because entity is not found.", e);
			return Result.FAILURE;
		}
		return Result.SUCCESS;
	}

	protected ODataConsumer getConsumer() {
		return consumer;
	}

}
