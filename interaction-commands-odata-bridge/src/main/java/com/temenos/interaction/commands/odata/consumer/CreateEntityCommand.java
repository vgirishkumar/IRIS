package com.temenos.interaction.commands.odata.consumer;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OCreateRequest;
import org.odata4j.core.OEntity;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;

public class CreateEntityCommand implements InteractionCommand {

	private ODataConsumer consumer;

	public CreateEntityCommand(ODataConsumer consumer) {
		this.consumer = consumer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() != null);
		
		// create the entity
		EntityResource<OEntity> entityResource = (EntityResource<OEntity>) ctx.getResource();
		OEntity entity = entityResource.getEntity();
		
		OCreateRequest<OEntity> createRequest = consumer.createEntity(ctx.getCurrentState().getEntityName());
		if (entity != null){
			createRequest.properties(entity.getProperties());
		}
		
		/*
		 * Execute request
		 */
		OEntity newEntity = createRequest.execute();
		ctx.setResource(CommandHelper.createEntityResource(newEntity));
		return Result.SUCCESS;
	}

}
