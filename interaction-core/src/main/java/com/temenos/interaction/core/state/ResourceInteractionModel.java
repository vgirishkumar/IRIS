package com.temenos.interaction.core.state;

import java.util.Collection;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.temenos.interaction.core.link.Link;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.resource.RESTResource;

public interface ResourceInteractionModel {

    /**
     * The current application state.
     * @return
     */
    public ResourceState getCurrentState();
    /**
     * The links from this application state.
     * @return
     */
    public Collection<Link> getLinks(HttpHeaders headers, MultivaluedMap<String, String> pathParameters, RESTResource entity);
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
