package com.temenos.interaction.example.hateoas.simple;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.EntityResource;

public class GETServiceRootCommand implements ResourceGetCommand {

	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		EntityResource<Object> resource = new EntityResource<Object>(null);
		RESTResponse rr = new RESTResponse(Response.Status.OK, resource);
		return rr;
	}

}
