package com.temenos.interaction.core.state;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceDeleteCommand;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePutCommand;

/**
 * Define a Create Read Update Delete 'CRUD' Resource Interaction Model.
 * @author aphethean
 *
 */
public abstract class CRUDResourceInteractionModel implements ResourceStateTransition {
	private final Logger logger = LoggerFactory.getLogger(CRUDResourceInteractionModel.class);

	private String resourcePath;
	private CommandController commandController = new CommandController();
		
	public CRUDResourceInteractionModel(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}

	protected CommandController getCommandController() {
		return commandController;
	}
	
	/**
	 * GET a resource representation.
	 * @precondition a valid GET command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response get( @Context HttpHeaders headers, @PathParam("id") String id ) {
    	logger.debug("GET " + resourcePath);
    	assert(resourcePath != null);
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(getResourcePath());
    	RESTResponse response = getCommand.get(id);
    	assert (response != null);
    	StatusType status = response.getStatus();
		assert (status != null);  // not a valid get command
		if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
			assert(response.getResource() != null);
			ResponseBuilder rb = Response.ok(response.getResource()).status(status);
			return HeaderHelper.allowHeader(rb, response).build();
		}
		return Response.status(status).build();
    }
    
	/**
	 * PUT a resource.
	 * @precondition a valid PUT command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 */
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response put( @Context HttpHeaders headers, @PathParam("id") String id, EntityResource resource ) {
    	logger.debug("PUT " + resourcePath);
    	assert(resourcePath != null);
		ResourcePutCommand putCommand = (ResourcePutCommand) commandController.fetchStateTransitionCommand("PUT", getResourcePath());
		StatusType status = putCommand.put(id, resource);
		assert (status != null);  // not a valid put command
    	if (status == Response.Status.OK) {
        	return get(headers, id);
    	} else {
    		return Response.status(status).build();
    	}
    }

	/**
	 * DELETE a resource.
	 * @precondition a valid DELETE command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 */
    @DELETE
    public Response delete( @Context HttpHeaders headers, @PathParam("id") String id ) {
    	logger.debug("DELETE " + resourcePath);
    	assert(resourcePath != null);
    	ResourceDeleteCommand deleteCommand = (ResourceDeleteCommand) commandController.fetchStateTransitionCommand("DELETE", getResourcePath());
		StatusType status = deleteCommand.delete(id);
		assert (status != null);  // not a valid put command
   		return Response.status(status).build();
    }

	/**
	 * OPTIONS for a resource.
	 * @precondition a valid GET command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 */
    @Override
    public Response options(String id) {
    	logger.debug("OPTIONS " + resourcePath);
    	assert(resourcePath != null);
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(getResourcePath());
    	ResponseBuilder response = Response.ok();
    	RESTResponse rResponse = getCommand.get(id);
    	assert (rResponse != null);
    	StatusType status = rResponse.getStatus();
		assert (status != null);  // not a valid get command
    	if (status == Response.Status.OK) {
        	response = HeaderHelper.allowHeader(response, rResponse);
    	}
    	return response.build();
    }
}
