package com.temenos.interaction.commands.odata;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class GETEntityCommand implements ResourceGetCommand {

	// Command configuration
	private String entity;
	
	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;
	private Iterable<EdmEntityType> entityTypes;

	public GETEntityCommand(String entity, ODataProducer producer) {
		this.entity = entity;
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entity);
		this.entityTypes = edmDataServices.getEntityTypes();
		assert(entity.equals(entitySet.getName()));
	}
	
	/* Implement ResourceGetCommand (OEntity) */
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		//Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entity, id);
		} catch(Exception e) {
			return new RESTResponse(Response.Status.NOT_ACCEPTABLE, null);
		}
		
		//Get the entity
		EntityResponse er = getProducer().getEntity(entity, key, null);
		OEntity oEntity = er.getEntity();
		
		EntityResource<OEntity> oer = CommandHelper.createEntityResource(oEntity);
		RESTResponse rr = new RESTResponse(Response.Status.OK, oer);		
		return rr;
	}

	protected ODataProducer getProducer() {
		return producer;
	}
}
