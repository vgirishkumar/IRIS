package com.temenos.interaction.core.rim;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.odata4j.core.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.ExtendedMediaTypes;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.HttpStatusTypes;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.hypermedia.ASTValidation;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.LinkHeader;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.Transition;

/**
 * <P>
 * Implement HTTP interactions for resources using an hypermedia driven command 
 * controller. This model for resource interaction can be used for individual (item) 
 * or collection resources who conform to the HTTP generic uniform interface and the
 * Hypermedia As The Engine Of Application State (HATEOAS) constraints.
 * HTTP provides one operation to view the resource (GET), one operation to create
 * a new resource (POST) and two operations to change an individual resources
 * state (PUT and DELETE).  
 * </P>
 * @author aphethean
 *
 */
public class HTTPHypermediaRIM implements HTTPResourceInteractionModel {
	private final static Logger logger = LoggerFactory.getLogger(HTTPHypermediaRIM.class);

	private final HTTPHypermediaRIM parent;
	private final NewCommandController commandController;
	private final ResourceStateMachine hypermediaEngine;
	private final ResourceState currentState;
		
	/**
	 * <p>Create a new resource for HTTP interaction.</p>
	 * @param commandController
	 * 			All commands for all resources.
	 * @param hypermediaEngine
	 * 			All application states, responsible for creating links from one state to another.
	 * @param currentState	
	 * 			The current application state when accessing this resource.
	 */
	public HTTPHypermediaRIM(
			NewCommandController commandController, 
			ResourceStateMachine hypermediaEngine) {
		this(null, commandController, hypermediaEngine, hypermediaEngine.getInitial());
	}

	/*
	 * Create a child resource.  This constructor is used to create resources where there
	 * are multiple states for the same entity.
	 * @param parent
	 * 			This resources parent interaction model.
	 * @param commandController
	 * 			All commands for all resources.
	 * @param hypermediaEngine
	 * 			All application states, responsible for creating links from one state to another.
	 * @param currentState	
	 * 			The current application state when accessing this resource.
	 */
	protected HTTPHypermediaRIM(
			HTTPHypermediaRIM parent, 
			NewCommandController commandController, 
			ResourceStateMachine hypermediaEngine,
			ResourceState currentState) {
		this.parent = parent;
		this.commandController = commandController;
		this.hypermediaEngine = hypermediaEngine;
		this.currentState = currentState;
		assert(this.commandController != null);
		assert(this.hypermediaEngine != null);
		assert(this.currentState != null);
		if (parent == null && hypermediaEngine.getInitial() != null) {
			logger.info("State graph for [" + this.toString() + "] [" + new ASTValidation().graph(hypermediaEngine) + "]");
		}
		bootstrap();
	}

	protected ResourceStateMachine getHypermediaEngine() {
		return hypermediaEngine;
	}
	
	/*
	 * Bootstrap the resource by attempting to fetch a command for all the required
	 * interactions with the resource state.
	 */
	private void bootstrap() {
		// every resource MUST have a GET command
		getCommandController().fetchCommand("GET", getFQResourcePath());
		Set<String> interactions = hypermediaEngine.getInteractions(currentState);
		
		if (interactions != null) {
			// interactions are a set of http methods
			for (String method : interactions) {
				logger.debug("Checking configuration for [" + method + "] " + getFQResourcePath());
				// already checked GET command for this resource
				if (method.equals(HttpMethod.GET))
					continue;
				// check valid http method
				if (!(method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE) || method.equals(HttpMethod.POST)))
					throw new RuntimeException("Invalid configuration of state [" + hypermediaEngine.getInitial().getId() + "] - invalid http method [" + method + "]");
				// fetch command from command controller for this method
				InteractionCommand command = getCommandController().fetchCommand(method, getFQResourcePath());
				if (command == null)
					throw new RuntimeException("Invalid configuration of dynamic resource [" + this + "] - no state transition command for http method [" + method + "]");
			}
		}

