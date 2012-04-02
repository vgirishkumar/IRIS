package com.temenos.interaction.core.state;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceDeleteCommand;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePostCommand;
import com.temenos.interaction.core.command.ResourcePutCommand;

public class TestAbstractHTTPResourceInteractionModel {

	/* We decode the query parameters to workaround an issue in Wink */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testDecodeQueryParameters() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		when(rgc.get(anyString(), any(MultivaluedMap.class))).thenReturn(new RESTResponse(Response.Status.FORBIDDEN, null));
		cc.setGetCommand(resourcePath, rgc);
		
		UriInfo uriInfo = mock(UriInfo.class);
		MultivaluedMap<String, String> queryMap = new MultivaluedMapImpl();
		queryMap.add("$filter", "this+that");
		when(uriInfo.getQueryParameters()).thenReturn(queryMap);
		
		r.get(null, "id", uriInfo);
		verify(rgc).get(eq("id"), (MultivaluedMap<String, String>) argThat(new MultimapArgumentMatcher()));
	}

	@SuppressWarnings("rawtypes")
	class MultimapArgumentMatcher extends ArgumentMatcher {
		@SuppressWarnings("unchecked")
		public boolean matches(Object o) {
			if (o instanceof MultivaluedMap) {
				MultivaluedMap<String, String> mvmap = (MultivaluedMap<String, String>) o;
				if (!mvmap.getFirst("$filter").equals("this that")) {
					return false;
				}
				return true;
			}
            return false;
        }
    }

	@Test
	public void testFQResourcePath() {
		final AbstractHTTPResourceInteractionModel parent = mock(AbstractHTTPResourceInteractionModel.class);
		when(parent.getResourcePath()).thenReturn("/root");
		AbstractHTTPResourceInteractionModel child = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", "/child") {
			public ResourceInteractionModel getParent() {
				return parent;
			}
		};

		assertEquals("/child", child.getResourcePath());
		assertEquals("/root/child", child.getFQResourcePath());
	}

	/* Test a status returned in the ResourceGetCommand will be returned all the way to the client */
	@SuppressWarnings("unchecked")
	@Test
	public void testGETCommandStatus() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		when(rgc.get(anyString(), isNull(MultivaluedMap.class))).thenReturn(new RESTResponse(Response.Status.FORBIDDEN, null));
		cc.setGetCommand(resourcePath, rgc);
		Response response = r.get(null, "123", null);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}

	/* When the ResourceGetCommand does not return a Status (null) we expect to get an assertion error */
	@Test(expected = AssertionError.class)
	public void testGETStatusNull() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		cc.setGetCommand(resourcePath, mock(ResourceGetCommand.class));
		r.get(null, "123", null);
	}

	/* No real need to test for this exception, responsibility of CommandConntroller */
	@Test(expected = WebApplicationException.class)
	public void testGETNoCommand() {
		String resourcePath = "/test";
		HTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		r.get(null, "123", null);
	}

	/* ResourceGetCommand returns a RESTResponse with Status OK, but getResource will return null */
	@SuppressWarnings("unchecked")
	@Test(expected = AssertionError.class)
	public void testGET200NoResource() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceGetCommand rgc = mock(ResourceGetCommand.class);
		when(rgc.get(anyString(), isNull(MultivaluedMap.class))).thenReturn(new RESTResponse(Response.Status.OK, null));
		cc.setGetCommand(resourcePath, rgc);
		r.get(null, "123", null);
	}

	/* Test a status returned in the ResourceDeleteCommand will be returned all the way to the client */
	@Test
	public void testDELETECommandStatus() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceDeleteCommand rpc = mock(ResourceDeleteCommand.class);
		when(rpc.delete(anyString())).thenReturn(Response.Status.ACCEPTED);
		when(rpc.getMethod()).thenReturn("DELETE");
		cc.addStateTransitionCommand(resourcePath, rpc);
		Response response = r.delete(mock(HttpHeaders.class), "123");
		assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
	}

	/* When the ResourceDeleteCommand does not return a Status (null) we expect to get an assertion error */
	@Test(expected = AssertionError.class)
	public void testDELETEStatusNull() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourceDeleteCommand rdc = mock(ResourceDeleteCommand.class);
		when(rdc.getMethod()).thenReturn("DELETE");
		cc.addStateTransitionCommand(resourcePath, rdc);
		r.delete(null, "123");
	}

	/* No real need to test for this exception, responsibility of CommandConntroller */
	@Test
	public void testDELETENoCommand() {
		String resourcePath = "/test";
		HTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		Response resp = r.delete(null, "123");
		assertTrue(resp.getStatus() == 405);
	}

	/* Test a status of method not allows returns a response with the valid options */
	@Test
	public void testDELETECommand405Options() {
		String resourcePath = "/test";
		
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		
		Response response = r.delete(mock(HttpHeaders.class), "123");
		assertEquals(405, response.getStatus());
		
        // as per the http spec, 405 MUST include an Allow header
		List<Object> allowHeader = response.getMetadata().get("Allow");
		assertNotNull(allowHeader);
        assertEquals(1, allowHeader.size());
        assertEquals("GET, OPTIONS, HEAD", allowHeader.get(0));
	}

	/* Test a status returned in the ResourcePostCommand will be returned all the way to the client */
	@Test
	public void testPOSTCommandStatus() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
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
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rpc = mock(ResourcePostCommand.class);
		when(rpc.getMethod()).thenReturn("POST");
		cc.addStateTransitionCommand(resourcePath, rpc);
		r.post(null, "123", mock(EntityResource.class));
	}

	/* No real need to test for this exception, responsibility of CommandConntroller */
	@Test
	public void testPOSTNoCommand() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		Response resp = r.post(null, "123", null);
		assertTrue(resp.getStatus() == 405);
	}

	/* ResourcePostCommand returns a RESTResponse with Status OK, but getResource will return null */
	@Test(expected = AssertionError.class)
	public void testPOST200NoResource() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rpc = mock(ResourcePostCommand.class);
		when(rpc.post(anyString(), any(EntityResource.class))).thenReturn(new RESTResponse(Response.Status.OK, null));
		when(rpc.getMethod()).thenReturn("POST");
		cc.addStateTransitionCommand(resourcePath, rpc);
		r.post(null, "123", mock(EntityResource.class));
	}

	/* ResourcePostCommand returns a RESTResponse with Status "Family" (202), getResource will return null, and options */
	@Test
	public void testPOST204Options() {
		String resourcePath = "/test";

		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		ResourcePostCommand rpc = mock(ResourcePostCommand.class);
		when(rpc.post(anyString(), any(EntityResource.class))).thenReturn(new RESTResponse(Response.Status.ACCEPTED, mock(EntityResource.class)));
		when(rpc.getMethod()).thenReturn("POST");
		cc.addStateTransitionCommand(resourcePath, rpc);

		Response response = r.post(null, "123", mock(EntityResource.class));
		assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

		// we don't need to, but it's nice to return the options
		List<Object> allowHeader = response.getMetadata().get("Allow");
		assertNotNull(allowHeader);
        assertEquals(1, allowHeader.size());
        assertEquals("POST, GET, OPTIONS, HEAD", allowHeader.get(0));
	}

	/* Test a status returned in the ResourcePostCommand will be returned all the way to the client */
	@Test
	public void testPOSTCommand405Options() {
		String resourcePath = "/test";
		
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		
		Response response = r.post(mock(HttpHeaders.class), "123", mock(EntityResource.class));
		assertEquals(405, response.getStatus());
		
        // as per the http spec, 405 MUST include an Allow header
		List<Object> allowHeader = response.getMetadata().get("Allow");
		assertNotNull(allowHeader);
        assertEquals(1, allowHeader.size());
        assertEquals("GET, OPTIONS, HEAD", allowHeader.get(0));
	}

	/* ResourcePutCommand returns a RESTResponse with Status "Family" (202), getResource will return null, and get latest version of resource */
	@Test
	public void testPUT204Options() {
		String resourcePath = "/test";

		CommandController cc = new CommandController();
		ResourcePutCommand rpc = mock(ResourcePutCommand.class);
		when(rpc.put(anyString(), any(EntityResource.class))).thenReturn(Response.Status.ACCEPTED);
		when(rpc.getMethod()).thenReturn("PUT");
		cc.addStateTransitionCommand(resourcePath, rpc);

		AbstractHTTPResourceInteractionModel r = mock(AbstractHTTPResourceInteractionModel.class);
		when(r.getCommandController()).thenReturn(cc);
		when(r.getResourcePath()).thenReturn(resourcePath);
		when(r.getFQResourcePath()).thenReturn(resourcePath);
		when(r.put(any(HttpHeaders.class), anyString(), any(EntityResource.class))).thenCallRealMethod();
		r.put(null, "123", mock(EntityResource.class));
		verify(r).get(any(HttpHeaders.class), anyString(), any(UriInfo.class));
	}

	/* Test a status of method not allows returns a response with the valid options */
	@Test
	public void testPUTCommand405Options() {
		String resourcePath = "/test";
		
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		
		Response response = r.put(mock(HttpHeaders.class), "123", mock(EntityResource.class));
		assertEquals(405, response.getStatus());
		
        // as per the http spec, 405 MUST include an Allow header
		List<Object> allowHeader = response.getMetadata().get("Allow");
		assertNotNull(allowHeader);
        assertEquals(1, allowHeader.size());
        assertEquals("GET, OPTIONS, HEAD", allowHeader.get(0));
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
