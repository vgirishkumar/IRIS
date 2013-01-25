package com.temenos.interaction.core.workflow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;

public class TestNaiveWorkflowStrategyCommand {

	@Test
	public void testAllCommandsExecutedConstructor() {
		InteractionCommand command1 = mock(InteractionCommand.class);
		InteractionCommand command2 = mock(InteractionCommand.class);
		InteractionContext ctx = mock(InteractionContext.class);
		
		List<InteractionCommand> commands = new ArrayList<InteractionCommand>();
		commands.add(command1);
		commands.add(command2);
		NaiveWorkflowStrategyCommand w = new NaiveWorkflowStrategyCommand(commands);
		
		w.execute(ctx);
		verify(command1, times(1)).execute(ctx);
		verify(command2, times(1)).execute(ctx);
	}

	@Test
	public void testAllCommandsExecuted() {
		InteractionCommand command1 = mock(InteractionCommand.class);
		InteractionCommand command2 = mock(InteractionCommand.class);
		InteractionContext ctx = mock(InteractionContext.class);
		
		NaiveWorkflowStrategyCommand w = new NaiveWorkflowStrategyCommand();
		w.addCommand(command1);
		w.addCommand(command2);
		
		w.execute(ctx);
		verify(command1, times(1)).execute(ctx);
		verify(command2, times(1)).execute(ctx);
	}

	@Test
	public void testAllCommandsExecutedWhereOneFails() {
		InteractionCommand command1 = mock(InteractionCommand.class);
		when(command1.execute(any(InteractionContext.class))).thenReturn(Result.FAILURE);
		InteractionCommand command2 = mock(InteractionCommand.class);
		when(command2.execute(any(InteractionContext.class))).thenReturn(Result.SUCCESS);
		InteractionContext ctx = mock(InteractionContext.class);
		
		NaiveWorkflowStrategyCommand w = new NaiveWorkflowStrategyCommand();
		w.addCommand(command1);
		w.addCommand(command2);
		
		Result result = w.execute(ctx);
		assertEquals(Result.SUCCESS, result);
		verify(command1, times(1)).execute(ctx);
		verify(command2, times(1)).execute(ctx);
	}

	@Test
	public void testResultOfLastCommand() {
		InteractionCommand command1 = mock(InteractionCommand.class);
		InteractionCommand command2 = mock(InteractionCommand.class);
		when(command2.execute(any(InteractionContext.class))).thenReturn(Result.FAILURE);
		InteractionContext ctx = mock(InteractionContext.class);
		
		NaiveWorkflowStrategyCommand w = new NaiveWorkflowStrategyCommand();
		w.addCommand(command1);
		w.addCommand(command2);
		
		Result result = w.execute(ctx);
		assertEquals(Result.FAILURE, result);
	}

	@Test(expected = AssertionError.class)
	public void testNoCommands() {
		NaiveWorkflowStrategyCommand w = new NaiveWorkflowStrategyCommand();
		w.execute(mock(InteractionContext.class));
	}

	@Test(expected = AssertionError.class)
	public void testNullContext() {
		NaiveWorkflowStrategyCommand w = new NaiveWorkflowStrategyCommand();
		w.execute(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCommands() {
		new NaiveWorkflowStrategyCommand(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullCommand() {
		new NaiveWorkflowStrategyCommand().addCommand(null);
	}

}
