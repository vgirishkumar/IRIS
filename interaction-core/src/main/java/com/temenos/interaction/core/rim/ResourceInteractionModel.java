package com.temenos.interaction.core.rim;

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


import java.util.Collection;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.temenos.interaction.core.hypermedia.ResourceState;

public interface ResourceInteractionModel {

    /**
     * The current application state.
     * @return
     */
    public ResourceState getCurrentState();
    /**
	 * The path to this resource
	 * @return
	 */
	public String getResourcePath();
	/**
	 * The path to this resource with all ancestors
	 * @return
	 */
	public String getFQResourcePath();
	public ResourceInteractionModel getParent();
	public Collection<ResourceInteractionModel> getChildren();
	
    @OPTIONS
    public Response options( @Context HttpHeaders headers, @PathParam("id") String id, @Context UriInfo uriInfo );
	
}
