package com.temenos.interaction.commands.odata;

import org.odata4j.edm.EdmDataServices;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

/**
 * GET command for obtaining meta data defining either the
 * resource model or the service document. 
 */
public class GETMetadataCommand implements InteractionCommand {

	// command configuration
	// TODO remove this when we no longer use a MetaDataResource
	private String resourceToProvide;
	
	private EdmDataServices edmDataServices;

	/**
	 * Construct an instance of this command
	 * @param resourceToProvide Configure this command to provide either an EntityResource for the
	 * service document or a MetaDataResource for the metadata.
	 * @param resourceMetadata Description of the resources and their types.
	 */
	public GETMetadataCommand(String resourceToProvide, EdmDataServices resourceMetadata) {
		this.resourceToProvide = resourceToProvide;
		this.edmDataServices = resourceMetadata;
	}
	
	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		if(resourceToProvide.equals("ServiceDocument")) {
			EntityResource<EdmDataServices> sdr = CommandHelper.createServiceDocumentResource(edmDataServices);
			ctx.setResource(sdr);
		} else {
			MetaDataResource<EdmDataServices> mdr = CommandHelper.createMetaDataResource(edmDataServices);
			ctx.setResource(mdr);
		}
		return Result.SUCCESS;
	}

}
