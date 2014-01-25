package com.temenos.interaction.core.hypermedia.expression;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Expression e : expressions)
			sb.append(e);		
		return sb.toString();
	}
}
