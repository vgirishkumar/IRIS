package com.temenos.interaction.example.mashup.twitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.mashup.twitter.model.Tweet;

public class GETUserTwitterUpdatesCommand implements InteractionCommand {

	public GETUserTwitterUpdatesCommand() {}
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		List<EntityResource<Tweet>> tweetEntities = new ArrayList<EntityResource<Tweet>>();
		
		// this command designed to be used with {username} in the path e.g.  /tweets/{username]
		Collection<Tweet> tweets = new Twitter4JConsumer().requestTweetsByUser(ctx.getPathParameters().getFirst("username"));
		for (Tweet t : tweets) {
			tweetEntities.add(new EntityResource<Tweet>(t));
		}
		CollectionResource<Tweet> usersResource = new CollectionResource<Tweet>(tweetEntities);
		ctx.setResource(usersResource);
		return Result.SUCCESS;
	}

}
