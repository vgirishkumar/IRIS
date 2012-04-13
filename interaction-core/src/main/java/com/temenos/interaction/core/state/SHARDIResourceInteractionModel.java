package com.temenos.interaction.core.state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.StatusType;

import com.jayway.jaxrs.hateoas.HateoasContext;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.ExtendedMediaTypes;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.link.ResourceState;

/**
 * Define the T24 SEE, HISTORY, AUTHORISE, REVERSE, DELETE, INPUT 'SHARDI' Resource Interaction Model.
 * 'S' - See
 * 'H' - History
 * 'A' - Authorise
 * 'R' - Reverse
 * 'D' - Delete
 * 'I' - Input
 * @author aphethean
 */
public abstract class SHARDIResourceInteractionModel implements ResourceInteractionModel {

	/**
	 * Indicates that the annotated method responds to HTTP AUTHORISE requests
	 * @see HttpMethod
	 */
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@HttpMethod("AUTHORISE")
	public @interface AUTHORISE { 
	}

//    private @Context UriInfo uriInfo;
	private CommandController commandController;
	private String resourcePath = null;
	
	public SHARDIResourceInteractionModel(String resourcePath) {
		this.resourcePath = resourcePath;
		this.commandController = new CommandController();
	}
	
	public ResourceState getCurrentState() {
		return null;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
	
	// TODO REMOVE this class, or this duplication
	public String getFQResourcePath() {
		String result = "";
		if (getParent() != null)
			result += getParent().getResourcePath();
			
		return result += resourcePath;
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
	 * Override this method to provide support for application state or links.
	 */
	@Override
	public HateoasContext getHateoasContext() {
		return null;
	}

	public void registerGetCommand(ResourceGetCommand c) {
		commandController.setGetCommand(resourcePath, c);
		System.out.println("Registered GET command [" + resourcePath + "]");
	}
	
    @GET
    @Produces({MediaType.APPLICATION_XML})
    public Response getXML( @PathParam("id") String id ) {
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(resourcePath);
    	RESTResponse rResponse = getCommand.get(id, null);
    	assert (rResponse != null);
    	StatusType status = rResponse.getStatus();
		assert (status != null);  // not a valid get command
   // 	Decorator<Response> d = new XMLDecorator();
   // 	Response xml = d.decorateRESTResponse(rResponse);
		Response xml = Response.ok(rResponse.getResource()).build();
    	return HeaderHelper.allowHeader(Response.fromResponse(xml), getInteractions()).build();
}
	
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson( @PathParam("id") String id ) {
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(resourcePath);
    	RESTResponse rResponse = getCommand.get(id, null);
    	assert (rResponse != null);
    	StatusType status = rResponse.getStatus();
		assert (status != null);  // not a valid get command
//    	Decorator<StreamingOutput> d = new JSONStreamingDecorator();
//    	StreamingOutput so = d.decorateRESTResponse(rResponse);
		Response json = Response.ok(rResponse.getResource()).build();
    	return HeaderHelper.allowHeader(Response.fromResponse(json), getInteractions()).build();
    }

    /*
    @GET
    @Produces(com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML)
    public Response getHALXml( @PathParam("id") String id ) {
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(getResourcePath());
    	Response.Status status = getCommand.get(id);
    	Decorator<Response> d = new HALXMLDecorator();
    	Response r = d.decorateRESTResponse(new RESTResponse(status, getCommand.getResource()));
    	return HeaderHelper.allowHeader(Response.ok(r), getCommand).build();
    }
    */
    
    @GET
    @Produces(ExtendedMediaTypes.APPLICATION_ODATA_XML)
    public Response getODATAXml( @PathParam("id") String id ) {
    	throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
    
    @AUTHORISE
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorise( @PathParam("id") String id ) {
    	throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
    
    public Response options(String id ) {
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
     * Get the valid methods for interacting with this resource.
     * @return
     */
    public Set<String> getInteractions() {
    	Set<String> interactions = new HashSet<String>();
    	interactions.add("GET");  // SEE
    	interactions.add("HISTORY");
    	interactions.add("AUTHORISE");
    	interactions.add("REVERSE");
    	interactions.add("DELETE");
    	interactions.add("INPUT");
    	interactions.add("HEAD");
    	interactions.add("OPTIONS");
    	return interactions;
    }

}
