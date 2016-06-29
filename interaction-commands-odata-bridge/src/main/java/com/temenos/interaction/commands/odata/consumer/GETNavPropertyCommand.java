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


import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OQueryRequest;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.EntitiesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class GETNavPropertyCommand implements InteractionCommand {
	private static final Logger LOGGER = LoggerFactory.getLogger(GETNavPropertyCommand.class);
	// Command configuration
	private String entitySetName;
	private String navProperty;
	
	private ODataConsumer consumer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;
	private Iterable<EdmEntityType> entityTypes;

	public GETNavPropertyCommand(String entitySetName, String navProperty, ODataConsumer consumer) {
		this.entitySetName = entitySetName;
		this.navProperty = navProperty;
		this.consumer = consumer;
		this.edmDataServices = consumer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entitySetName);
		this.entityTypes = edmDataServices.getEntityTypes();
		assert entitySetName.equals(entitySet.getName());
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		//Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entitySetName, ctx.getId());
		} catch(Exception e) {
		    LOGGER.warn("Failure to create the entity key.", e);
			return Result.FAILURE;
		}

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
		if (queryParams == null){
		    LOGGER.error("Query params null");
			return Result.FAILURE;
		}
		if (navProperty == null){
		    LOGGER.error("NavProperty null");
			return Result.FAILURE;
		}
		
		int top = getAsInt(queryParams.getFirst("$top"));
		int skip = getAsInt(queryParams.getFirst("$skip"));
		String filter = queryParams.getFirst("$filter");
		String orderBy = queryParams.getFirst("$orderby");
		String expand = queryParams.getFirst("$expand");
		String select = queryParams.getFirst("$select");

		/*
		 * Create request
		 */
		OQueryRequest<OEntity> request = consumer.getEntities(entitySetName);
		request
			.top(top)
			.skip(skip)
			.filter(filter)
			.orderBy(orderBy)
			.expand(expand)
			.select(select)
			.nav(key, navProperty);
		
		/*
		 * Handle custom options if any
		 */
		for (String name : queryParams.keySet()){
			String value = queryParams.getFirst(name);
			if (!name.startsWith("$")){
				request.custom(name, value);
			}
		}
		
		/*
		 * Execute request
		 */
		Enumerable<OEntity> response = request.execute();

		if (response != null){
			if (response.count() == 1) {
	        	OEntity oe = response.first();
	        	ctx.setResource(CommandHelper.createEntityResource(oe));
	        	return Result.SUCCESS;
	        } else if (response instanceof EntitiesResponse) {
	        	List<OEntity> entities = response.toList();
	        	ctx.setResource(CommandHelper.createCollectionResource(entitySetName, entities));
	        	return Result.SUCCESS;
	    	} else {
	    	    LOGGER.error("Other type of unsupported response from ODataProducer.getNavProperty");
	        }
		}
		return Result.FAILURE;
	}

	public static int getAsInt(String value){
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}	

}
