package com.temenos.interaction.core.state;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceDeleteCommand;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class TestCRUDResourceInteractionModel {

	/* Test a status returned in the ResourceGetCommand will be returned all the way to the client */
	@Test
	public void testGETCommandStatus() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		when(rgc.get(anyString())).thenReturn(new RESTResponse(Response.Status.FORBIDDEN, null, null));
		cc.setGetCommand(rgc);
		Response response = r.get(null, "123");
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}

	/* When the ResourceGetCommand does not return a Status (null) we expect to get an assertion error */
	@Test(expected = AssertionError.class)
	public void testGETStatusNull() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		cc.setGetCommand(mock(ResourceGetCommand.class));
		r.get(null, "123");
	}

	/* No real need to test for this exception, responsibility of CommandConntroller */
	@Test(expected = WebApplicationException.class)
	public void testGETNoCommand() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		r.get(null, "123");
	}

	/* ResourceGetCommand returns a RESTResponse with Status OK, but getResource will return null */
	@Test(expected = AssertionError.class)
	public void testGET200NoResource() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		when(rgc.get(anyString())).thenReturn(new RESTResponse(Response.Status.OK, null, null));
		cc.setGetCommand(rgc);
		r.get(null, "123");
	}

	/* Test a status returned in the ResourceDeleteCommand will be returned all the way to the client */
	@Test
	public void testDELETECommandStatus() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceDeleteCommand rpc = mock(ResourceDeleteCommand.class);
		when(rpc.delete(anyString())).thenReturn(Response.Status.ACCEPTED);
		when(rpc.getMethod()).thenReturn("DELETE");
		when(rpc.getPath()).thenReturn(resourcePath);
		cc.addStateTransitionCommand(rpc);
		Response response = r.delete(mock(HttpHeaders.class), "123");
		assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
	}

	/* When the ResourceDeleteCommand does not return a Status (null) we expect to get an assertion error */
	@Test(expected = AssertionError.class)
	public void testDELETEStatusNull() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceDeleteCommand rdc = mock(ResourceDeleteCommand.class);
		when(rdc.getMethod()).thenReturn("DELETE");
		when(rdc.getPath()).thenReturn(resourcePath);
		cc.addStateTransitionCommand(rdc);
		r.delete(null, "123");
	}

	/* No real need to test for this exception, responsibility of CommandConntroller */
	@Test(expected = WebApplicationException.class)
	public void testDELETENoCommand() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		r.delete(null, "123");
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
