package com.temenos.interaction.example.mashup.twitter;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.mashup.twitter.model.User;

public class GETUserCommand implements InteractionCommand {

	private Persistence persistence;
	
	public GETUserCommand(Persistence p) {
		persistence = p;
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		// retrieve from a database, etc.
		String id = ctx.getId();
		User user = persistence.getUser(new Long(id));
		if (user != null) {
			ctx.setResource(new EntityResource<User>(user));
			return Result.SUCCESS;
		} else {
			return Result.FAILURE;
		}
	}

}
