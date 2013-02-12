package com.temenos.interaction.core.hypermedia.expression;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class TestSimpleLogicalExpressionEvaluator {

	private Expression createFalseExpression() {
		Expression expression = mock(Expression.class);
		when(expression.evaluate(any(ResourceStateMachine.class), any(InteractionContext.class))).thenReturn(false);
		return expression;
	}

	private Expression createTrueExpression() {
		Expression expression = mock(Expression.class);
		when(expression.evaluate(any(ResourceStateMachine.class), any(InteractionContext.class))).thenReturn(true);
		return expression;
	}

	@Test
	public void testNoExpressions() {
		List<Expression> expressions = new ArrayList<Expression>();
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertTrue(expEvaluator.evaluate(mock(ResourceStateMachine.class), mock(InteractionContext.class)));
	}

	@Test
	public void testOneTrueExpression() {
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(createTrueExpression());
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertTrue(expEvaluator.evaluate(mock(ResourceStateMachine.class), mock(InteractionContext.class)));
	}
	
	@Test
	public void testOneFalseExpression() {
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(createFalseExpression());
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertFalse(expEvaluator.evaluate(mock(ResourceStateMachine.class), mock(InteractionContext.class)));
	}

	@Test
	public void testTrueFalseExpression() {
		Expression trueEX = createTrueExpression();
		Expression falseEX = createFalseExpression();

		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(trueEX);
		expressions.add(falseEX);
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertFalse(expEvaluator.evaluate(mock(ResourceStateMachine.class), mock(InteractionContext.class)));
		verify(trueEX).evaluate(any(ResourceStateMachine.class), any(InteractionContext.class));
		verify(falseEX).evaluate(any(ResourceStateMachine.class), any(InteractionContext.class));
	}

	@Test
	public void testFalseTrueExpression() {
		Expression falseEX = createFalseExpression();
		Expression trueEX = createTrueExpression();
		
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(falseEX);
		expressions.add(trueEX);
		SimpleLogicalExpressionEvaluator expEvaluator = new SimpleLogicalExpressionEvaluator(expressions);

		assertFalse(expEvaluator.evaluate(mock(ResourceStateMachine.class), mock(InteractionContext.class)));
		verify(falseEX).evaluate(any(ResourceStateMachine.class), any(InteractionContext.class));
		verify(trueEX, never()).evaluate(any(ResourceStateMachine.class), any(InteractionContext.class));
	}

}
