package com.temenos.interaction.core.state;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceDeleteCommand;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePostCommand;

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
		cc.addGetCommand(resourcePath, rgc);
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
		cc.addGetCommand(resourcePath, mock(ResourceGetCommand.class));
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
		cc.addGetCommand(resourcePath, rgc);
		r.get(null, "123");
	}

	/* Test a status returned in the ResourcePostCommand will be returned all the way to the client */
	@Test
	public void testPOSTCommandStatus() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rpc = mock(ResourcePostCommand.class);
		when(rpc.post(anyString(), any(EntityResource.class))).thenReturn(new RESTResponse(Response.Status.ACCEPTED, mock(RESTResource.class), null));
		cc.addStateTransitionCommand("POST", resourcePath, rpc);
		Response response = r.post(mock(HttpHeaders.class), "123", mock(EntityResource.class));
		assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
	}

	/* When the ResourcePostCommand does not return a Status (null) we expect to get an assertion error */
	@Test(expected = AssertionError.class)
	public void testPOSTStatusNull() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		cc.addStateTransitionCommand("POST", resourcePath, mock(ResourcePostCommand.class));
		r.post(null, "123", mock(EntityResource.class));
	}

	/* No real need to test for this exception, responsibility of CommandConntroller */
	@Test(expected = WebApplicationException.class)
	public void testPOSTNoCommand() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		r.post(null, "123", null);
	}

	/* ResourcePostCommand returns a RESTResponse with Status OK, but getResource will return null */
	@Test(expected = AssertionError.class)
	public void testPOST200NoResource() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rgc = mock(ResourcePostCommand.class);
		when(rgc.post(anyString(), any(EntityResource.class))).thenReturn(new RESTResponse(Response.Status.OK, null, null));
		cc.addStateTransitionCommand("POST", resourcePath, rgc);
		r.post(null, "123", mock(EntityResource.class));
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
		cc.addStateTransitionCommand("DELETE", resourcePath, rpc);
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
		cc.addStateTransitionCommand("DELETE", resourcePath, mock(ResourceDeleteCommand.class));
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
