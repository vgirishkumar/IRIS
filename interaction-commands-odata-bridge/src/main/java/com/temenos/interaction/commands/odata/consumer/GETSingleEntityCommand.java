package com.temenos.interaction.commands.odata.consumer;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;

/**
 * A GET command that will return the current value of the supplied entity and property.  This 
 * command is implemented by calling getEntity on the {@link ODataProducer)
 * @author aphethean
 */
public class GETSingleEntityCommand implements ResourceGetCommand {

	// Command configuration
	private String entity;
	private String entityKey;

	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;

	public GETSingleEntityCommand(String entity, String entityKey, ODataProducer producer) {
		this.entity = entity;
		this.entityKey = entityKey;
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entity);
	}

	/**
	 * Implement {@link ResourceGetCommand}
	 */
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		assert(id == null || "".equals(id));
		assert(entity.equals(entitySet.getName()));

		OEntityKey key = OEntityKey.create(entityKey);
		EntityResponse eResp = producer.getEntity(entitySet.getName(), key, null);
		OEntity oEntity = eResp.getEntity();
		EntityResource<OEntity> er = new EntityResource<OEntity>(oEntity);
		return new RESTResponse(Response.Status.OK, er);
	}

}
