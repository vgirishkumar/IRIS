package com.temenos.interaction.example.hateoas.simple;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.hateoas.simple.model.Preferences;

public class GETPreferencesCommand implements InteractionCommand, ResourceGetCommand {

	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		// retrieve from a database, etc.
		return new RESTResponse(Status.OK, new EntityResource<Preferences>(new Preferences("user", "UK", "en")));
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		EntityResource<Preferences> resource = new EntityResource<Preferences>(new Preferences("user", "UK", "en"));
		ctx.setResource(resource);
		return Result.SUCCESS;
	}

	@Override
	public String getMethod() {
		return HttpMethod.GET;
	}

}
