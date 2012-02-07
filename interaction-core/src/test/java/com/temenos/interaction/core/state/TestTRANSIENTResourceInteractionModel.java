package com.temenos.interaction.core.state;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourcePostCommand;


public class TestTRANSIENTResourceInteractionModel {

	/* Test a status returned in the ResourcePostCommand will be returned all the way to the client */
	@Test
	public void testPOSTCommandStatus() {
		String resourcePath = "/test";
		TRANSIENTResourceInteractionModel r = new TRANSIENTResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rpc = mock(ResourcePostCommand.class);
		when(rpc.post(anyString(), any(EntityResource.class))).thenReturn(new RESTResponse(Response.Status.ACCEPTED, mock(RESTResource.class)));
		when(rpc.getMethod()).thenReturn("POST");
		cc.addStateTransitionCommand(resourcePath, rpc);
		Response response = r.post(mock(HttpHeaders.class), "123", mock(EntityResource.class));
		assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
	}

	/* When the ResourcePostCommand does not return a Status (null) we expect to get an assertion error */
	@Test(expected = AssertionError.class)
	public void testPOSTStatusNull() {
		String resourcePath = "/test";
		TRANSIENTResourceInteractionModel r = new TRANSIENTResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rpc = mock(ResourcePostCommand.class);
		when(rpc.getMethod()).thenReturn("POST");
		cc.addStateTransitionCommand(resourcePath, rpc);
		r.post(null, "123", mock(EntityResource.class));
	}

	/* No real need to test for this exception, responsibility of CommandConntroller */
	@Test(expected = WebApplicationException.class)
	public void testPOSTNoCommand() {
		String resourcePath = "/test";
		TRANSIENTResourceInteractionModel r = new TRANSIENTResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		r.post(null, "123", null);
	}

	/* ResourcePostCommand returns a RESTResponse with Status OK, but getResource will return null */
	@Test(expected = AssertionError.class)
	public void testPOST200NoResource() {
		String resourcePath = "/test";
		TRANSIENTResourceInteractionModel r = new TRANSIENTResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rpc = mock(ResourcePostCommand.class);
		when(rpc.post(anyString(), any(EntityResource.class))).thenReturn(new RESTResponse(Response.Status.OK, null));
		when(rpc.getMethod()).thenReturn("POST");
		cc.addStateTransitionCommand(resourcePath, rpc);
		r.post(null, "123", mock(EntityResource.class));
	}

	
}
