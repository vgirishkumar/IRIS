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
		CommandController cc = new CommandController();
		cc.setGetCommand("/test-resource", null);
	}

	@Test
	public void testGetCommandNull() {
		CommandController cc = new CommandController("/test-resource", null, null);
		boolean exceptionThrown = false;
		try {
			cc.fetchGetCommand("/test-resource");
		} catch (WebApplicationException wae) {
			if (wae.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				exceptionThrown = true;
			}
		}
		assertTrue(exceptionThrown);
	}

	@Test
	public void testGetCommandNotNullNotRegistered() {
		CommandController cc = new CommandController();
		boolean exceptionThrown = false;
		try {
			cc.fetchGetCommand("/test-resource");
		} catch (WebApplicationException wae) {
			if (wae.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				exceptionThrown = true;
			}
		}
		assertTrue(exceptionThrown);
	}

	@Test
	public void testGetCommandRegistered() {
		CommandController cc = new CommandController();
		ResourceGetCommand getCommand = mock(ResourceGetCommand.class);
		cc.setGetCommand("/test-resource", getCommand);
		assertEquals(getCommand, cc.fetchGetCommand("/test-resource"));
	}

	@Test(expected = AssertionError.class)
	public void testAddSTCommand() {
		CommandController cc = new CommandController();
		cc.addStateTransitionCommand(null, null);
	}

	@Test
	public void testSTCommandNull() {
		CommandController cc = new CommandController();
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
		CommandController cc = new CommandController();
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
		CommandController cc = new CommandController();
		String uriPath = "/test-resource/do";
		ResourceStateTransitionCommand command = mock(ResourcePutCommand.class);
		when(command.getMethod()).thenReturn("DO");
		cc.addStateTransitionCommand("/test-resource/do", command);
		assertEquals(command, cc.fetchStateTransitionCommand("DO", uriPath));
	}

}
