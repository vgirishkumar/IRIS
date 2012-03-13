package com.temenos.interaction.example.sandbox;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.resource.EntityResource;

@Path("/sandbox/{id}")
public class SandboxRIM {

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response put( @Context HttpHeaders headers, @PathParam("id") String id, EntityResource<?> resource) {
    	if (resource != null) {
        	return Response.ok(new EntityResource<Book>(new Book("Farms", "A kids book"))).build();
    	} else {
    		return Response.status(Status.BAD_REQUEST).build();
    	}
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response get(@PathParam("id") String id) {
//       	return Response.ok(new EntityResource(new Book("Farms", "A kids book"))).build();
       	return Response.ok(new Book("Farms", "A kids book")).build();
    }

}
