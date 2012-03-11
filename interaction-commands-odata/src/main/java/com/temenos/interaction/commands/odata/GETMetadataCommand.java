package com.temenos.interaction.commands.odata;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.MetaDataResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.ServiceDocumentResource;
import com.temenos.interaction.core.command.ResourceGetCommand;

/**
 * GET command for obtaining meta data defining either the
 * resource model or the service document. 
 */
public class GETMetadataCommand implements ResourceGetCommand {

	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	private String entity;

	/**
	 * Construct an instance of this command
	 * @param entity Entity name
	 * @param producer Producer
	 */
	public GETMetadataCommand(String entity, ODataProducer producer) {
		this.entity = entity;
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
	}

	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		RESTResponse rr;
		if(entity.equals("ServiceDocument")) {
			ServiceDocumentResource<EdmDataServices> sdr = CommandHelper.createServiceDocumentResource(edmDataServices);
			rr = new RESTResponse(Response.Status.OK, sdr);
		}
		else {
			MetaDataResource<EdmDataServices> mdr = CommandHelper.createMetaDataResource(edmDataServices);
			rr = new RESTResponse(Response.Status.OK, mdr);
		}
		return rr;
	}

	protected ODataProducer getProducer() {
		return producer;
	}
}
