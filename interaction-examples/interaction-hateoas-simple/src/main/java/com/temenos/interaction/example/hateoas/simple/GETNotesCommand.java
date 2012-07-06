package com.temenos.interaction.example.hateoas.simple;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.hateoas.simple.model.Note;

public class GETNotesCommand implements ResourceGetCommand, InteractionCommand {

	private Persistence persistence;
	
	public GETNotesCommand(Persistence p) {
		persistence = p;
	}
	
	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		List<EntityResource<Note>> noteEntities = new ArrayList<EntityResource<Note>>();
		List<Note> notes = persistence.getNotes();
		for (Note n : notes) {
			noteEntities.add(new EntityResource<Note>(n));
		}
		CollectionResource<Note> notesResource = new CollectionResource<Note>("note", noteEntities);
		return new RESTResponse(Status.OK, notesResource);
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		List<EntityResource<Note>> noteEntities = new ArrayList<EntityResource<Note>>();
		List<Note> notes = persistence.getNotes();
		for (Note n : notes) {
			noteEntities.add(new EntityResource<Note>(n));
		}
		CollectionResource<Note> notesResource = new CollectionResource<Note>("note", noteEntities);
		ctx.setResource(notesResource);
		return Result.SUCCESS;
	}

	@Override
	public String getMethod() {
		return HttpMethod.GET;
	}

}
