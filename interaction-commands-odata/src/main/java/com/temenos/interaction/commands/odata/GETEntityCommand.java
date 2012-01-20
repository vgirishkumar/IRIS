package com.temenos.interaction.commands.odata;

import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class GETEntityCommand implements ResourceGetCommand {

	// Command configuration
	private String entity;
	
	private ODataProducer producer;
	
	public GETEntityCommand(String entity, ODataProducer producer) {
		this.entity = entity;
		this.producer = producer;
	}
	
	/* Implement ResourceGetCommand (OEntity) */
	public RESTResponse get(String id) {
		// TODO lookup EdmType and form the right kind of key for this entity
		OEntityKey key = OEntityKey.create(new Long(id));
		EntityResponse er = producer.getEntity(entity, key, null);
		OEntity oEntity = er.getEntity();
		
		RESTResponse rr = new RESTResponse(Response.Status.OK, new EntityResource(oEntity), null);
		return rr;
	}

}
