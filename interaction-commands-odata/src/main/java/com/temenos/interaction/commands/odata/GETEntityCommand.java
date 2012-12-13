package com.temenos.interaction.commands.odata;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class GETEntityCommand implements InteractionCommand {
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
		
		String entityName = ctx.getCurrentState().getEntityName();
		try {
			EdmEntitySet entitySet = CommandHelper.getEntitySet(entityName, edmDataServices);
			String entitySetName = entitySet.getName();

			Iterable<EdmEntityType> entityTypes = edmDataServices.getEntityTypes();
			
			//Create entity key (simple types only)
			OEntityKey key = CommandHelper.createEntityKey(entityTypes, entitySetName, ctx.getId());
			
			//Get the entity
			EntityResponse er = getProducer().getEntity(entitySetName, key, null);
			OEntity oEntity = er.getEntity();
			
			EntityResource<OEntity> oer = CommandHelper.createEntityResource(oEntity);
			ctx.setResource(oer);		
		}
		catch(Exception e) {
			logger.error("Failed to GET entity [" + entityName + "]: " + e.getMessage());
			return Result.FAILURE;
		}
		return Result.SUCCESS;
	}

}
