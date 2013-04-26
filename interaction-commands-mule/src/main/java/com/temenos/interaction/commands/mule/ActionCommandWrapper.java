package com.temenos.interaction.commands.mule;

import javax.ws.rs.core.MultivaluedMap;

import com.temenos.interaction.core.entity.Entity;

public class ActionCommandWrapper {

	private final MultivaluedMap<String, String> pathParams;
	private final MultivaluedMap<String, String> queryParams;
	private final Entity requestBody;

	public ActionCommandWrapper(MultivaluedMap<String, String> pathParams, MultivaluedMap<String, String> queryParams, Entity body) {
		this.pathParams = pathParams;
		this.queryParams = queryParams;
		this.requestBody = body;
	}
	
	public MultivaluedMap<String, String> getPathParameters() {
		return pathParams;
	}
	
	public MultivaluedMap<String, String> getQueryParameters() {
		return queryParams;
	}
	
	public Entity getRequestBody() {
		return requestBody;
	}
}
