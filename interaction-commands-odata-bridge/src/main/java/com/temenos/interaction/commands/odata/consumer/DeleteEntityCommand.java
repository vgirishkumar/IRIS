package com.temenos.interaction.commands.odata.consumer;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;

import com.temenos.interaction.core.command.ResourceDeleteCommand;

public class DeleteEntityCommand implements ResourceDeleteCommand {

	// Command configuration
	private String entitySetName;
	
	private ODataConsumer consumer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;
	private Iterable<EdmEntityType> entityTypes;

	public DeleteEntityCommand(String entitySetName, ODataConsumer consumer) {
		this.entitySetName = entitySetName;
		this.consumer = consumer;
		this.edmDataServices = consumer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entitySetName);
		this.entityTypes = edmDataServices.getEntityTypes();
		assert(entitySetName.equals(entitySet.getName()));
	}
	
	/* Implement ResourceDeleteCommand (OEntity) */
	public StatusType delete(String id) {
		// Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entitySetName, id);
		} catch(Exception e) {
			return Response.Status.NOT_ACCEPTABLE;
		}
		
		// delete the entity
		try {
			consumer.deleteEntity(entitySetName, key).execute();
		} catch (Exception e) {
			// exception if the entity is not found, delete the entity if it exists;
		}
		return Response.Status.NO_CONTENT;
	}

	protected ODataConsumer getConsumer() {
		return consumer;
	}

	@Override
	public String getMethod() {
		return HttpMethod.DELETE;
	}
}
