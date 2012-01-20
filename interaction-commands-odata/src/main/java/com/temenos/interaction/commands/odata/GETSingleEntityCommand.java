package com.temenos.interaction.commands.odata;

import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.EntityResource;
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

	public GETSingleEntityCommand(String entity, String entityKey, ODataProducer producer) {
		this.entity = entity;
		this.entityKey = entityKey;
		this.producer = producer;
	}

	/**
	 * Implement {@link ResourceGetCommand}
	 */
	public RESTResponse get(String id) {
		assert(id == null || "".equals(id));

		OEntityKey key = OEntityKey.create(entityKey);
		EntityResponse eResp = producer.getEntity(entity, key, null);
		OEntity oEntity = eResp.getEntity();
		EntityResource er = new EntityResource(oEntity);
		return new RESTResponse(Response.Status.OK, er, null);
	}

}
