package com.temenos.interaction.core.state;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class TestGPPDStateTransition {

	@Test
	public void testGetStatus() {
		String resourcePath = "/test";
		GPPDResource<RESTResource> r = new GPPDResource<RESTResource>(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		when(rgc.get(anyString())).thenReturn(Response.Status.FORBIDDEN);
		cc.addGetCommand(resourcePath, rgc);
		Response response = r.get(null, "123");
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}

	@Test(expected = AssertionError.class)
	public void testGetStatusNull() {
		String resourcePath = "/test";
		GPPDResource<RESTResource> r = new GPPDResource<RESTResource>(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		cc.addGetCommand(resourcePath, mock(ResourceGetCommand.class));
		r.get(null, "123");
	}

	@Test
	public void testGet500() {
		String resourcePath = "/test";
		GPPDResource<RESTResource> r = new GPPDResource<RESTResource>(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		when(rgc.get(anyString())).thenReturn(Response.Status.INTERNAL_SERVER_ERROR);
		cc.addGetCommand(resourcePath, rgc);
		r.get(null, "123");
	}

	@Test(expected = AssertionError.class)
	public void testGet200NoResource() {
		String resourcePath = "/test";
		GPPDResource<RESTResource> r = new GPPDResource<RESTResource>(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		// return ok, but getResource will return null
		when(rgc.get(anyString())).thenReturn(Response.Status.OK);
		cc.addGetCommand(resourcePath, rgc);
		r.get(null, "123");
	}

	/* TODO
	@Test
	public void testGetAlternateMediaTypes() {
		fail("Not yet implemented");
	}

	@Test
	public void testPutText() {
		fail("Not yet implemented");
	}

	@Test
	public void testPostJSON() {
		fail("Not yet implemented");
	}

	@Test
	public void testPutJSON() {
		fail("Not yet implemented");
	}

	@Test
	public void testPutHAL() {
		fail("Not yet implemented");
	}

	@Test
	public void testOptions() {
		fail("Not yet implemented");
	}
*/
}
