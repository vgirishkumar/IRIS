package com.temenos.interaction.commands.odata;

import javax.ws.rs.core.Response.Status;

import org.odata4j.core.OEntity;
import org.odata4j.exceptions.ODataProducerException;
import org.odata4j.producer.ODataProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

public class UpdateEntityCommand extends AbstractODataCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(UpdateEntityCommand.class);

	private ODataProducer producer;

	public UpdateEntityCommand(ODataProducer producer) {
		this.producer = producer;
	}

	/* Implement InteractionCommand interface */
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() != null);
		
		// update the entity
		String entityName = getEntityName(ctx);
		logger.debug("Getting entity for " + entityName);
		EntityResource<OEntity> entityResource = (EntityResource<OEntity>) ctx.getResource();
		try {
			producer.updateEntity(entityName, entityResource.getEntity());
		}
		catch(ODataProducerException ope) {
			logger.debug("Failed to update entity [" + entityName + "]: " + ope.getMessage());
			throw new InteractionException(ope.getHttpStatus(), ope.getMessage());
		}
		catch(Exception e) {
			logger.debug("Error while updating entity [" + entityName + "]: " + e.getMessage());
			throw new InteractionException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		ctx.setResource(null);
		return Result.SUCCESS;
	}

}
