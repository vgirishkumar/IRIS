package com.temenos.interaction.commands.odata.consumer;

import javax.ws.rs.core.MultivaluedMap;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OQueryRequest;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.CollectionResource;

public class GETEntitiesCommand implements InteractionCommand {
	private final Logger logger = LoggerFactory.getLogger(GETEntitiesCommand.class);

	private ODataConsumer consumer;
	private EdmDataServices edmDataServices;

	public GETEntitiesCommand(ODataConsumer consumer) {
		this.consumer = consumer;
		this.edmDataServices = consumer.getMetadata();
	}

	/* Implement InteractionCommand interface */

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() == null);

		String entitySetName = ctx.getCurrentState().getEntityName();
		logger.info("Getting entities for " + entitySetName);
		EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(entitySetName);
		if (entitySet == null)
			throw new RuntimeException("Entity set not found [" + entitySetName + "]");

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
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
			.select(select);
		
		/*
		 * Handle custom options if any
		 */
		for (String key : queryParams.keySet()){
			String value = queryParams.getFirst(key);
			if (!key.startsWith("$")){
				request.custom(key, value);
			}
		}
		
		/*
		 * Execute request
		 */
		Enumerable<OEntity> response = request.execute();
		    
		CollectionResource<OEntity> cr = CommandHelper.createCollectionResource(entitySetName, response.toList());
		ctx.setResource(cr);
		return Result.SUCCESS;
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
