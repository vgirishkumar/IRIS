package com.temenos.interaction.commands.odata;

import org.odata4j.core.OEntity;
import org.odata4j.producer.ODataProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class UpdateEntityCommand extends AbstractODataCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(UpdateEntityCommand.class);

	private ODataProducer producer;

	public UpdateEntityCommand(ODataProducer producer) {
		this.producer = producer;
	}

	/* Implement InteractionCommand interface */
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() != null);
		
		// update the entity
		String entityName = getEntityName(ctx);
		logger.debug("Getting entity for " + entityName);
		EntityResource<OEntity> entityResource = (EntityResource<OEntity>) ctx.getResource();
		producer.updateEntity(entityName, entityResource.getEntity());
		
		ctx.setResource(null);
		return Result.SUCCESS;
	}

}
