package com.temenos.interaction.core.hypermedia.expression;

import java.util.List;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

/**
 * A very simple Expression implementation that supports left to right, 'AND' expression
 * evaluation (short-circuiting logical expressions with just the logical AND operator)
 * @author aphethean
 */
public class SimpleLogicalExpressionEvaluator implements Expression {

	private final List<Expression> expressions;
	
	public SimpleLogicalExpressionEvaluator(List<Expression> expressions) {
		this.expressions = expressions;
		assert(this.expressions != null);
	}
	
	@Override
	public boolean evaluate(ResourceStateMachine hypermediaEngine, InteractionContext ctx) {
		for (Expression e : expressions) {
			if (!e.evaluate(hypermediaEngine, ctx))
				return false;
		}
		return true;
	}
}
