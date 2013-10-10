package com.temenos.interaction.core;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.resource.RESTResource;

public class RESTResponse {

	private final StatusType status;
	private final RESTResource resource;
	private final Set<Link> transitions = new HashSet<Link>();
	private final Set<String> validMethods = new HashSet<String>();
	
	public RESTResponse(StatusType status, RESTResource resource) {
		this(status, resource, null, null);
	}
	
	public RESTResponse(StatusType status, RESTResource resource, Set<Link> transitions, Set<String> validMethods) {
		this.status = status;
		this.resource = resource;
		if (transitions != null)
			this.transitions.addAll(transitions);
		if (validMethods != null)
			this.validMethods.addAll(validMethods);
	}
	
	public StatusType getStatus() {
		return status;
	}
	
	public RESTResource getResource() {
		return resource;
	}
	
	public Set<Link> getTransitions() {
		return transitions;
	}

	public Set<String> getValidMethods() {
		return validMethods;
	}
	
}
