package com.temenos.interaction.example.hateoas.simple;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class DELETENoteCommand implements InteractionCommand {

	private Persistence persistence;

	public DELETENoteCommand(Persistence p) {
		this.persistence = p;
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

}
