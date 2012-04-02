package com.temenos.interaction.core.command;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.resource.EntityResource;

public class NoopPUTCommand implements ResourcePutCommand {
	@Override
	public String getMethod() {
		return HttpMethod.PUT;
	}
	@Override
	public StatusType put(String id, EntityResource<?> resource) {
		return MethodNotAllowedCommand.HTTP_STATUS_METHOD_NOT_ALLOWED;
	}
};

