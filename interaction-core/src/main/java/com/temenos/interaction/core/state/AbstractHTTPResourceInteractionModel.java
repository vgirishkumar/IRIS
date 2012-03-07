package com.temenos.interaction.core.state;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.odata4j.core.OEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.ExtendedMediaTypes;
import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceDeleteCommand;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePostCommand;
import com.temenos.interaction.core.command.ResourcePutCommand;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;

/**
 * <P>
 * Define HTTP interactions for individual resources.  This model for resource
 * interaction should be used for individual or collection resources who conform
 * to the HTTP generic uniform interface.
 * HTTP provides one operation to view the resource (GET), one operation to create
 * a new resource (POST) and a only two operations to change an individual resources
 * state (PUT and DELETE).  
 * </P>
 * @author aphethean
 *
 */
public abstract class AbstractHTTPResourceInteractionModel implements HTTPResourceInteractionModel {
	private final Logger logger = LoggerFactory.getLogger(AbstractHTTPResourceInteractionModel.class);

	private String entityName;
	private String resourcePath;
	private ResourceRegistry resourceRegistry;
	private CommandController commandController;
		
	public AbstractHTTPResourceInteractionModel(String entityName, String resourcePath) {
		this(entityName, resourcePath, null, new CommandController());
	}

	public AbstractHTTPResourceInteractionModel(String entityName, String resourcePath, ResourceRegistry resourceRegistry, CommandController commandController) {
		this.entityName = entityName;
		this.resourcePath = resourcePath;
		this.resourceRegistry = resourceRegistry;
		this.commandController = commandController;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public String getFQResourcePath() {
		String result = "";
		if (getParent() != null)
			result += getParent().getResourcePath();
			
		return result += getResourcePath();
	}

	public ResourceInteractionModel getParent() {
		return null;
	}

	public Collection<ResourceInteractionModel> getChildren() {
		return null;
	}

	protected CommandController getCommandController() {
		return commandController;
	}
	
	/**
	 * GET a resource representation.
	 * @precondition a valid GET command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 * @see com.temenos.interaction.core.state.HTTPResourceInteractionModel#get(javax.ws.rs.core.HttpHeaders, java.lang.String)
	 */
    @Override
	@GET
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML, ExtendedMediaTypes.APPLICATION_ATOMSVC_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response get( @Context HttpHeaders headers, @PathParam("id") String id, @Context UriInfo uriInfo ) {
    	logger.debug("GET " + getFQResourcePath());
    	assert(resourcePath != null);
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(getFQResourcePath());
    	MultivaluedMap<String, String> queryParameters = uriInfo != null ? uriInfo.getQueryParameters() : null;
    	// work around an issue in wink, wink does not decode query parameters in 1.1.3
    	decodeQueryParams(queryParameters);
    	RESTResponse response = getCommand.get(id, queryParameters);
    	
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
    
    private void decodeQueryParams(MultivaluedMap<String, String> queryParameters) {
    	try {
    		if (queryParameters == null)
    			return;
			URLDecoder ud = new URLDecoder();
			for (String key : queryParameters.keySet()) {
				List<String> values = queryParameters.get(key);
				if (values != null) {
					List<String> newValues = new ArrayList<String>();
				    for (String value : values) {
				    	newValues.add(ud.decode(value, "UTF-8"));
				    }
				    queryParameters.put(key, newValues);
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }
    
	/**
	 * POST a document to a resource.
	 * @precondition a valid POST command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 */
    @POST
    @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response post( @Context HttpHeaders headers, @PathParam("id") String id, EntityResource resource ) {
    	logger.debug("POST " + resourcePath);
    	assert(resourcePath != null);
		ResourcePostCommand postCommand = (ResourcePostCommand) commandController.fetchStateTransitionCommand("POST", getResourcePath());
    	RESTResponse response = postCommand.post(id, resource);
    	assert (response != null);
    	StatusType status = response.getStatus();
    	assert (status != null);  // not a valid post command
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
	 * @see com.temenos.interaction.core.state.HTTPResourceInteractionModel#put(javax.ws.rs.core.HttpHeaders, java.lang.String, com.temenos.interaction.core.EntityResource)
	 */
    @Override
	@PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response put( @Context HttpHeaders headers, @PathParam("id") String id, EntityResource resource ) {
    	logger.debug("PUT " + getFQResourcePath());
    	assert(resourcePath != null);
		ResourcePutCommand putCommand = (ResourcePutCommand) commandController.fetchStateTransitionCommand("PUT", getFQResourcePath());
		StatusType status = putCommand.put(id, resource);
		assert (status != null);  // not a valid put command
    	if (status == Response.Status.OK) {
        	return get(headers, id, null);
    	} else {
    		return Response.status(status).build();
    	}
    }

	/**
	 * DELETE a resource.
	 * @precondition a valid DELETE command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 * @see com.temenos.interaction.core.state.HTTPResourceInteractionModel#delete(javax.ws.rs.core.HttpHeaders, java.lang.String)
	 */
    @Override
	@DELETE
    public Response delete( @Context HttpHeaders headers, @PathParam("id") String id ) {
    	logger.debug("DELETE " + getFQResourcePath());
    	assert(resourcePath != null);
    	ResourceDeleteCommand deleteCommand = (ResourceDeleteCommand) commandController.fetchStateTransitionCommand("DELETE", getFQResourcePath());
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
    	logger.debug("OPTIONS " + getFQResourcePath());
    	assert(resourcePath != null);
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(getFQResourcePath());
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
    	if (commandController.isValidStateTransitioncommand("PUT", resourcePath)) {
        	interactions.add("PUT");
    	}
    	if (commandController.isValidStateTransitioncommand("POST", resourcePath)) {
        	interactions.add("POST");
    	}
    	if (commandController.isValidStateTransitioncommand("DELETE", resourcePath)) {
        	interactions.add("DELETE");
    	}
    	interactions.add("HEAD");
    	interactions.add("OPTIONS");
    	return interactions;
    }
}
