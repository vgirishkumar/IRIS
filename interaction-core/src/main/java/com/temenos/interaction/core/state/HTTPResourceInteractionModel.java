package com.temenos.interaction.core.state;

import java.util.HashSet;
import java.util.Set;

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

import org.odata4j.core.OEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceDeleteCommand;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePutCommand;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;

/**
 * <P>
 * Define HTTP interactions for individual resources.  This model for resource
 * interaction should be used for an individual resource who maintains persistent
 * state.  HTTP provides one operation to view the resource (GET), and a only two
 * operation to change an individual resources state (PUT and DELETE).  
 * </P>
 * <P>
 * You might be wondering about a POST to a resource.  We've defined POST 
 * as a transient operation, ie. an operation that does not change an individual 
 * resources state. See {@link TRANSIENTResourceInteractionModel}.  The HTTP spec
 * defines POST as "request that the origin server accept the entity enclosed in the 
 * request as a new subordinate of the resource identified".  Therefore, you are
 * creating a new individual resource, not modifying this resources state.
 * </P>
 * @author aphethean
 *
 */
public abstract class HTTPResourceInteractionModel implements ResourceInteractionModel {
	private final Logger logger = LoggerFactory.getLogger(HTTPResourceInteractionModel.class);

	private String entityName;
	private String resourcePath;
	private ResourceRegistry resourceRegistry;
	private CommandController commandController;
		
	public HTTPResourceInteractionModel(String entityName, String resourcePath) {
		this(entityName, resourcePath, null, new CommandController());
	}

	public HTTPResourceInteractionModel(String entityName, String resourcePath, ResourceRegistry resourceRegistry, CommandController commandController) {
		this.entityName = entityName;
		this.resourcePath = resourcePath;
		this.resourceRegistry = resourceRegistry;
		if (resourceRegistry != null) {
			resourceRegistry.add(this);
		}
		this.commandController = commandController;
	}

	public String getEntityName() {
		return entityName;
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
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response get( @Context HttpHeaders headers, @PathParam("id") String id ) {
    	logger.debug("GET " + resourcePath);
    	assert(resourcePath != null);
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(resourcePath);
    	RESTResponse response = getCommand.get(id, null);
    	
    	if (response != null && resourceRegistry != null && response.getResource() instanceof EntityResource) {
        	RESTResource rr = response.getResource();
    		EntityResource er = (EntityResource) rr;
        	OEntity oe = resourceRegistry.rebuildOEntityLinks(er.getOEntity(), getCurrentState());
        	EntityResource rebuilt = new EntityResource(oe);
        	response = new RESTResponse(response.getStatus(), rebuilt);
    	}
    	
    	assert (response != null);
    	StatusType status = response.getStatus();
		assert (status != null);  // not a valid get command
		if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
			assert(response.getResource() != null);
			ResponseBuilder rb = Response.ok(response.getResource()).status(status);
			return HeaderHelper.allowHeader(rb, getInteractions()).build();
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
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(resourcePath);
    	ResponseBuilder response = Response.ok();
    	RESTResponse rResponse = getCommand.get(id, null);
    	assert (rResponse != null);
    	StatusType status = rResponse.getStatus();
		assert (status != null);  // not a valid get command
    	if (status == Response.Status.OK) {
        	response = HeaderHelper.allowHeader(response, getInteractions());
    	}
    	return response.build();
    }
    
    /**
     * The current application state.
     * @return
     */
    public ResourceState getCurrentState() {
    	return null;
    }
    
    /**
     * Get the valid methods for interacting with this resource.
     * @return
     */
    public Set<String> getInteractions() {
    	Set<String> interactions = new HashSet<String>();
    	interactions.add("GET");
    	interactions.add("PUT");
    	interactions.add("POST");
    	interactions.add("DELETE");
    	interactions.add("HEAD");
    	interactions.add("OPTIONS");
    	return interactions;
    }
}
