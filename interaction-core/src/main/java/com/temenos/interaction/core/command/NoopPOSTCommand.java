package com.temenos.interaction.core.command;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.resource.EntityResource;

public 	class NoopPOSTCommand implements ResourcePostCommand {
	@Override
	public String getMethod() {
		return HttpMethod.POST;
	}
	@Override
	public RESTResponse post(String id, EntityResource<?> resource) {
		return new RESTResponse(MethodNotAllowedCommand.HTTP_STATUS_METHOD_NOT_ALLOWED, new EntityResource<String>(""));
	}
};
