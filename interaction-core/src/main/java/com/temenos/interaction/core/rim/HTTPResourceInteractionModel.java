package com.temenos.interaction.core.rim;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.ExtendedMediaTypes;

public interface HTTPResourceInteractionModel extends ResourceInteractionModel {

	/**
	 * GET a resource representation.
	 */
	@GET
	@Produces({
			MediaType.APPLICATION_ATOM_XML,
			MediaType.APPLICATION_XML,
			ExtendedMediaTypes.APPLICATION_ATOMSVC_XML,
			MediaType.APPLICATION_JSON,
			com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML,
			com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
	public abstract Response get(@Context HttpHeaders headers,
			@PathParam("id") String id,
			@Context UriInfo uriInfo);

	/**
	 * POST a resource representation.
	 */
	@POST
	@Consumes({
		MediaType.APPLICATION_ATOM_XML,
    	MediaType.APPLICATION_XML, 
    	MediaType.APPLICATION_JSON, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
	@Produces({
		MediaType.APPLICATION_ATOM_XML,
    	MediaType.APPLICATION_XML, 
    	MediaType.APPLICATION_JSON, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
	public abstract Response post(@Context HttpHeaders headers,
			@PathParam("id") String id,
			@Context UriInfo uriInfo,
			EntityResource<?> resource);

	/**
	 * PUT a resource.
	 */
	@PUT
	@Consumes({
		MediaType.APPLICATION_ATOM_XML,
    	MediaType.APPLICATION_XML, 
    	MediaType.APPLICATION_JSON, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
	public abstract Response put(@Context HttpHeaders headers,
			@PathParam("id") String id,
			@Context UriInfo uriInfo,
			EntityResource<?> resource);

	/**
	 * DELETE a resource.
	 */
	@DELETE
	public abstract Response delete(@Context HttpHeaders headers,
			@PathParam("id") String id,
			@Context UriInfo uriInfo);


}