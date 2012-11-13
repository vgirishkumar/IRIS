package com.temenos.interaction.commands.odata;

import org.odata4j.core.OEntity;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class UpdateEntityCommand implements InteractionCommand {

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
		
		// create the entity
		EntityResource<OEntity> entityResource = (EntityResource<OEntity>) ctx.getResource();
		producer.updateEntity(ctx.getCurrentState().getEntityName(), entityResource.getEntity());
		
		ctx.setResource(null);
		return Result.SUCCESS;
	}

}
