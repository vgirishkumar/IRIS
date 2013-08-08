package com.interaction.example.odata.airline.commands;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class GETThrowRuntimeExceptionCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		throw new RuntimeException("Unknown fatal error");
	}
	
}
