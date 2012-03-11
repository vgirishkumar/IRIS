package com.temenos.interaction.commands.odata;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourcePostCommand;

public class CreateEntityCommand implements ResourcePostCommand {

	// Command configuration
	private String entity;

	private ODataProducer producer;

	public CreateEntityCommand(String entity, ODataProducer producer) {
		this.entity = entity;
		this.producer = producer;
	}

	@Override
	public String getMethod() {
		return HttpMethod.POST;
	}

	@Override
	public RESTResponse post(String id, EntityResource<?> resource) {
		assert(entity != null && !entity.equals(""));
		assert(resource != null);
		
		// create the entity
		EntityResponse er = producer.createEntity(entity, resource.getOEntity());
		OEntity oEntity = er.getEntity();
		
		RESTResponse rr = new RESTResponse(Response.Status.CREATED, new EntityResource<OEntity>(oEntity));
		return rr;
	}

}
