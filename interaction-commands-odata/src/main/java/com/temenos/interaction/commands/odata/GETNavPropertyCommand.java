package com.temenos.interaction.commands.odata;

import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MultivaluedMap;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.BaseResponse;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.PropertyResponse;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class GETNavPropertyCommand implements InteractionCommand {
	private final Logger logger = LoggerFactory.getLogger(GETNavPropertyCommand.class);

	private ODataProducer producer;
	private EdmDataServices edmDataServices;

	public GETNavPropertyCommand(ODataProducer producer) {
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getViewAction() != null);
		assert(ctx.getResource() == null);
		
		Properties properties = ctx.getCurrentState().getViewAction().getProperties();
		if (properties == null || properties.get("entity") == null)
			throw new IllegalArgumentException("'entity' must be provided");
		String entity = (String) properties.get("entity");
		EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(entity);
		if (entitySet == null)
			throw new RuntimeException("Entity set not found [" + entity + "]");
		assert(entity.equals(entitySet.getName()));

		//Obtain the navigation property
		if(properties.get("navproperty") == null) {
			throw new IllegalArgumentException("Command must be bound to an OData navigation property resource");
		}
		String navProperty = (String) properties.get("navproperty");
		
		//Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(edmDataServices, entity, ctx.getId());
		} catch(Exception e) {
			return Result.FAILURE;
		}

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
		QueryInfo query = null;
		if (queryParams != null) {
			String inlineCount = queryParams.getFirst("$inlinecount");
			String top = queryParams.getFirst("$top");
			String skip = queryParams.getFirst("$skip");
			String filter = queryParams.getFirst("$filter");
			String orderBy = queryParams.getFirst("$orderby");
	// TODO what are format and callback used for
//			String format = queryParams.getFirst("$format");
//			String callback = queryParams.getFirst("$callback");
			String skipToken = queryParams.getFirst("$skiptoken");
			String expand = queryParams.getFirst("$expand");
			String select = queryParams.getFirst("$select");

			query = new QueryInfo(
					OptionsQueryParser.parseInlineCount(inlineCount),
					OptionsQueryParser.parseTop(top),
					OptionsQueryParser.parseSkip(skip),
					OptionsQueryParser.parseFilter(filter),
					OptionsQueryParser.parseOrderBy(orderBy),
					OptionsQueryParser.parseSkipToken(skipToken),
					null,
					OptionsQueryParser.parseExpand(expand),
					OptionsQueryParser.parseSelect(select));
		}

		BaseResponse response = producer.getNavProperty(entity, key, navProperty, query);

		if (response instanceof PropertyResponse) {
			logger.error("We don't currently support the ability to get an item property");
		} else if (response instanceof EntityResponse) {
        	OEntity oe = ((EntityResponse) response).getEntity();
        	ctx.setResource(CommandHelper.createEntityResource(oe));
        	return Result.SUCCESS;
        } else if (response instanceof EntitiesResponse) {
        	List<OEntity> entities = ((EntitiesResponse) response).getEntities();
        	ctx.setResource(CommandHelper.createCollectionResource(entity, entities));
        	return Result.SUCCESS;
    	} else {
			logger.error("Other type of unsupported response from ODataProducer.getNavProperty");
        }

		return Result.FAILURE;
	}

}
