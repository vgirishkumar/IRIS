package com.temenos.interaction.core.state;

import java.util.Collection;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.jayway.jaxrs.hateoas.HateoasContext;
import com.temenos.interaction.core.link.ResourceState;

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
	
	public HateoasContext getHateoasContext();
	
    @OPTIONS
    public Response options( @PathParam("id") String id );
	
}
