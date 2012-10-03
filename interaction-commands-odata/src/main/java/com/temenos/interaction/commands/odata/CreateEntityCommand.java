package com.temenos.interaction.commands.odata;

import org.odata4j.core.OEntity;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class CreateEntityCommand implements InteractionCommand {

	private ODataProducer producer;

	public CreateEntityCommand(ODataProducer producer) {
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
		EntityResponse er = producer.createEntity(ctx.getCurrentState().getEntityName(), entityResource.getEntity());
		OEntity oEntity = er.getEntity();
		
		ctx.setResource(CommandHelper.createEntityResource(oEntity));
		return Result.SUCCESS;
	}

}
