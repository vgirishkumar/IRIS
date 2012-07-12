package com.temenos.interaction.core.rim;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.web.RequestContext;
import com.temenos.interaction.core.command.HttpStatusTypes;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.NewCommandController;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HTTPHypermediaRIM.class})
public class TestResponseHTTPHypermediaRIM {
	
	@Before
	public void setup() {
		// initialise the thread local request context with requestUri and baseUri
		UriBuilder baseUri = UriBuilder.fromUri("/baseuri");
		String requestUri = "/baseuri/";
        RequestContext ctx = new RequestContext(baseUri, requestUri, null);
        RequestContext.setRequestContext(ctx);
	}

	/*
	 * This test checks that we receive a 404 'Not Found' if a GET command is not registered.
	 * Every resource must have a GET command, so no command means no resource (404)
	 */
	@Test
	public void testGETCommandNotRegistered() {
		// our empty command controller
		NewCommandController mockCommandController = new NewCommandController();

		ResourceState initialState = new ResourceState("entity", "state", "/path");
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState));
		Response response = rim.get(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	/*
	 * This test checks that we receive a 405 'Method Not Allowed' if a command is 
	 * not registered for the given method PUT/POST/DELETE.
	 */
	@Test
	public void testPUTCommandNotRegisteredNotAllowedHeader() {
		// our empty command controller
		NewCommandController mockCommandController = new NewCommandController();

		ResourceState initialState = new ResourceState("entity", "state", "/path");
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState));
		Response response = rim.put(mock(HttpHeaders.class), "id", mockEmptyUriInfo(), mock(EntityResource.class));
		assertEquals(HttpStatusTypes.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
        // as per the http spec, 405 MUST include an Allow header
		List<Object> allowHeader = response.getMetadata().get("Allow");
		assertNotNull(allowHeader);
        assertEquals(1, allowHeader.size());
        assertEquals("GET, OPTIONS, HEAD", allowHeader.get(0));
	}

	/*
	 * This test checks that we receive a 405 'Method Not Allowed' if a command is 
	 * not registered for the given method PUT/POST/DELETE.
	 */
	@Test
	public void testPOSTCommandNotRegisteredNotAllowedHeader() {
		// our empty command controller
		NewCommandController mockCommandController = new NewCommandController();

		ResourceState initialState = new ResourceState("entity", "state", "/path");
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState));
		Response response = rim.post(mock(HttpHeaders.class), "id", mockEmptyUriInfo(), mock(EntityResource.class));
		assertEquals(HttpStatusTypes.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
        // as per the http spec, 405 MUST include an Allow header
		List<Object> allowHeader = response.getMetadata().get("Allow");
		assertNotNull(allowHeader);
        assertEquals(1, allowHeader.size());
        assertEquals("GET, OPTIONS, HEAD", allowHeader.get(0));
	}

	/*
	 * This test checks that we receive a 405 'Method Not Allowed' if a command is 
	 * not registered for the given method PUT/POST/DELETE.
	 */
	@Test
	public void testDELETECommandNotRegisteredNotAllowedHeader() {
		// our empty command controller
		NewCommandController mockCommandController = new NewCommandController();

		ResourceState initialState = new ResourceState("entity", "state", "/path");
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState));
		Response response = rim.delete(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		assertEquals(HttpStatusTypes.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
        // as per the http spec, 405 MUST include an Allow header
		List<Object> allowHeader = response.getMetadata().get("Allow");
		assertNotNull(allowHeader);
        assertEquals(1, allowHeader.size());
        assertEquals("GET, OPTIONS, HEAD", allowHeader.get(0));
	}

	/*
	 * This test is for a PUT request that returns HttpStatus 204 "No Content"
	 * A PUT command that does not return a new resource will inform the client
	 * that there is no new information to display, continue with the current
	 * view of this resource.
	 */
	@Test
	public void testBuildResponseWithNoContent() throws Exception {
		ResourceState initialState = new ResourceState("entity", "state", "/path");
		/*
		 * construct an InteractionCommand that simply mocks the result of 
		 * storing a resource, with no updated resource for the user agent
		 * to re-display
		 */
		InteractionCommand mockCommand = new InteractionCommand() {
			@Override
			public Result execute(InteractionContext ctx) {
				// this is how a command indicates No Content
				ctx.setResource(null);
				return Result.SUCCESS;
			}
			@Override
			public String getMethod() {
				return null;
			}
		};
		
		// create mock command controller
		NewCommandController mockCommandController = mock(NewCommandController.class);
		when(mockCommandController.isValidCommand("PUT", "/path")).thenReturn(true);
		when(mockCommandController.fetchCommand("PUT", "/path")).thenReturn(mockCommand);

		// RIM with command controller that issues our mock InteractionCommand
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState));
		Response response = rim.put(mock(HttpHeaders.class), "id", mockEmptyUriInfo(), mock(EntityResource.class));
		
		// null resource for no content
		RESTResource resource = (RESTResource) response.getEntity();
		assertNull(resource);
		// 204 http status for no content
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
	}

	/*
	 * This test is for a DELETE request that returns HttpStatus 405 "Content Reset"
	 * A successful DELETE command does not return a new resource and should inform
	 * the user agent to refresh the current view.
	 */
	@SuppressWarnings({ "unchecked", })
	@Test
	public void testBuildResponseWith205ContentReset() throws Exception {
		/*
		 * construct an InteractionContext that simply mocks the result of 
		 * deleting a resource, with no updated resource for the user agent
		 * to re-display
		 */
		ResourceState initialState = new ResourceState("entity", "state", "/path");
		InteractionContext testContext = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), initialState);
		testContext.setResource(null);
		// mock 'new InteractionContext()' in call to delete
		whenNew(InteractionContext.class).withArguments(any(MultivaluedMap.class), any(MultivaluedMap.class), any(ResourceState.class)).thenReturn(testContext);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState));
		Response response = rim.delete(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		
		// null resource
		RESTResource resource = (RESTResource) response.getEntity();
		assertNull(resource);
		// 205 http status for Reset Content
		assertEquals(HttpStatusTypes.RESET_CONTENT.getStatusCode(), response.getStatus());
	}

	/*
	 * This test is for a DELETE request that supplies a custom link relation
	 * via the Link header.  See (see rfc5988)
	 * When a user agent follows a link it is able to supply the link relations
	 * given to it by the server for that link.  This provides the server with
	 * some information about which link the client followed, and therefore
	 * what state/links to show them next.
	 */
	@SuppressWarnings({ "unchecked", })
	@Test
	public void testBuildResponseWith303SeeOtherSameEntity() throws Exception {
		/*
		 * construct an InteractionContext that simply mocks the result of 
		 * deleting a resource, with no updated resource for the user agent
		 * to re-display
		 */
		ResourceState initialState = new ResourceState("home", "initial", "/machines");
		ResourceState existsState = new ResourceState("toaster", "exists", "/machines/toaster");
		ResourceState cookingState = new ResourceState("toaster", "cooking", "/machines/toaster/cooking");
		
		// view the toaster if it exists
		initialState.addTransition("GET", existsState);
		// view the resource if the toaster is cooking (could be time remaining)
		existsState.addTransition("GET", cookingState);
		// stop the toast cooking
		cookingState.addTransition("DELETE", existsState);
		InteractionContext testContext = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), initialState);
		testContext.setResource(null);
		// mock 'new InteractionContext()' in call to delete
		whenNew(InteractionContext.class).withArguments(any(MultivaluedMap.class), any(MultivaluedMap.class), any(ResourceState.class)).thenReturn(testContext);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState));
		Collection<ResourceInteractionModel> children = rim.getChildren();
		// find the draft resource interaction model
		HTTPHypermediaRIM draftRIM = null;
		for (ResourceInteractionModel r : children) {
			if (r.getCurrentState().getId().equals("toaster.cooking")) {
				draftRIM = (HTTPHypermediaRIM) children.iterator().next();
			}
		}
		// mock the Link header
		HttpHeaders mockHeaders = mock(HttpHeaders.class);
		List<String> links = new ArrayList<String>();
		links.add("</path>; rel=\"toaster.cooking>toaster.exists\"");
		when(mockHeaders.getRequestHeader("Link")).thenReturn(links);
		Response response = draftRIM.delete(mockHeaders, "id", mockEmptyUriInfo());
		
		// null resource
		RESTResource resource = (RESTResource) response.getEntity();
		assertNull(resource);
		// 303 "See Other" instructs user agent to fetch another resource as specified by the 'Location' header
		assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
		List<Object> locationHeader = response.getMetadata().get("Location");
		assertNotNull(locationHeader);
        assertEquals(1, locationHeader.size());
        assertEquals("/baseuri/machines/toaster", locationHeader.get(0));
	}

	@SuppressWarnings({ "unchecked", })
	@Test
	public void testBuildResponseWithLinks() throws Exception {
		// construct an InteractionContext that simply mocks the result of loading a resource
		ResourceState initialState = new ResourceState("entity", "state", "/path");
		InteractionContext testContext = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), initialState);
		testContext.setResource(new EntityResource<Object>(null));
		// mock 'new InteractionContext()' in call to get
		whenNew(InteractionContext.class).withArguments(any(MultivaluedMap.class), any(MultivaluedMap.class), any(ResourceState.class)).thenReturn(testContext);
		
		List<Link> links = new ArrayList<Link>();
		links.add(new Link("id", "self", "href", null, null, "GET", "label", "description", null));
		ResourceStateMachine hypermediaEngine = mock(ResourceStateMachine.class);
		when(hypermediaEngine.getInitial()).thenReturn(initialState);
		when(hypermediaEngine.getLinks(any(MultivaluedMap.class), any(RESTResource.class), any(ResourceState.class), any(List.class)))
			.thenReturn(links);
		
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), hypermediaEngine);
		Response response = rim.get(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		
		RESTResource resourceWithLinks = (RESTResource) ((GenericEntity<?>)response.getEntity()).getEntity();
		assertNotNull(resourceWithLinks.getLinks());
		assertFalse(resourceWithLinks.getLinks().isEmpty());
		assertEquals(1, resourceWithLinks.getLinks().size());
		Link link = (Link) resourceWithLinks.getLinks().toArray()[0];
		assertEquals("self", link.getRel());
	}

	@SuppressWarnings({ "unchecked" })
	private UriInfo mockEmptyUriInfo() {
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPathParameters(true)).thenReturn(mock(MultivaluedMap.class));
		when(uriInfo.getQueryParameters(true)).thenReturn(mock(MultivaluedMap.class));
		return uriInfo;
	}
	
	private NewCommandController mockNoopCommandController() {
		// make sure command execution does nothing
		InteractionCommand testCommand = mock(InteractionCommand.class);
		when(testCommand.execute(any(InteractionContext.class))).thenReturn(Result.SUCCESS);
		NewCommandController commandController = mock(NewCommandController.class);
		when(commandController.isValidCommand(anyString(), any(String.class))).thenReturn(true);
		when(commandController.fetchCommand(anyString(), any(String.class))).thenReturn(testCommand);
		return commandController;
	}
}
