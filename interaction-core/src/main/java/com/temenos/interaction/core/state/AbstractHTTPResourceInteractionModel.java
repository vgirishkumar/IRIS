package com.temenos.interaction.core.state;

import java.io.UnsupportedEncodingException;
import java.net.URI;
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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jaxrs.hateoas.HateoasContext;
import com.jayway.jaxrs.hateoas.core.HateoasResponse;
import com.jayway.jaxrs.hateoas.core.HateoasResponse.HateoasResponseBuilder;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;
import com.temenos.interaction.core.ExtendedMediaTypes;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.MethodNotAllowedCommand;
import com.temenos.interaction.core.command.ResourceCommand;
import com.temenos.interaction.core.command.ResourceDeleteCommand;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePostCommand;
import com.temenos.interaction.core.command.ResourcePutCommand;
import com.temenos.interaction.core.command.ResourceStatusCommand;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.TransitionCommandSpec;

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
	private final static Logger logger = LoggerFactory.getLogger(AbstractHTTPResourceInteractionModel.class);

	private String resourcePath;
	private ResourceRegistry resourceRegistry;
	private CommandController commandController;
		
	public AbstractHTTPResourceInteractionModel(String resourcePath) {
		this(resourcePath, null, new CommandController());
	}

	public AbstractHTTPResourceInteractionModel(String resourcePath, ResourceRegistry resourceRegistry, CommandController commandController) {
		this.resourcePath = resourcePath;
		// TODO extract resource registry into HTTPDynaRIM
		this.resourceRegistry = resourceRegistry;
		this.commandController = commandController;
		assert(this.commandController != null);
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

	@Override
	public ResourceInteractionModel getParent() {
		return null;
	}

	@Override
	public Collection<ResourceInteractionModel> getChildren() {
		return null;
	}

	/**
	 * We use links (also called hypermedia) for controlling / describing application 
	 * state.  This method returns a context object for the application state at the 
	 * time this resource was viewed.
	 */
	@Override
	public HateoasContext getHateoasContext() {
		return getResourceRegistry();
	}
    
		
    /*
     * The registry of all resources / application states in this application.
     */
    protected ResourceRegistry getResourceRegistry() {
    	return resourceRegistry;
    }

    public void setResourceRegistry(ResourceRegistry resourceRegistry) {
    	this.resourceRegistry = resourceRegistry;
    }

    /*
     * The map of all commands for http methods, paths, and media types.
     */
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
	@SuppressWarnings("unchecked")
	@Override
	@GET
    @Produces({MediaType.APPLICATION_ATOM_XML, 
    	MediaType.APPLICATION_XML, 
    	ExtendedMediaTypes.APPLICATION_ATOMSVC_XML, 
    	MediaType.APPLICATION_JSON, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, 
    	com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
    public Response get( @Context HttpHeaders headers, @PathParam("id") String id, @Context UriInfo uriInfo ) {
    	logger.debug("GET " + getFQResourcePath());
    	assert(getResourcePath() != null);
    	ResourceGetCommand getCommand = getCommandController().fetchGetCommand(getFQResourcePath());
    	MultivaluedMap<String, String> queryParameters = uriInfo != null ? uriInfo.getQueryParameters(true) : null;
    	// work around an issue in wink, wink does not decode query parameters in 1.1.3
    	decodeQueryParams(queryParameters);
    	
    	// debugging
    	MultivaluedMap<String, String> pathParameters = uriInfo != null ? uriInfo.getPathParameters(true) : null;
    	if (pathParameters != null) {
        	for (String pathParam : pathParameters.keySet()) {
        		System.out.println("PathParam " + pathParam + ":" + pathParameters.get(pathParam));
        	}
    	}
    	
    	RESTResponse response = getCommand.get(id, queryParameters);

    	assert (response != null);
    	StatusType status = response.getStatus();
		assert (status != null);  // not a valid get command
		if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
			assert(response.getResource() != null);

			// Wrap response into a JAX-RS GenericEntity object 
			GenericEntity<?> resource = response.getResource().getGenericEntity();
			
			// map of properties to supply to link builder
			Map<String, Object> map = null;
			
			// Rebuild resource links if necessary
			if (resourceRegistry != null) {
				if (ResourceTypeHelper.isType(resource.getRawType(), resource.getType(), EntityResource.class, OEntity.class)) {
					String entitySetName = getCurrentState().getEntityName();
					EdmEntitySet entitySet = resourceRegistry.getEntitySet(entitySetName);
					EdmEntityType entityType = entitySet.getType();

					EntityResource<OEntity> er = (EntityResource<OEntity>) resource.getEntity();
		    		OEntity oEntity = er.getEntity();
		        	
		    		// get the map of properties to supply to link builder
		    		map = buildMapFromOEntity(oEntity.getProperties());
		    		
		    		// get the links for this entity
		    		List<OLink> links = resourceRegistry.getNavigationLinks(entityType);
		        	// create a new entity as at the moment we pass the resource links in the OEntity
		        	OEntity oe = OEntities.create(entitySet, oEntity.getEntityKey(), oEntity.getProperties(), links);;
		        	EntityResource<OEntity> rebuilt = new EntityResource<OEntity>(oe) {};
		        	resource = rebuilt.getGenericEntity();
				} else if (ResourceTypeHelper.isType(resource.getRawType(), resource.getType(), CollectionResource.class)) {
					String entitySetName = getCurrentState().getEntityName();
					EdmEntitySet entitySet = resourceRegistry.getEntitySet(entitySetName);
					EdmEntityType entityType = entitySet.getType();

					CollectionResource<OEntity> cr = (CollectionResource<OEntity>) resource.getEntity();
					List<OEntity> entities = (List<OEntity>) cr.getEntities();
					List<OEntity> newEntities = new ArrayList<OEntity>();
					for (OEntity oEntity : entities) {
			    		// get the links for this entity
			    		List<OLink> links = resourceRegistry.getNavigationLinks(entityType);
			        	// create a new entity as at the moment we pass the resource links in the OEntity
			        	OEntity oe = OEntities.create(entitySet, oEntity.getEntityKey(), oEntity.getProperties(), links);
			        	newEntities.add(oe);
					}
					CollectionResource<OEntity> rebuilt = new CollectionResource<OEntity>(entitySetName, newEntities, null) {};
		        	resource = rebuilt.getGenericEntity();
				}
	    	}	    	

			// Create hypermedia representation for this resource
	    	HateoasResponseBuilder builder = HateoasResponse.status(status);
	    	if (getHateoasContext() != null) {
	    		buildLinks(builder, id, map);
	    	}
	    	builder.entity(resource);
	    	
			// Create the Response for this resource GET (representation created by the jax-rs Provider)
			return HeaderHelper.allowHeader(builder, getInteractions()).build();
		}
		return Response.status(status).build();
    }
    
	private void buildLinks(HateoasResponseBuilder builder, String id, Map<String, Object> map) {
		if (id != null) {
    		builder.selfLink(getHateoasContext(), getCurrentState().getId(), id);	    		
		} else {
    		builder.selfLink(getHateoasContext(), getCurrentState().getId());
		}
		
		Collection<ResourceState> targetStates = getCurrentState().getAllTargets();
		for (ResourceState s : targetStates) {
			TransitionCommandSpec cs = getCurrentState().getTransition(s).getCommand();
			// TODO remove this workaround, at the minute we don't support links that require dynamic value (URITemplates)
			if (cs.getPath() != null && !cs.getPath().contains("{")) {
				builder.link(getHateoasContext(), s.getId(), "rel=" + s.getId() + "+method=" + cs.getMethod());
			} else if (map != null && cs.getPath() != null) {
				UriBuilder linkTemplate = UriBuilder.fromPath(cs.getPath());
				URI link = linkTemplate.buildFromMap(map);
				System.out.println("Link: " +link.toString());
				
				builder.link(getHateoasContext(), getCurrentState().getId() + "." + s.getId(), s.getId(), map);
			}
		}
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
    public Response post( @Context HttpHeaders headers, @PathParam("id") String id, EntityResource<?> resource ) {
    	logger.debug("POST " + getFQResourcePath());
    	assert(getResourcePath() != null);
    	ResourceCommand c = getCommandController().fetchStateTransitionCommand("POST", getFQResourcePath());

    	StatusType status = null;
    	RESTResponse response = null;
    	if (c instanceof ResourcePostCommand) {
    		ResourcePostCommand postCommand = (ResourcePostCommand) c;
        	response = postCommand.post(id, resource);
        	assert (response != null);
        	status = response.getStatus();
    	} else if (c instanceof ResourceStatusCommand) {
       		status = ((ResourceStatusCommand) c).getStatus();    		
    	}
    	assert (status != null);  // not a valid post command

		if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
			assert(response.getResource() != null);
			// Wrap response into a JAX-RS GenericEntity object 
			GenericEntity<?> newResource = response.getResource().getGenericEntity();

			// map of properties to supply to link builder
			Map<String, Object> map = null;

			// Rebuild resource links if necessary
			if (resourceRegistry != null) {
				if (ResourceTypeHelper.isType(newResource.getRawType(), newResource.getType(), EntityResource.class)) {
					String entitySetName = getCurrentState().getEntityName();
					EdmEntitySet entitySet = resourceRegistry.getEntitySet(entitySetName);
					EdmEntityType entityType = entitySet.getType();

					@SuppressWarnings("unchecked")
					EntityResource<OEntity> er = (EntityResource<OEntity>) newResource.getEntity();
		    		OEntity oEntity = er.getEntity();
		        			        	
		    		// get the map of properties to supply to link builder
		    		map = buildMapFromOEntity(oEntity.getProperties());

		    		// get the links for this entity
		    		List<OLink> links = resourceRegistry.getNavigationLinks(entityType);
		        	// create a new entity as at the moment we pass the resource links in the OEntity
		        	OEntity oe = OEntities.create(entitySet, oEntity.getEntityKey(), oEntity.getProperties(), links);;
		        	EntityResource<OEntity> rebuilt = new EntityResource<OEntity>(oe) {};
		        	newResource = rebuilt.getGenericEntity();
				} else if (ResourceTypeHelper.isType(newResource.getRawType(), newResource.getType(), CollectionResource.class)) {
					assert(false);  // don't expect a collection here
				}
	    	}	    	

			// Create hypermedia representation for this resource
	    	HateoasResponseBuilder builder = HateoasResponse.status(status);
	    	if (getHateoasContext() != null) {
	    		buildLinks(builder, id, map);
	    	}
	    	builder.entity(newResource);
			
			return HeaderHelper.allowHeader(builder, getInteractions()).build();
		} else if (status.equals(MethodNotAllowedCommand.HTTP_STATUS_METHOD_NOT_ALLOWED)) {
			ResponseBuilder rb = Response.status(status);
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
    @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response put( @Context HttpHeaders headers, @PathParam("id") String id, EntityResource<?> resource ) {
    	logger.debug("PUT " + getFQResourcePath());
    	assert(getResourcePath() != null);
    	ResourceCommand c = getCommandController().fetchStateTransitionCommand("PUT", getFQResourcePath());

    	StatusType status = null;
    	if (c instanceof ResourcePutCommand) {
    		ResourcePutCommand putCommand = (ResourcePutCommand) c;
    		status = putCommand.put(id, resource);
    	} else if (c instanceof ResourceStatusCommand) {
       		status = ((ResourceStatusCommand) c).getStatus();    		
    	}
		assert (status != null);  // not a valid put command
		
		if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
        	return get(headers, id, null);
		} else if (status.equals(MethodNotAllowedCommand.HTTP_STATUS_METHOD_NOT_ALLOWED)) {
			ResponseBuilder rb = Response.status(status);
			return HeaderHelper.allowHeader(rb, getInteractions()).build();
    	}
   		return Response.status(status).build();
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
    	assert(getResourcePath() != null);
    	ResourceCommand c = getCommandController().fetchStateTransitionCommand("DELETE", getFQResourcePath());
    	StatusType status = null;
    	if (c instanceof ResourceDeleteCommand) {
        	ResourceDeleteCommand deleteCommand = (ResourceDeleteCommand) c;
  			status = deleteCommand.delete(id);
    	} else if (c instanceof ResourceStatusCommand) {
       		status = ((ResourceStatusCommand) c).getStatus();    		
		}
		assert (status != null);  // not a valid delete command
    	if (status.equals(MethodNotAllowedCommand.HTTP_STATUS_METHOD_NOT_ALLOWED)) {
			ResponseBuilder rb = Response.status(status);
			return HeaderHelper.allowHeader(rb, getInteractions()).build();
    	} else {
       		return Response.status(status).build();
    	}
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
    	assert(getResourcePath() != null);
    	ResourceGetCommand getCommand = getCommandController().fetchGetCommand(getFQResourcePath());
    	ResponseBuilder response = Response.ok();
    	RESTResponse rResponse = getCommand.get(id, null);
    	assert (rResponse != null);
    	StatusType status = rResponse.getStatus();
		assert (status != null);  // not a valid get command
		if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
        	response = HeaderHelper.allowHeader(response, getInteractions());
    	}
    	return response.build();
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