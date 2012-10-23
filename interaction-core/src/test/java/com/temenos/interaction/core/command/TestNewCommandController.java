package com.temenos.interaction.core.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

import org.junit.Test;

public class TestNewCommandController {

	@Test
	public void testDefaultConstructorNotNullNotRegistered() {
		NewCommandController cc = new NewCommandController();
		InteractionCommand command = cc.fetchCommand("dostuff");
		assertNull(command);
	}

	@Test(expected = AssertionError.class)
	public void testFetchCommandNoCommandsSet() {
		new NewCommandController(null);
	}

	@Test
	public void testFetchCommandNoCommandsSetNotFound() {
		NewCommandController cc = new NewCommandController(new HashMap<String, InteractionCommand>());
		InteractionCommand command = cc.fetchCommand("dostuff");
		assertNull(command);
	}

	@Test(expected = AssertionError.class)
	public void testAddNullCommand() {
		NewCommandController cc = new NewCommandController();
		cc.addCommand("commandName", null);
	}

	@Test(expected = AssertionError.class)
	public void testAddNullName() {
		NewCommandController cc = new NewCommandController();
		cc.addCommand(null, mock(InteractionCommand.class));
	}

	@Test
	public void testCommandRegistered() {
		NewCommandController cc = new NewCommandController();
		InteractionCommand command = mock(InteractionCommand.class);
		cc.addCommand("DO", command);
		assertEquals(command, cc.fetchCommand("DO"));
	}

	@Test
	public void testIsValidCommandCommandRegistered() {
		NewCommandController cc = new NewCommandController();
		InteractionCommand command = mock(InteractionCommand.class);
		cc.addCommand("DO", command);
		assertTrue(cc.isValidCommand("DO"));
		assertFalse(cc.isValidCommand("NOTHING"));
	}

}
