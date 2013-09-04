package com.interaction.example.odata.airline.commands;

import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.commands.odata.CommandHelper;
import com.temenos.interaction.core.command.HttpStatusTypes;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

public class ThrowInteractionExceptionCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert(ctx != null);
		int statusCode = Integer.valueOf(CommandHelper.getViewActionProperty(ctx, "status")).intValue(); 
		String message = CommandHelper.getViewActionProperty(ctx, "message");
		StatusType status = HttpStatusTypes.fromStatusCode(statusCode);
		
		throw new InteractionException(status, message);
	}
}
