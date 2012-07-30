package com.temenos.interaction.core.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class TestNewCommandController {

	@Test
	public void testDefaultConstructorNotNullNotRegistered() {
		NewCommandController cc = new NewCommandController();
		InteractionCommand command = cc.fetchCommand("GET", "/test-resource");
		assertNull(command);
	}

	@Test
	public void testFetchCommandNoCommandsNotFound() {
		NewCommandController cc = new NewCommandController("/test-resource", null);
		InteractionCommand command = cc.fetchCommand("GET", "/test-resource");
		assertNull(command);
	}

	@Test
	public void testFetchCommandNoCommandsNotAllowed() {
		NewCommandController cc = new NewCommandController("/test-resource", null);
		InteractionCommand command = cc.fetchCommand("DO", "/test-resource");
		assertNull(command);
	}

	@Test(expected = AssertionError.class)
	public void testAddNullCommand() {
		NewCommandController cc = new NewCommandController();
		cc.addCommand(null, null);
	}

	@Test
	public void testCommandRegistered() {
		NewCommandController cc = new NewCommandController();
		String uriPath = "/test-resource/do";
		InteractionCommand command = mock(InteractionCommand.class);
		when(command.getMethod()).thenReturn("DO");
		cc.addCommand("/test-resource/do", command);
		assertEquals(command, cc.fetchCommand("DO", uriPath));
	}

	@Test
	public void testIsValidCommandCommandRegistered() {
		NewCommandController cc = new NewCommandController();
		String uriPath = "/test-resource/do";
		InteractionCommand command = mock(InteractionCommand.class);
		when(command.getMethod()).thenReturn("DO");
		cc.addCommand("/test-resource/do", command);
		assertTrue(cc.isValidCommand("DO", uriPath));
		assertFalse(cc.isValidCommand("NOTHING", uriPath));
	}

}
