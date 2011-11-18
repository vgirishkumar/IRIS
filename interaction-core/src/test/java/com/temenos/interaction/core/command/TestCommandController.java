package com.temenos.interaction.core.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestCommandController {

	@Test
	public void testAddGetCommand() {
		CommandController cc = new CommandController();
		cc.addGetCommand(null, null);
	}

	@Test
	public void testGetCommandNull() {
		CommandController cc = new CommandController();
		boolean exceptionThrown = false;
		try {
			cc.fetchGetCommand(null);
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
			cc.fetchGetCommand("test");
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
		String uriPath = "test";
		ResourceGetCommand getCommand = mock(ResourceGetCommand.class);
		cc.addGetCommand(uriPath, getCommand);
		assertEquals(getCommand, cc.fetchGetCommand(uriPath));
	}

	@Test
	public void testAddSTCommand() {
		CommandController cc = new CommandController();
		cc.addStateTransitionCommand(null, null, null);
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
		String uriPath = "test";
		ResourceStateTransitionCommand command = mock(ResourcePutCommand.class);
		cc.addStateTransitionCommand("DO", uriPath, command);
		assertEquals(command, cc.fetchStateTransitionCommand("DO", uriPath));
	}

}
