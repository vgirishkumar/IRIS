package com.temenos.interaction.example.hateoas.simple;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.hateoas.simple.model.Note;

public class GETNoteCommand implements InteractionCommand {

	private Persistence persistence;
	
	public GETNoteCommand(Persistence p) {
		persistence = p;
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		// retrieve from a database, etc.
		String id = ctx.getId();
		Note note = persistence.getNote(new Long(id));
		if (note != null) {
			ctx.setResource(new EntityResource<Note>(note));
			return Result.SUCCESS;
		} else {
			return Result.RESOURCE_UNAVAILABLE;
		}
	}

}
