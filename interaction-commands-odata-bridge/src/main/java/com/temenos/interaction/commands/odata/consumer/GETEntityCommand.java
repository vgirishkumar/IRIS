package com.temenos.interaction.commands.odata.consumer;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;

public class GETEntityCommand implements InteractionCommand {

	// Command configuration
	private String entitySetName;
	
	private ODataConsumer consumer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;
	private Iterable<EdmEntityType> entityTypes;

	public GETEntityCommand(String entitySetName, ODataConsumer consumer) {
		this.entitySetName = entitySetName;
		this.consumer = consumer;
		this.edmDataServices = consumer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entitySetName);
		this.entityTypes = edmDataServices.getEntityTypes();
		assert(entitySetName.equals(entitySet.getName()));
	}
	
	/* Implement ResourceGetCommand (OEntity) */
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		//Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entitySetName, ctx.getId());
		} catch(Exception e) {
			return Result.FAILURE;
		}
		
		OEntityGetRequest<OEntity> request = getConsumer().getEntity(entitySetName, key);
		//request.expand(expand);
		//request.select(select);
		
		/*
		 * Execute request
		 */
		OEntity oEntity = request.execute();
		
		EntityResource<OEntity> oer = CommandHelper.createEntityResource(oEntity);
		ctx.setResource(oer);		
		return Result.SUCCESS;
	}

	protected ODataConsumer getConsumer() {
		return consumer;
	}
	
}
