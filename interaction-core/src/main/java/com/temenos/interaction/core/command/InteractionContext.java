package com.temenos.interaction.core.command;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.rim.HTTPHypermediaRIM;

/**
 * This object holds the execution context for processing of the 
 * interaction commands.  {@link InteractionCommand}
 * @author aphethean
 */
public class InteractionContext {
	private final static Logger logger = LoggerFactory.getLogger(InteractionContext.class);

	/**
	 * The default path element key used if no other is specified when defining the resource.
	 */
	public final static String DEFAULT_ID_PATH_ELEMENT = "id";
	
	/* Execution context */
	private final MultivaluedMap<String, String> queryParameters;
	private final MultivaluedMap<String, String> pathParameters;
	private final ResourceState currentState;
	
	/* Command context */
	private RESTResource resource;
	
	/**
	 * Construct the context for execution of an interaction.
	 * @see HTTPHypermediaRIM for pre and post conditions of this InteractionContext
	 * 			following the execution of a command.
	 * @invariant pathParameters not null
	 * @invariant queryParameters not null
	 * @invariant currentState not null
	 * @param pathParameters
	 * @param queryParameters
	 */
	public InteractionContext(MultivaluedMap<String, String> pathParameters, MultivaluedMap<String, String> queryParameters, ResourceState currentState) {
		this.pathParameters = pathParameters;
		this.queryParameters = queryParameters;
		this.currentState = currentState;
		assert(pathParameters != null);
		assert(queryParameters != null);
		assert(currentState != null);
	}

	/**
	 * <p>The query part of the uri (after the '?')</p>
	 * URI query parameters as a result of jax-rs {@link UriInfo#getQueryParameters(true)}
	 */
	public MultivaluedMap<String, String> getQueryParameters() {
		return queryParameters;
	}

	/**
	 * <p>the path part of the uri (up to the '?')</p>
	 * URI path parameters as a result of jax-rs {@link UriInfo#getPathParameters(true)}
	 */
	public MultivaluedMap<String, String> getPathParameters() {
		return pathParameters;
	}

	/**
	 * The object form of the resource this interaction is dealing with.
	 * @return
	 */
	public RESTResource getResource() {
		return resource;
	}

	/**
	 * In terms of the hypermedia interactions this is the current application state.
	 * @return
	 */
	public ResourceState getCurrentState() {
		return currentState;
	}

	/**
	 * @see InteractionContext#getResource()
	 * @param resource
	 */
	public void setResource(RESTResource resource) {
		this.resource = resource;
	}

    public String getId() {
    	String id = pathParameters.getFirst(DEFAULT_ID_PATH_ELEMENT);
    	if (pathParameters != null) {
    		if (getCurrentState().getPathIdParameter() != null) {
    			id = pathParameters.getFirst(getCurrentState().getPathIdParameter());
    		}
    		if (logger.isDebugEnabled()) {
            	for (String pathParam : pathParameters.keySet()) {
            		logger.debug("PathParam " + pathParam + ":" + pathParameters.get(pathParam));
            	}
    		}
    	}
    	return id;
    }

}
