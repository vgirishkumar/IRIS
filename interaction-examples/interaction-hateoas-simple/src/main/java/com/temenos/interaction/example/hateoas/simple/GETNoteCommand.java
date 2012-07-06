package com.temenos.interaction.example.hateoas.simple;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.hateoas.simple.model.Note;

public class GETNoteCommand implements ResourceGetCommand, InteractionCommand {

	private Persistence persistence;
	
	public GETNoteCommand(Persistence p) {
		persistence = p;
	}

	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		// retrieve from a database, etc.
		Note note = persistence.getNote(new Long(id));
		if (note != null) {
			return new RESTResponse(Status.OK, new EntityResource<Note>(note));
		} else {
			return new RESTResponse(Status.NOT_FOUND, null);
		}
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
			return Result.FAILURE;
		}
	}

	@Override
	public String getMethod() {
		return HttpMethod.GET;
	}

}
