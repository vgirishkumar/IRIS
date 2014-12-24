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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.rim.HTTPHypermediaRIM;

public class TestSimpleLogicalExpressionEvaluator {

	private Expression createFalseExpression() {
		Expression expression = mock(Expression.class);
		when(expression.evaluate(any(HTTPHypermediaRIM.class), any(InteractionContext.class), any(EntityResource.class))).thenReturn(false);
		return expression;
	}

	private Expression createTrueExpression() {
		Expression expression = mock(Expression.class);
		when(expression.evaluate(any(HTTPHypermediaRIM.class), any(InteractionContext.class), any(EntityResource.class))).thenReturn(true);
		return expression;
	}

	@Test
	public void testNoExpressions() {
		List<Expression> expressions = new ArrayList<Expression>();
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertTrue(expEvaluator.evaluate(mock(HTTPHypermediaRIM.class), mock(InteractionContext.class), null));
	}

	@Test
	public void testOneTrueExpression() {
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(createTrueExpression());
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertTrue(expEvaluator.evaluate(mock(HTTPHypermediaRIM.class), mock(InteractionContext.class), null));
	}
	
	@Test
	public void testOneFalseExpression() {
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(createFalseExpression());
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertFalse(expEvaluator.evaluate(mock(HTTPHypermediaRIM.class), mock(InteractionContext.class), null));
	}

	@Test
	public void testTrueFalseExpression() {
		Expression trueEX = createTrueExpression();
		Expression falseEX = createFalseExpression();

		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(trueEX);
		expressions.add(falseEX);
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertFalse(expEvaluator.evaluate(mock(HTTPHypermediaRIM.class), mock(InteractionContext.class), null));
		verify(trueEX).evaluate(any(HTTPHypermediaRIM.class), any(InteractionContext.class), any(EntityResource.class));
		verify(falseEX).evaluate(any(HTTPHypermediaRIM.class), any(InteractionContext.class), any(EntityResource.class));
	}

	@Test
	public void testFalseTrueExpression() {
		Expression falseEX = createFalseExpression();
		Expression trueEX = createTrueExpression();
		
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(falseEX);
		expressions.add(trueEX);
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertFalse(expEvaluator.evaluate(mock(HTTPHypermediaRIM.class), mock(InteractionContext.class), null));
		verify(falseEX).evaluate(any(HTTPHypermediaRIM.class), any(InteractionContext.class), any(EntityResource.class));
		verify(trueEX, never()).evaluate(any(HTTPHypermediaRIM.class), any(InteractionContext.class), any(EntityResource.class));
	}

}
