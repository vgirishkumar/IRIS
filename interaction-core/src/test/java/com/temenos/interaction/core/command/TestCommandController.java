package com.temenos.interaction.core.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestCommandController {

	@Test
	public void testAddGetCommand() {
		CommandController cc = new CommandController("/test-resource");
		cc.setGetCommand(null);
	}

	@Test
	public void testGetCommandNull() {
		CommandController cc = new CommandController("/test-resource");
		boolean exceptionThrown = false;
		try {
			cc.fetchGetCommand();
		} catch (WebApplicationException wae) {
			if (wae.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				exceptionThrown = true;
			}
		}
		assertTrue(exceptionThrown);
	}

	@Test
	public void testGetCommandNotNullNotRegistered() {
		CommandController cc = new CommandController("/test-resource");
		boolean exceptionThrown = false;
		try {
			cc.fetchGetCommand();
		} catch (WebApplicationException wae) {
			if (wae.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				exceptionThrown = true;
			}
		}
		assertTrue(exceptionThrown);
	}

	@Test
	public void testGetCommandRegistered() {
		CommandController cc = new CommandController("/test-resource");
		ResourceGetCommand getCommand = mock(ResourceGetCommand.class);
		cc.setGetCommand(getCommand);
		assertEquals(getCommand, cc.fetchGetCommand());
	}

	@Test(expected = AssertionError.class)
	public void testAddSTCommand() {
		CommandController cc = new CommandController("/test-resource");
		cc.addStateTransitionCommand(null);
	}

	@Test
	public void testSTCommandNull() {
		CommandController cc = new CommandController("/test-resource");
		boolean exceptionThrown = false;
		try {
			cc.fetchStateTransitionCommand(null, null);
		} catch (WebApplicationException wae) {
			if (wae.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				exceptionThrown = true;
			}
		}
		assertTrue(exceptionThrown);
	}

	@Test
	public void testSTCommandNotNullNotRegistered() {
		CommandController cc = new CommandController("/test-resource");
		boolean exceptionThrown = false;
		try {
			cc.fetchStateTransitionCommand("DO", "test");
		} catch (WebApplicationException wae) {
			if (wae.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				exceptionThrown = true;
			}
		}
		assertTrue(exceptionThrown);
	}

	@Test
	public void testSTCommandRegistered() {
		CommandController cc = new CommandController("/test-resource");
		String uriPath = "/test-resource/do";
		ResourceStateTransitionCommand command = mock(ResourcePutCommand.class);
		when(command.getMethod()).thenReturn("DO");
		when(command.getPath()).thenReturn(uriPath);
		cc.addStateTransitionCommand(command);
		assertEquals(command, cc.fetchStateTransitionCommand("DO", uriPath));
	}

}
