package com.interaction.example.odata.airline.commands;

import com.temenos.interaction.commands.odata.CommandHelper;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.GenericError;

public class GETGenericErrorCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		String code = CommandHelper.getViewActionProperty(ctx, "code"); 
		String message = CommandHelper.getViewActionProperty(ctx, "message"); 
		if(code != null) {
			ctx.setResource(CommandHelper.createEntityResource(new GenericError(code.toString(), message)));
		}
		return Result.valueOf(code);
	}
}
