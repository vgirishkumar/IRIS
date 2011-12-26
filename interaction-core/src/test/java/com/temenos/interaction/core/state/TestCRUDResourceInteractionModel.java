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
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePostCommand;

public class TestCRUDResourceInteractionModel {

	@Test
	public void testGetStatus() {
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

	@Test(expected = AssertionError.class)
	public void testGetStatusNull() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		cc.addGetCommand(resourcePath, mock(ResourceGetCommand.class));
		r.get(null, "123");
	}

	@Test
	public void testGet500() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		when(rgc.get(anyString())).thenReturn(new RESTResponse(Response.Status.INTERNAL_SERVER_ERROR, null, null));
		cc.addGetCommand(resourcePath, rgc);
		r.get(null, "123");
	}

	@Test(expected = AssertionError.class)
	public void testGet200NoResource() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		// return ok, but getResource will return null
		when(rgc.get(anyString())).thenReturn(new RESTResponse(Response.Status.OK, null, null));
		cc.addGetCommand(resourcePath, rgc);
		r.get(null, "123");
	}

	/* No real need to test for this exception, responsibility of CommandConntroller */
	@Test(expected = WebApplicationException.class)
	public void testPOSTNoCommand() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		r.post(null, "123", null);
	}

	@Test
	public void testPOSTStatus() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rpc = mock(ResourcePostCommand.class);
		// return ok, but getResource will return null
		when(rpc.post(anyString(), any(EntityResource.class))).thenReturn(new RESTResponse(Response.Status.ACCEPTED, mock(RESTResource.class), null));
		cc.addStateTransitionCommand("POST", resourcePath, rpc);
		Response response = r.post(mock(HttpHeaders.class), "123", mock(EntityResource.class));
		assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
	}

	@Test(expected = AssertionError.class)
	public void testPOSTStatusNull() {
		String resourcePath = "/test";
		CRUDResourceInteractionModel r = new CRUDResourceInteractionModel(resourcePath) {
		};
		CommandController cc = r.getCommandController();
		cc.addStateTransitionCommand("POST", resourcePath, mock(ResourcePostCommand.class));
		r.post(null, "123", mock(EntityResource.class));
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
