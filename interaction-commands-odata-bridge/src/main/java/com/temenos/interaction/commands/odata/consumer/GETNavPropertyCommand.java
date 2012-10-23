package com.temenos.interaction.commands.odata.consumer;

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
	private final Logger logger = LoggerFactory.getLogger(GETNavPropertyCommand.class);
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
		assert(entitySetName.equals(entitySet.getName()));
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
			return Result.FAILURE;
		}

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
		if (queryParams == null){
			logger.error("Query params null");
			return Result.FAILURE;
		}
		if (navProperty == null){
			logger.error("NavProperty null");
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
				logger.error("Other type of unsupported response from ODataProducer.getNavProperty");
	        }
		}
		return Result.FAILURE;
	}

	public static int getAsInt(String value){
		try {
			int result = Integer.parseInt(value);
			return result;
		} catch (NumberFormatException e) {
			return 0;
		}
	}	

}
