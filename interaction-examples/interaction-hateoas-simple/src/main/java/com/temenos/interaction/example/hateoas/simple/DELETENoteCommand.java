package com.temenos.interaction.example.hateoas.simple;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.command.HttpStatusTypes;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.ResourceDeleteCommand;

public class DELETENoteCommand implements ResourceDeleteCommand, InteractionCommand {

	private Persistence persistence;

	public DELETENoteCommand(Persistence p) {
		this.persistence = p;
	}
	
	@Override
	public StatusType delete(String id) {
		// delete from a database, etc.
		persistence.removeNote(new Long(id));
		return HttpStatusTypes.RESET_CONTENT;
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		// delete from a database, etc.
		String id = ctx.getId();
		persistence.removeNote(new Long(id));
		return Result.SUCCESS;
	}

	@Override
	public String getMethod() {
		return HttpMethod.DELETE;
	}

}
