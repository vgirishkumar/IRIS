package com.temenos.interaction.example.hateoas.simple;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public class GETNotesCommand implements ResourceGetCommand {

	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		List<EntityResource<Note>> notes = new ArrayList<EntityResource<Note>>();
		notes.add(new EntityResource<Note>(new Note(1L, "Test note one")));
		notes.add(new EntityResource<Note>(new Note(2L, "Test note two")));
		CollectionResource<Note> notesResource = new CollectionResource<Note>("note", notes);
		return new RESTResponse(Status.OK, notesResource);
	}

}
