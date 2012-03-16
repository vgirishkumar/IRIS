package com.temenos.interaction.core.state;

import java.util.Collection;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.jayway.jaxrs.hateoas.HateoasContext;

public interface ResourceInteractionModel {

	/**
	 * The name of resource definition that this interaction model deals with
	 * @return
	 */
	public String getEntityName();
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
