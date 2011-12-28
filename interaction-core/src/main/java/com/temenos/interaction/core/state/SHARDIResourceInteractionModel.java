package com.temenos.interaction.core.state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.ExtendedMediaTypes;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.decorator.Decorator;
import com.temenos.interaction.core.decorator.JSONStreamingDecorator;
import com.temenos.interaction.core.decorator.PDFDecorator;
import com.temenos.interaction.core.decorator.XMLDecorator;

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
public abstract class SHARDIResourceInteractionModel implements ResourceStateTransition {

	/**
	 * Indicates that the annotated method responds to HTTP AUTHORISE requests
	 * @see HttpMethod
	 */
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@HttpMethod("AUTHORISE")
	public @interface AUTHORISE { 
	}

    private @Context UriInfo uriInfo;
	private CommandController commandController = new CommandController();
	private String resourcePath = null;
	
	/* Keep JAXB happy */
	public SHARDIResourceInteractionModel() {}

	public SHARDIResourceInteractionModel(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
	
	public void registerGetCommand(String resourcePath, ResourceGetCommand c) {
		commandController.addGetCommand(resourcePath, c);
		System.out.println("Registered GET command [" + resourcePath + "]");
	}
	
    @GET
    @Produces({MediaType.APPLICATION_XML})
    public Response getXML( @PathParam("id") String id ) {
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(getResourcePath());
    	RESTResponse rResponse = getCommand.get(id);
    	assert (rResponse != null);
    	StatusType status = rResponse.getStatus();
		assert (status != null);  // not a valid get command
   // 	Decorator<Response> d = new XMLDecorator();
   // 	Response xml = d.decorateRESTResponse(rResponse);
		Response xml = Response.ok(rResponse.getResource()).build();
    	return HeaderHelper.allowHeader(Response.fromResponse(xml), rResponse).build();
}
	
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson( @PathParam("id") String id ) {
    	ResourceGetCommand getCommand = commandController.fetchGetCommand(getResourcePath());
    	RESTResponse rResponse = getCommand.get(id);
    	assert (rResponse != null);
    	StatusType status = rResponse.getStatus();
		assert (status != null);  // not a valid get command
//    	Decorator<StreamingOutput> d = new JSONStreamingDecorator();
//    	StreamingOutput so = d.decorateRESTResponse(rResponse);
		Response json = Response.ok(rResponse.getResource()).build();
    	return HeaderHelper.allowHeader(Response.fromResponse(json), rResponse).build();
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
    
    @GET
    @Produces(ExtendedMediaTypes.APPLICATION_PDF)
    public StreamingOutput getPDF( @PathParam("id") String id ) {
   		ResourceGetCommand getCommand = (ResourceGetCommand) commandController.fetchGetCommand(getResourcePath());
    	RESTResponse rResponse = getCommand.get(id);
    	assert (rResponse != null);
    	StatusType status = rResponse.getStatus();
		assert (status != null);  // not a valid get command
       	Decorator<StreamingOutput> d = new PDFDecorator();
       	return d.decorateRESTResponse(rResponse);
    }

    @AUTHORISE
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorise( @PathParam("id") String id ) {
    	throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
    
    public Response options(String id ) {
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
