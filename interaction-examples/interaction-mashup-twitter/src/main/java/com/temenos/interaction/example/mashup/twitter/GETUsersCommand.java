package com.temenos.interaction.example.mashup.twitter;

import java.util.ArrayList;
import java.util.List;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.mashup.twitter.model.User;

public class GETUsersCommand implements InteractionCommand {

	private Persistence persistence;
	
	public GETUsersCommand(Persistence p) {
		persistence = p;
	}
	
	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		List<EntityResource<User>> userEntities = new ArrayList<EntityResource<User>>();
		List<User> users = persistence.getUsers();
		for (User u : users) {
			userEntities.add(new EntityResource<User>(u));
		}
		CollectionResource<User> usersResource = new CollectionResource<User>("user", userEntities);
		ctx.setResource(usersResource);
		return Result.SUCCESS;
	}

}
