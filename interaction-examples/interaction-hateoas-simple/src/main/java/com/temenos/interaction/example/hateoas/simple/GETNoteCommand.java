package com.temenos.interaction.example.hateoas.simple;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.EntityResource;

public class GETNoteCommand implements ResourceGetCommand {

	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		// retrieve from a database, etc.
		return new RESTResponse(Status.OK, new EntityResource<Note>(new Note(new Long(id), "body[" + id + "]")));
	}

}
