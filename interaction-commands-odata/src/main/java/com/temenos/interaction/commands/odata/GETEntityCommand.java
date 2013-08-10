package com.temenos.interaction.commands.odata;

import javax.ws.rs.core.MultivaluedMap;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.exceptions.BadRequestException;
import org.odata4j.exceptions.NotAuthorizedException;
import org.odata4j.exceptions.NotFoundException;
import org.odata4j.producer.EntityQueryInfo;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.resources.OptionsQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.GenericError;
import com.temenos.interaction.core.resource.EntityResource;

public class GETEntityCommand extends AbstractODataCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(GETEntityCommand.class);

	private ODataProducer producer;
	private EdmDataServices edmDataServices;

	public GETEntityCommand(ODataProducer producer) {
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
	}
	
	protected ODataProducer getProducer() {
		return producer;
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() == null);
		
		String entityName = getEntityName(ctx);
		logger.debug("Getting entity for " + entityName);
		try {
			EdmEntitySet entitySet = CommandHelper.getEntitySet(entityName, edmDataServices);
			String entitySetName = entitySet.getName();

			//Create entity key (simple types only)
			OEntityKey key = CommandHelper.createEntityKey(edmDataServices, entitySetName, ctx.getId());
			
			//Get the entity
			EntityResponse er = getProducer().getEntity(entitySetName, key, getEntityQueryInfo(ctx));
			
			EntityResource<OEntity> oer = CommandHelper.createEntityResource(er.getEntity());
			ctx.setResource(oer);		
		}
		catch(NotAuthorizedException nae) {
			logger.debug("Access to resource [" + entityName + ", " + ctx.getId() + "] not allowed: " + nae.getMessage());
			ctx.setResource(CommandHelper.createGenericErrorResource(entityName, new GenericError(Result.AUTHORISATION_FAILURE.toString(), nae.getMessage())));
			return Result.AUTHORISATION_FAILURE;
		}
		catch(NotFoundException nfe) {
			logger.debug("Entity not found [" + entityName + ", " + ctx.getId() + "]: " + nfe.getMessage());
			ctx.setResource(CommandHelper.createGenericErrorResource(entityName, new GenericError(Result.RESOURCE_UNAVAILABLE.toString(), nfe.getMessage())));
			return Result.RESOURCE_UNAVAILABLE;
		}
		catch(BadRequestException bre) {
			logger.debug("Invalid request: " + bre.getMessage());
			ctx.setResource(CommandHelper.createGenericErrorResource(entityName, new GenericError(Result.INVALID_REQUEST.toString(), bre.getMessage())));
			return Result.INVALID_REQUEST;
		}
		catch(Exception e) {
			logger.error("Failed to GET entity [" + entityName + ", " + ctx.getId() + "]: " + e.getMessage());
			ctx.setResource(CommandHelper.createGenericErrorResource(entityName, new GenericError(Result.FAILURE.toString(), e.getMessage())));
			return Result.FAILURE;
		}
		return Result.SUCCESS;
	}

	/*
	 * Obtain the odata query information from the context's query parameters
	 * @param ctx interaction context
	 * @return query details
	 */
	private EntityQueryInfo getEntityQueryInfo(InteractionContext ctx) {
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
		String actionFilter = CommandHelper.getViewActionProperty(ctx, "filter");		//Filter defined as action property 
		String filter = queryParams.getFirst("$filter");								//Query filter
		if(actionFilter != null && !actionFilter.isEmpty()) {
			filter = filter != null ? filter + " and " + actionFilter : actionFilter;
		}
		String expand = queryParams.getFirst("$expand");
		String select = queryParams.getFirst("$select");
	      
		return new EntityQueryInfo(
				OptionsQueryParser.parseFilter(filter),
				null,
				OptionsQueryParser.parseExpand(expand),
				OptionsQueryParser.parseSelect(select));		
	}
}
