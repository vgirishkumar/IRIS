package com.temenos.interaction.core;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class RESTResponse {

	public Response.Status status;
	public RESTResource resource;
	
	public RESTResponse(Response.Status status, RESTResource resource) {
		this.status = status;
		this.resource = resource;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public RESTResource getResource() {
		return resource;
	}
	
	
}
