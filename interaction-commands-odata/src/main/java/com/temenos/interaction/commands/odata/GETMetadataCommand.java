package com.temenos.interaction.commands.odata;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.edm.EdmDataServices;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

/**
 * GET command for obtaining meta data defining either the
 * resource model or the service document. 
 */
public class GETMetadataCommand implements InteractionCommand {

	private EdmDataServices edmDataServices;
	private String entity;

	/**
	 * Construct an instance of this command
	 * @param entity Entity name
	 * @param resourceMetadata Description of the resources and their types.
	 */
	public GETMetadataCommand(String entity, EdmDataServices resourceMetadata) {
		this.entity = entity;
		this.edmDataServices = resourceMetadata;
	}
	
	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		if(entity.equals("ServiceDocument")) {
			EntityResource<EdmDataServices> sdr = CommandHelper.createServiceDocumentResource(edmDataServices);
			ctx.setResource(sdr);
		} else {
			MetaDataResource<EdmDataServices> mdr = CommandHelper.createMetaDataResource(edmDataServices);
			ctx.setResource(mdr);
		}
		return Result.SUCCESS;
	}

	@Override
	public String getMethod() {
		return HttpMethod.GET;
	}

}
