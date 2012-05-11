package com.temenos.interaction.commands.odata.consumer;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OCreateRequest;
import org.odata4j.core.OEntity;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourcePostCommand;
import com.temenos.interaction.core.resource.EntityResource;

public class CreateEntityCommand implements ResourcePostCommand {

	// Command configuration
	private String entitySetName;

	private ODataConsumer consumer;

	public CreateEntityCommand(String entitySetName, ODataConsumer consumer) {
		this.entitySetName = entitySetName;
		this.consumer = consumer;
	}

	@Override
	public String getMethod() {
		return HttpMethod.POST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RESTResponse post(String id, EntityResource<?> resource) {
		assert(entitySetName != null && !entitySetName.equals(""));
		assert(resource != null);
		
		// create the entity
		EntityResource<OEntity> entityResource = (EntityResource<OEntity>) resource;
		OEntity entity = entityResource.getEntity();
		
		OCreateRequest<OEntity> createRequest = consumer.createEntity(entitySetName);
		if (entity != null){
			createRequest.properties(entity.getProperties());
		}
		
		/*
		 * Execute request
		 */
		OEntity newEntity = createRequest.execute();
		
		RESTResponse rr = new RESTResponse(Response.Status.CREATED, CommandHelper.createEntityResource(newEntity));
		return rr;
	}

}