		// TODO should be verified in constructor, but this class is currently mixed with dynamic resources that do not use links
		// assert(getResourceRegistry() != null);
		// resource created and valid, now register ourselves in the resource registry
//		if (getResourceRegistry() != null)
//			getResourceRegistry().add(this);
	}

	public String getResourcePath() {
		return currentState.getPath();
	}

	public String getFQResourcePath() {
		String result = "";
		if (getParent() != null)
			result = getParent().getResourcePath();
			
		return result + getResourcePath();
	}

	@Override
	public ResourceInteractionModel getParent() {
		return parent;
	}

	@Override
	public Collection<ResourceInteractionModel> getChildren() {
		List<ResourceInteractionModel> result = new ArrayList<ResourceInteractionModel>();
		
		Map<String, ResourceState> resourceStates = hypermediaEngine.getResourceStatesByPath(this.currentState);
		for (String childPath : resourceStates.keySet()) {
			// get the child state
			ResourceState childState = resourceStates.get(childPath);
			HTTPHypermediaRIM child = null;
			if (childState.equals(getCurrentState()) || childState.getPath().equals(getCurrentState().getPath())) {
				continue;
			}
			child = new HTTPHypermediaRIM(null, getCommandController(), hypermediaEngine, childState);
			result.add(child);
		}
		return result;
	}

    /*
     * The map of all commands for http methods, paths, and media types.
     */
    protected NewCommandController getCommandController() {
		return commandController;
	}

	/**
	 * GET a resource representation.
	 * @precondition a valid GET command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 * @see com.temenos.interaction.core.rim.HTTPResourceInteractionModel#get(javax.ws.rs.core.HttpHeaders, java.lang.String)
	 */
	@Override
	@GET
    @Produces({MediaType.APPLICATION_ATOM_XML, 
    	MediaType.APPLICATION_XML, 
    	ExtendedMediaTypes.APPLICATION_ATOMSVC_XML, 
    	MediaType.APPLICATION_JSON, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
    public Response get( @Context HttpHeaders headers, @PathParam("id") String id, @Context UriInfo uriInfo ) {
    	logger.info("GET " + getFQResourcePath());
    	assert(getResourcePath() != null);
    	if (!getCommandController().isValidCommand("GET", getFQResourcePath())) {
    		return buildResponse(headers, Status.NOT_FOUND, null, null, getInteractions(), null);
    	}
    	InteractionCommand getCommand = getCommandController().fetchCommand("GET", getFQResourcePath());
    	MultivaluedMap<String, String> queryParameters = uriInfo != null ? uriInfo.getQueryParameters(true) : null;
    	MultivaluedMap<String, String> pathParameters = uriInfo != null ? uriInfo.getPathParameters(true) : null;

    	// work around an issue in wink, wink does not decode query parameters in 1.1.3
    	decodeQueryParams(queryParameters);
    	// create the interaction context
    	InteractionContext ctx = new InteractionContext(pathParameters, queryParameters, getCurrentState());
    	// execute GET command
    	InteractionCommand.Result result = getCommand.execute(ctx);
    	StatusType status = result == Result.SUCCESS ? Status.OK : Status.NOT_FOUND;
    	return buildResponse(headers, status, ctx.getResource(), getLinks(headers, pathParameters, ctx.getResource()), null, null);
	}
	
    private Response buildResponse(HttpHeaders headers, StatusType status, RESTResource resource, Collection<Link> links, Set<String> interactions, Link target) {	
    	RESTResponse response = new RESTResponse(status, resource);

    	assert (response != null);
		assert (status != null);  // not a valid get command

		// Build the Response (representation will be created by the jax-rs Provider)
		ResponseBuilder responseBuilder = Response.status(status);
		if (status.equals(HttpStatusTypes.RESET_CONTENT)) {
			responseBuilder = HeaderHelper.allowHeader(responseBuilder, interactions);
		} else if (status.equals(HttpStatusTypes.METHOD_NOT_ALLOWED)) {
			assert(interactions != null);
			responseBuilder = HeaderHelper.allowHeader(responseBuilder, interactions);
		} else if (status.equals(Response.Status.NO_CONTENT)) {
			responseBuilder = HeaderHelper.allowHeader(responseBuilder, interactions);
		} else if (status.equals(Response.Status.SEE_OTHER)) {
			responseBuilder = HeaderHelper.locationHeader(responseBuilder, target.getHref());
		} else if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
			assert(response.getResource() != null);
			/*
			 * Add entity information to this resource
			 */
    		resource.setEntityName(getCurrentState().getEntityName());
			/*
			 * Add hypermedia information to this resource
			 */
    		resource.setLinks(links);
    		/*
    		 * Wrap response into a JAX-RS GenericEntity object to ensure we have the type 
    		 * information available to the Providers
    		 */
    		responseBuilder.entity(resource.getGenericEntity());
    		responseBuilder = HeaderHelper.allowHeader(responseBuilder, interactions);
    	} else {
        	// TODO add support for other status codes
    	}

		logger.info("Building response " + status.getStatusCode() + " " + status.getReasonPhrase());
		return responseBuilder.build();
    }
	
	protected Map<String, Object> buildMapFromOEntity(List<OProperty<?>> properties) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (OProperty<?> property : properties) {
			map.put(property.getName(), property.getValue());				
		}
		return map;
	}
	
    @SuppressWarnings("static-access")
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
				    	if (value != null)
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
    @Consumes({MediaType.APPLICATION_ATOM_XML, 
    	MediaType.APPLICATION_XML, 
    	ExtendedMediaTypes.APPLICATION_ATOMSVC_XML, 
    	MediaType.APPLICATION_JSON, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
    @Produces({MediaType.APPLICATION_ATOM_XML, 
    	MediaType.APPLICATION_XML, 
    	ExtendedMediaTypes.APPLICATION_ATOMSVC_XML, 
    	MediaType.APPLICATION_JSON, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
    public Response post( @Context HttpHeaders headers, @PathParam("id") String id, @Context UriInfo uriInfo, EntityResource<?> resource ) {
    	logger.info("POST " + getFQResourcePath());
    	assert(getResourcePath() != null);
    	if (!getCommandController().isValidCommand("POST", getFQResourcePath())) {
    		return buildResponse(headers, HttpStatusTypes.METHOD_NOT_ALLOWED, null, null, getInteractions(), null);
    	}
    	InteractionCommand postCommand = getCommandController().fetchCommand("POST", getFQResourcePath());
    	MultivaluedMap<String, String> queryParameters = uriInfo != null ? uriInfo.getQueryParameters(true) : null;
    	MultivaluedMap<String, String> pathParameters = uriInfo != null ? uriInfo.getPathParameters(true) : null;

    	// work around an issue in wink, wink does not decode query parameters in 1.1.3
    	decodeQueryParams(queryParameters);
    	// create the interaction context
    	InteractionContext ctx = new InteractionContext(pathParameters, queryParameters, getCurrentState());
    	// set the resource for the command to access
    	ctx.setResource(resource);
    	// execute commands
    	InteractionCommand.Result result = postCommand.execute(ctx);
    	// TODO need to add support for differed create (ACCEPTED) and actually created (CREATED)
   		StatusType status = result == Result.SUCCESS ? Status.CREATED : Status.INTERNAL_SERVER_ERROR;
    	return buildResponse(headers, status, ctx.getResource(), getLinks(headers, pathParameters, ctx.getResource()), null, null);
    }

    /**
	 * PUT a resource.
	 * @precondition a valid PUT command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 * @see com.temenos.interaction.core.rim.HTTPResourceInteractionModel#put(javax.ws.rs.core.HttpHeaders, java.lang.String, com.temenos.interaction.core.EntityResource)
	 */
    @Override
	@PUT
    @Consumes({MediaType.APPLICATION_ATOM_XML, 
    	MediaType.APPLICATION_XML, 
    	ExtendedMediaTypes.APPLICATION_ATOMSVC_XML, 
    	MediaType.APPLICATION_JSON, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
    public Response put( @Context HttpHeaders headers, @PathParam("id") String id, @Context UriInfo uriInfo, EntityResource<?> resource ) {
    	logger.info("PUT " + getFQResourcePath());
    	assert(getResourcePath() != null);
    	if (!getCommandController().isValidCommand("PUT", getFQResourcePath())) {
    		return buildResponse(headers, HttpStatusTypes.METHOD_NOT_ALLOWED, null, null, getInteractions(), null);
    	}
    	InteractionCommand putCommand = getCommandController().fetchCommand("PUT", getFQResourcePath());
    	MultivaluedMap<String, String> queryParameters = uriInfo != null ? uriInfo.getQueryParameters(true) : null;
    	MultivaluedMap<String, String> pathParameters = uriInfo != null ? uriInfo.getPathParameters(true) : null;

    	// work around an issue in wink, wink does not decode query parameters in 1.1.3
    	decodeQueryParams(queryParameters);
    	// create the interaction context
    	InteractionContext ctx = new InteractionContext(pathParameters, queryParameters, getCurrentState());
    	// set the resource for the command to access
    	ctx.setResource(resource);
    	// execute commands
    	InteractionCommand.Result result = putCommand.execute(ctx);
    	/*
    	 * The resource manager must return an error result code or have stored this 
    	 * resource in a consistent state (conceptually a transaction)
    	 */
    	/*
    	 * TODO add support for PUTs that create (CREATED) and PUTs that replace (OK)
    	 */
   		StatusType status = null;
   		if (result == Result.SUCCESS && ctx.getResource() == null) {
   			status = Status.NO_CONTENT;
   		} else if (result == Result.SUCCESS) {
   			status = Status.OK;
   		} else {
   			status = Status.INTERNAL_SERVER_ERROR;
   		}
   			
   		/*
   		 * TODO need to add support for list of commands
   		 */
    	return buildResponse(headers, status, ctx.getResource(), getLinks(headers, pathParameters, ctx.getResource()), null, null);
    }

	/**
	 * DELETE a resource.
	 * @precondition a valid DELETE command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 * @see com.temenos.interaction.core.rim.HTTPResourceInteractionModel#delete(javax.ws.rs.core.HttpHeaders, java.lang.String)
	 */
    @Override
	@DELETE
    public Response delete( @Context HttpHeaders headers, @PathParam("id") String id, @Context UriInfo uriInfo) {
    	logger.info("DELETE " + getFQResourcePath());
    	assert(getResourcePath() != null);
    	if (!getCommandController().isValidCommand("DELETE", getFQResourcePath())) {
    		return buildResponse(headers, HttpStatusTypes.METHOD_NOT_ALLOWED, null, null, getInteractions(), null);
    	}
    	InteractionCommand getCommand = getCommandController().fetchCommand("DELETE", getFQResourcePath());
    	MultivaluedMap<String, String> queryParameters = uriInfo != null ? uriInfo.getQueryParameters(true) : null;
    	MultivaluedMap<String, String> pathParameters = uriInfo != null ? uriInfo.getPathParameters(true) : null;

    	// work around an issue in wink, wink does not decode query parameters in 1.1.3
    	decodeQueryParams(queryParameters);
    	// create the interaction context
    	InteractionContext ctx = new InteractionContext(pathParameters, queryParameters, getCurrentState());
    	// execute command
    	InteractionCommand.Result result = getCommand.execute(ctx);
    	// We do not support a delete command that returns a resource (HTTP does permit this)
    	assert(ctx.getResource() == null);
    	StatusType status = null;
    	Link target = null;
    	if (result == Result.SUCCESS) {
    		LinkHeader linkHeader = null;
    		List<String> linkHeaders = headers.getRequestHeader("Link");
    		if (linkHeaders != null && linkHeaders.size() > 0) {
        		// there must be only one Link header
        		assert(linkHeaders.size() == 1);
    			linkHeader = LinkHeader.valueOf(linkHeaders.get(0));
    		}
        	Link linkUsed = hypermediaEngine.getLinkFromRelations(pathParameters, null, linkHeader);
    		/*
    		 * No target found using link relations, try to find a transition from ourself
    		 */
    		if (linkUsed == null) {
    			linkUsed = hypermediaEngine.getLinkFromMethod(pathParameters, null, currentState, "DELETE");
    		}
    		if (linkUsed != null) {
    			ResourceState targetState = linkUsed.getTransition().getTarget();
    			if (targetState.isTransientState()) {
    				Transition autoTransition = targetState.getAutoTransition();
    				if (autoTransition.getTarget().equals(linkUsed.getTransition().getSource())) {
                		// this transition has been configured to reset content
                   		status = HttpStatusTypes.RESET_CONTENT;
            		} else {
            			status = Status.SEE_OTHER;
            			target = hypermediaEngine.createLinkToTarget(autoTransition, ctx.getResource(), pathParameters);
            		}
    			} else if (targetState.isPseudoState() || targetState.equals(currentState)) {
        			// did we delete ourselves or pseudo final state, both are transitions to No Content
            		status = Response.Status.NO_CONTENT;
        		} else {
        			throw new IllegalArgumentException("Resource interaction exception, should not be " +
        					"possible to use a link where target state is not our current state");
        		}
    		} else {
    			// null target (pseudo final state) is effectively a transition to No Content
        		status = Response.Status.NO_CONTENT;
    		}
    	} else {
    		status = Status.NOT_FOUND;
    	}
    	
    	return buildResponse(headers, status, null, getLinks(headers, pathParameters, ctx.getResource()), null, target);
    }

	/**
	 * OPTIONS for a resource.
	 * @precondition a valid GET command for this resourcePath must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 */
    @Override
    public Response options( @Context HttpHeaders headers, @PathParam("id") String id, @Context UriInfo uriInfo ) {
    	logger.info("OPTIONS " + getFQResourcePath());
    	assert(getResourcePath() != null);
    	InteractionCommand getCommand = getCommandController().fetchCommand("GET", getFQResourcePath());
    	MultivaluedMap<String, String> queryParameters = uriInfo != null ? uriInfo.getQueryParameters(true) : null;
    	MultivaluedMap<String, String> pathParameters = uriInfo != null ? uriInfo.getPathParameters(true) : null;

    	// work around an issue in wink, wink does not decode query parameters in 1.1.3
    	decodeQueryParams(queryParameters);
    	// create the interaction context
    	InteractionContext ctx = new InteractionContext(pathParameters, queryParameters, getCurrentState());
    	
    	// TODO add support for OPTIONS /resource/* which will provide information about valid interactions for any entity
    	
    	// execute GET command
    	InteractionCommand.Result result = getCommand.execute(ctx);
    	StatusType status = result == Result.SUCCESS ? Status.NO_CONTENT : Status.NOT_FOUND;
    	return buildResponse(headers, status, null, getLinks(headers, pathParameters, ctx.getResource()), getInteractions(), null);
	}
    
    /**
     * Get the valid methods for interacting with this resource.
     * @return
     */
    public Set<String> getInteractions() {
    	Set<String> interactions = new HashSet<String>();
    	interactions.add("GET");
    	if (commandController.isValidCommand("PUT", getResourcePath())) {
        	interactions.add("PUT");
    	}
    	if (commandController.isValidCommand("POST", getResourcePath())) {
        	interactions.add("POST");
    	}
    	if (commandController.isValidCommand("DELETE", getResourcePath())) {
        	interactions.add("DELETE");
    	}
    	interactions.add("HEAD");
    	interactions.add("OPTIONS");
    	return interactions;
    }

	@Override
	public ResourceState getCurrentState() {
		return currentState;
	}

	@Override
	public Collection<Link> getLinks(HttpHeaders headers,
			MultivaluedMap<String, String> pathParameters, RESTResource entity) {
		return hypermediaEngine.getLinks(pathParameters, entity, getCurrentState(), headers.getRequestHeader("Link"));
	}
	
	public boolean equals(Object other) {
		//check for self-comparison
	    if ( this == other ) return true;
	    if ( !(other instanceof HTTPHypermediaRIM) ) return false;
	    HTTPHypermediaRIM otherResource = (HTTPHypermediaRIM) other;
	    return getFQResourcePath().equals(otherResource.getFQResourcePath());
	}
	
	public int hashCode() {
		return getFQResourcePath().hashCode();
	}

	public String toString() {
		return ("HTTPHypermediaRIM " + currentState.getId() + "[" + getFQResourcePath() + "]");
	}

}