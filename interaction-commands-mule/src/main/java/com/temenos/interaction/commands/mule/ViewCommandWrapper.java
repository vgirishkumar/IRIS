package com.temenos.interaction.commands.mule;

import javax.ws.rs.core.MultivaluedMap;

public class ViewCommandWrapper {

	private final MultivaluedMap<String, String> pathParams;
	private final MultivaluedMap<String, String> queryParams;
	
	public ViewCommandWrapper(MultivaluedMap<String, String> pathParams, MultivaluedMap<String, String> queryParams) {
		this.pathParams = pathParams;
		this.queryParams = queryParams;
	}
	
	public MultivaluedMap<String, String> getPathParameters() {
		return pathParams;
	}
	
	public MultivaluedMap<String, String> getQueryParameters() {
		return queryParams;
	}
}
