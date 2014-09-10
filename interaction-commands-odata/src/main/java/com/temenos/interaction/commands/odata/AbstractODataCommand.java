package com.temenos.interaction.commands.odata;

/*
 * #%L
 * interaction-commands-odata
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


import javax.ws.rs.core.Response.Status;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.exceptions.NotFoundException;
import org.odata4j.producer.ODataProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.odataext.entity.MetadataOData4j;

public abstract class AbstractODataCommand {

	private final Logger logger = LoggerFactory.getLogger(AbstractODataCommand.class);
	/**
	 * Use this property to configure an action to use this entity 
	 * instead of the entity specified for the Resource.
	 */
	public final static String ENTITY_PROPERTY = "entity";
	protected ODataProducer producer; 
	private MetadataOData4j metadataOData4j;
	
	public AbstractODataCommand(ODataProducer producer) {
		this.producer = producer;
	}

	public AbstractODataCommand(MetadataOData4j metadataOData4j, ODataProducer producer) {
		this.producer = producer;
		this.metadataOData4j = metadataOData4j;
	}
	
	public String getEntityName(InteractionContext ctx) {
		String entityName = ctx.getCurrentState().getEntityName();
		// TODO improve this naive implementation, only using properties from first action
		Action action = null;
		if (ctx.getCurrentState().getActions().size() > 0)
			action = ctx.getCurrentState().getActions().get(0);
		
		if (action != null && action.getProperties() != null && action.getProperties().getProperty(ENTITY_PROPERTY) != null) {
			entityName = action.getProperties().getProperty(ENTITY_PROPERTY);
		}
		return entityName;
	}
	
	protected EdmDataServices getEdmMetadata() {
		return producer.getMetadata();
	}
	
	/**
	 * get EdmEntitySet from EdmDataServices
	 * @param entityName
	 * @return EdmEntitySet
	 * @throws Exception
	 */
	public EdmEntitySet getEdmEntitySet(String entityName) throws Exception {
		// We should try to get EdmEntitySet from MetadataOdata4j
		if (metadataOData4j != null) {
			try {
				EdmEntitySet entitySet = metadataOData4j.getEdmEntitySetByEntityName(entityName);
				if( null == entitySet ) {
					throw new Exception("Entity type does not exist");
				}
				return entitySet;
			} catch (Exception e) {
				throw new InteractionException(Status.BAD_REQUEST,"Entity type does not exist");
			}
		} else { // We fall back to default way of looking at EdmEntitySet
			return CommandHelper.getEntitySet(entityName, getEdmMetadata());
		}
	}

	/**
	 * get EdmEntitySetName from EdmEntitySet
	 * @param entityName
	 * @return entityName
	 * @throws Exception
	 */
	public String getEdmEntitySetName(String entityName) throws Exception {
		try {
			return getEdmEntitySet(entityName).getName();
		} catch (NotFoundException notFoundException) {
			notFoundException.printStackTrace();
			throw notFoundException;
		}
	}
}
