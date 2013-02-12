package com.temenos.interaction.core.hypermedia.expression;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public interface Expression {

	/**
	 * Evaluate this expression and return a boolean result.
	 * @param pathParams
	 * @return
	 */
	public boolean evaluate(ResourceStateMachine hypermediaEngine, InteractionContext ctx);
}
