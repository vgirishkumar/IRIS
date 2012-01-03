package com.temenos.interaction.core.media;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.RESTResponse;

public class JSONDecorator implements Decorator<Response> {

	public JSONDecorator() {
	}

	public Response decorateRESTResponse(RESTResponse r) {
		if (r == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		StatusType status = r.getStatus();
		if (status == Response.Status.OK) {

			
			StringBuilder json = new StringBuilder();
			// form body
			json.append("huh");
			// form links
			json.append("huh2");
			
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(status).build();
		}
	}

}
