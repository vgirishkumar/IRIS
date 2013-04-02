package com.temenos.interaction.commands.mule;

import javax.ws.rs.core.MultivaluedMap;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class ViewCommand implements InteractionCommand {

	public Result execute(InteractionContext ctx) {

		MultivaluedMap<String, String> pathParams = ctx.getPathParameters();
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
		
		return null;
	}

}
