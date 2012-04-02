package com.temenos.interaction.core.command;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.StatusType;

public class NoopDELETECommand implements ResourceDeleteCommand {
	@Override
	public String getMethod() {
		return HttpMethod.DELETE;
	}
	@Override
	public StatusType delete(String id) {
		return MethodNotAllowedCommand.HTTP_STATUS_METHOD_NOT_ALLOWED;
	}
};

