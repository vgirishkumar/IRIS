package com.temenos.interaction.commands.odata;

import javax.ws.rs.core.MultivaluedMap;

import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InvalidRequestException;

public class GETEntitiesCommand implements InteractionCommand {
	private final Logger logger = LoggerFactory.getLogger(GETEntitiesCommand.class);

	private ODataProducer producer;
	private EdmDataServices edmDataServices;

	public GETEntitiesCommand(ODataProducer producer) {
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
	}

	/* Implement InteractionCommand interface */

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() == null);

		String entityName = ctx.getCurrentState().getEntityName();
		logger.debug("Getting entities for " + entityName);
		try {
			EdmEntitySet entitySet = CommandHelper.getEntitySet(entityName, edmDataServices);
			String entitySetName = entitySet.getName();

			MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
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
		      
			QueryInfo query = new QueryInfo(
					OptionsQueryParser.parseInlineCount(inlineCount),
					OptionsQueryParser.parseTop(top),
					OptionsQueryParser.parseSkip(skip),
					OptionsQueryParser.parseFilter(filter),
					OptionsQueryParser.parseOrderBy(orderBy),
					OptionsQueryParser.parseSkipToken(skipToken),
					null,
					OptionsQueryParser.parseExpand(expand),
					OptionsQueryParser.parseSelect(select));
			
			EntitiesResponse response = producer.getEntities(entitySetName, query);
			    
			CollectionResource<OEntity> cr = CommandHelper.createCollectionResource(entitySetName, response.getEntities());
			ctx.setResource(cr);
		}
		catch(InvalidRequestException ire) {
			logger.error("Failed to GET entities [" + entityName + "]: " + ire.getMessage());
			return Result.INVALID_REQUEST;
		}
		catch(Exception e) {
			logger.error("Failed to GET entities [" + entityName + "]: " + e.getMessage());
			return Result.FAILURE;
		}
		return Result.SUCCESS;
	}

}
