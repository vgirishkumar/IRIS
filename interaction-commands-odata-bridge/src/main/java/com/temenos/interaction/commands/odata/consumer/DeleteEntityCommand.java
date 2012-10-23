package com.temenos.interaction.commands.odata.consumer;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class DeleteEntityCommand implements InteractionCommand {

	private ODataConsumer consumer;
	private EdmDataServices edmDataServices;

	public DeleteEntityCommand(ODataConsumer consumer) {
		this.consumer = consumer;
		this.edmDataServices = consumer.getMetadata();
	}
	
	/* Implement ResourceDeleteCommand (OEntity) */
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() == null);
		
		String entity = ctx.getCurrentState().getEntityName();
		EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(entity);
		if (entitySet == null)
			throw new RuntimeException("Entity set not found [" + entity + "]");
		Iterable<EdmEntityType> entityTypes = edmDataServices.getEntityTypes();
		assert(entity.equals(entitySet.getName()));

		// Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entity, ctx.getId());
		} catch(Exception e) {
			return Result.FAILURE;
		}
		
		// delete the entity
		try {
			consumer.deleteEntity(entity, key).execute();
		} catch (Exception e) {
			// exception if the entity is not found, delete the entity if it exists;
		}
		return Result.SUCCESS;
	}

	protected ODataConsumer getConsumer() {
		return consumer;
	}

}
