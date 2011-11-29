package com.temenos.interaction.core;

import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class RESTResponse {

	public Response.Status status;
	public RESTResource resource;
	public Set<String> validNextStates;
	
	public RESTResponse(Response.Status status, RESTResource resource, Set<String> nextStates) {
		this.status = status;
		this.resource = resource;
		this.validNextStates = nextStates;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public RESTResource getResource() {
		return resource;
	}
	
	public Set<String> getValidNextStates() {
		return validNextStates;
	}

	
}
