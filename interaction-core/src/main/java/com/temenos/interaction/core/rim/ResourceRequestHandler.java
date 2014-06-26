package com.temenos.interaction.core.rim;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.EntityResource;

public interface ResourceRequestHandler {
	
	/**
	 * 
	 * @param rimHandler the main handler for our resource requests
	 * @param headers HttpHeaders
	 * @param ctx our InteractionContext
	 * @param resource the request body (POST, PUT)
	 * @param config the resources we want, and how shall we process them
	 * @return
	 */
	public Map<Transition, ResourceRequestResult> getResources(HTTPHypermediaRIM rimHandler, HttpHeaders headers, InteractionContext ctx, EntityResource<?> resource, ResourceRequestConfig config);
}
