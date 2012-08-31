package com.temenos.interaction.example.hateoas.banking;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Metadata;

/**
 * GET command for obtaining meta data. 
 */
public class GETMetadataCommand implements InteractionCommand {

	private Metadata metadata;

	/**
	 * Construct an instance of this command
	 * @param metadata metadata
	 */
	public GETMetadataCommand(Metadata metadata) {
		this.metadata = metadata;
	}
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		MetaDataResource<Metadata> mdr = new MetaDataResource<Metadata>(metadata) {};	
		ctx.setResource(mdr);
		return Result.SUCCESS;
	}

	@Override
	public String getMethod() {
		return HttpMethod.GET;
	}

}
