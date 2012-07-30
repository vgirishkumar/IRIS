package com.temenos.interaction.commands.odata;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.ResourceDeleteCommand;

public class DeleteEntityCommand implements ResourceDeleteCommand, InteractionCommand {

	// Command configuration
	private String entity;
	
	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;
	private Iterable<EdmEntityType> entityTypes;

	public DeleteEntityCommand(String entity, ODataProducer producer) {
		this.entity = entity;
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entity);
		this.entityTypes = edmDataServices.getEntityTypes();
		assert(entity.equals(entitySet.getName()));
	}
	
	/* Implement ResourceDeleteCommand (OEntity) */
	public StatusType delete(String id) {
		// Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entity, id);
		} catch(Exception e) {
			return Response.Status.NOT_ACCEPTABLE;
		}
		
		// delete the entity
		try {
			producer.deleteEntity(entity, key);
		} catch (Exception e) {
			// exception if the entity is not found, delete the entity if it exists;
		}
		return Response.Status.NO_CONTENT;
	}

	protected ODataProducer getProducer() {
		return producer;
	}

	@Override
	public String getMethod() {
		return HttpMethod.DELETE;
	}

	/* Implement InteractionCommand interface */

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getResource() == null);
		
		// Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entity, ctx.getId());
		} catch(Exception e) {
			return Result.FAILURE;
		}
		
		// delete the entity
		try {
			producer.deleteEntity(entity, key);
		} catch (Exception e) {
			// exception if the entity is not found, delete the entity if it exists;
		}
		return Result.SUCCESS;
	}
}
