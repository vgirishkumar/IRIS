package com.temenos.interaction.commands.odata.consumer;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.EntityResource;

public class GETEntityCommand implements ResourceGetCommand {

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
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		//Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entitySetName, id);
		} catch(Exception e) {
			return new RESTResponse(Response.Status.NOT_ACCEPTABLE, null);
		}
		
		OEntityGetRequest<OEntity> request = getConsumer().getEntity(entitySetName, key);
		//request.expand(expand);
		//request.select(select);
		
		/*
		 * Execute request
		 */
		OEntity oEntity = request.execute();
		
		EntityResource<OEntity> oer = CommandHelper.createEntityResource(oEntity);
		RESTResponse rr = new RESTResponse(Response.Status.OK, oer);		
		return rr;
	}

	protected ODataConsumer getConsumer() {
		return consumer;
	}
}
