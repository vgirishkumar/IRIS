package com.temenos.interaction.core;

import java.util.Set;

import javax.ws.rs.core.Response.StatusType;

public class RESTResponse {

	public StatusType status;
	public RESTResource resource;
	public Set<String> validNextStates;
	
	public RESTResponse(StatusType status, RESTResource resource, Set<String> nextStates) {
		this.status = status;
		this.resource = resource;
		this.validNextStates = nextStates;
	}
	
	public StatusType getStatus() {
		return status;
	}
	
	public RESTResource getResource() {
		return resource;
	}
	
	public Set<String> getValidNextStates() {
		return validNextStates;
	}

	
}
