package com.temenos.interaction.example.hateoas.simple;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.hateoas.simple.model.Note;

public class GETNotesCommand implements InteractionCommand {

	private Persistence persistence;
	
	public GETNotesCommand(Persistence p) {
		persistence = p;
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

}
