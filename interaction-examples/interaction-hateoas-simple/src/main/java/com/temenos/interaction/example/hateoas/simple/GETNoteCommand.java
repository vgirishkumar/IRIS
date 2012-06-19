package com.temenos.interaction.example.hateoas.simple;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.hateoas.simple.model.Note;

public class GETNoteCommand implements ResourceGetCommand {

	private Persistence persistence;
	
	public GETNoteCommand(Persistence p) {
		persistence = p;
	}

	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		// retrieve from a database, etc.
		return new RESTResponse(Status.OK, new EntityResource<Note>(persistence.getNote(new Long(id))));
	}

}
