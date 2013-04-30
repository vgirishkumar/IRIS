package com.temenos.interaction.core.rim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.core.command.HttpStatusTypes;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.BeanTransformer;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.web.RequestContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HTTPHypermediaRIM.class})
public class TestResponseHTTPHypermediaRIM {
	
	class MockEntity {
		String id;
		MockEntity(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}
	};

	@Before
	public void setup() {
		// initialise the thread local request context with requestUri and baseUri
        RequestContext ctx = new RequestContext("/baseuri", "/requesturi", null);
        RequestContext.setRequestContext(ctx);
	}

	private List<Action> mockActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action("GET", Action.TYPE.VIEW));
		actions.add(new Action("DO", Action.TYPE.ENTRY));
		return actions;
	}
	
	private Metadata createMockMetadata() {
		Metadata metadata = mock(Metadata.class);
		when(metadata.getEntityMetadata(any(String.class))).thenReturn(mock(EntityMetadata.class));
		return metadata;
	}

	/*
	 * This test checks that we receive a 404 'Not Found' if a GET command is not registered.
	 * Every resource must have a GET command, so no command means no resource (404)
	 */
	@Test
	public void testGETCommandNotRegistered() {
		// our empty command controller
		NewCommandController mockCommandController = mock(NewCommandController.class);
		when(mockCommandController.fetchCommand("GET")).thenReturn(mock(InteractionCommand.class));

		ResourceState initialState = new ResourceState("entity", "state", new ArrayList<Action>(), "/path");
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState), createMockMetadata());
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
		NewCommandController mockCommandController = mock(NewCommandController.class);
		when(mockCommandController.fetchCommand("GET")).thenReturn(mock(InteractionCommand.class));

		ResourceState initialState = new ResourceState("entity", "state", new ArrayList<Action>(), "/path");
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState), createMockMetadata());
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
		NewCommandController mockCommandController = mock(NewCommandController.class);
		when(mockCommandController.fetchCommand("GET")).thenReturn(mock(InteractionCommand.class));

		ResourceState initialState = new ResourceState("entity", "state", new ArrayList<Action>(), "/path");
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState), createMockMetadata());
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
		NewCommandController mockCommandController = mock(NewCommandController.class);
		when(mockCommandController.fetchCommand("GET")).thenReturn(mock(InteractionCommand.class));

		ResourceState initialState = new ResourceState("entity", "state", new ArrayList<Action>(), "/path");
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState), createMockMetadata());
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
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");
		initialState.addTransition(HttpMethod.PUT, initialState);
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
		};
		
		// create mock command controller
		NewCommandController mockCommandController = mock(NewCommandController.class);
		when(mockCommandController.fetchCommand("GET")).thenReturn(mock(InteractionCommand.class));
		when(mockCommandController.isValidCommand("DO")).thenReturn(true);
		when(mockCommandController.fetchCommand("DO")).thenReturn(mockCommand);

		// RIM with command controller that issues our mock InteractionCommand
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState), createMockMetadata());
		Response response = rim.put(mock(HttpHeaders.class), "id", mockEmptyUriInfo(), mock(EntityResource.class));
		
		// null resource for no content
		RESTResource resource = (RESTResource) response.getEntity();
		assertNull(resource);
		// 204 http status for no content
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
	}

	/*
	 * This test is for a DELETE request that returns HttpStatus 204 "No Content"
	 * A successful DELETE command does not return a new resource; where a target state
	 * is not found we'll inform the user agent that everything went OK, but there is 
	 * nothing more to display i.e. No Content.
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testBuildResponseWith204NoContentNoTransition() throws Exception {
		/*
		 * construct an InteractionContext that simply mocks the result of 
		 * deleting a resource, with no updated resource for the user agent
		 * to re-display
		 */
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");
		initialState.addTransition(HttpMethod.DELETE, initialState);
		InteractionContext testContext = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), initialState, mock(Metadata.class));
		testContext.setResource(null);
		// mock 'new InteractionContext()' in call to delete
		whenNew(InteractionContext.class).withArguments(any(MultivaluedMap.class), any(MultivaluedMap.class), any(ResourceState.class), any(Metadata.class)).thenReturn(testContext);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState), createMockMetadata());
		Response response = rim.delete(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		
		// null resource
		RESTResource resource = (RESTResource) response.getEntity();
		assertNull(resource);
		// 204 http status for No Content
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
	}

	/*
	 * This test is for a DELETE request that returns HttpStatus 204 "No Content"
	 * A successful DELETE command does not return a new resource; where a target state
	 * is a psuedo final state (effectively no target) we'll inform the user agent
	 * that everything went OK, but there is nothing more to display i.e. No Content.
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testBuildResponseWith204NoContent() throws Exception {
		/*
		 * construct an InteractionContext that simply mocks the result of 
		 * deleting a resource, with no updated resource for the user agent
		 * to re-display
		 */
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");
		initialState.addTransition("DELETE", initialState);
		InteractionContext testContext = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), initialState, mock(Metadata.class));
		testContext.setResource(null);
		// mock 'new InteractionContext()' in call to delete
		whenNew(InteractionContext.class).withArguments(any(MultivaluedMap.class), any(MultivaluedMap.class), any(ResourceState.class), any(Metadata.class)).thenReturn(testContext);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState), createMockMetadata());
		Response response = rim.delete(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		
		// null resource
		RESTResource resource = (RESTResource) response.getEntity();
		assertNull(resource);
		// 204 http status for No Content
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
	}

	/*
	 * This test is for a DELETE request that returns HttpStatus 205 "Reset Content"
	 * A successful DELETE command does not return a new resource and should inform
	 * the user agent to refresh the current view.
	 */
	@Test
	public void testBuildResponseWith205ContentReset() throws Exception {
		/*
		 * construct an InteractionContext that simply mocks the result of 
		 * deleting a resource, with no updated resource for the user agent
		 * to re-display
		 */
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");
		ResourceState deletedState = new ResourceState(initialState, "deleted", mockActions());
		initialState.addTransition("DELETE", deletedState);
		deletedState.addTransition(initialState);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState), createMockMetadata());
		Response response = rim.delete(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		
		// null resource
		RESTResource resource = (RESTResource) response.getEntity();
		assertNull(resource);
		// 205 http status for Reset Content
		assertEquals(HttpStatusTypes.RESET_CONTENT.getStatusCode(), response.getStatus());
	}

	/*
	 * This test is for a DELETE request that returns HttpStatus 205 "Reset Content"
	 * A successful DELETE command does not return a new resource and should inform
	 * the user agent to refresh the current view if the target is the same as the source.
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testBuildResponseWith205ContentResetDifferentResource() throws Exception {
		/*
		 * construct an InteractionContext that simply mocks the result of 
		 * deleting a resource, with no updated resource for the user agent
		 * to re-display
		 */
		CollectionResourceState initialState = new CollectionResourceState("entity", "state", mockActions(), "/entities");
		ResourceState existsState = new ResourceState(initialState, "exists", mockActions(), "/123");
		ResourceState deletedState = new ResourceState(existsState, "deleted", mockActions());
		initialState.addTransitionForEachItem("GET", existsState, null);
		initialState.addTransitionForEachItem("DELETE", deletedState, null);
		existsState.addTransition("DELETE", deletedState);
		// the auto transition
		deletedState.addTransition(initialState);
		
		InteractionContext testContext = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), initialState, mock(Metadata.class));
		testContext.setResource(null);
		// mock 'new InteractionContext()' in call to delete
		whenNew(InteractionContext.class).withArguments(any(MultivaluedMap.class), any(MultivaluedMap.class), any(ResourceState.class), any(Metadata.class)).thenReturn(testContext);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState), createMockMetadata());
		Collection<ResourceInteractionModel> children = rim.getChildren();
		// find the resource interaction model for the entity item
		HTTPHypermediaRIM itemRIM = null;
		for (ResourceInteractionModel r : children) {
			if (r.getResourcePath().equals("/entities/123")) {
				itemRIM = (HTTPHypermediaRIM) r;
			}
		}
		// mock the Link header
		HttpHeaders mockHeaders = mock(HttpHeaders.class);
		List<String> links = new ArrayList<String>();
		links.add("</path>; rel=\"entity.state>DELETE>entity.deleted\"");
		when(mockHeaders.getRequestHeader("Link")).thenReturn(links);
		Response response = itemRIM.delete(mockHeaders, "id", mockEmptyUriInfo());
		
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
	@Test
	public void testBuildResponseWith303SeeOtherSameEntity() throws Exception {
		/*
		 * construct an InteractionContext that simply mocks the result of 
		 * deleting a resource, with no updated resource for the user agent
		 * to re-display
		 */
		ResourceState initialState = new ResourceState("home", "initial", mockActions(), "/machines");
		ResourceState existsState = new ResourceState("toaster", "exists", mockActions(), "/machines/toaster");
		ResourceState cookingState = new ResourceState(existsState, "cooking", mockActions(), "/cooking");
		ResourceState idleState = new ResourceState(cookingState, "idle", mockActions());
		
		// view the toaster if it exists (could show time remaining if cooking)
		initialState.addTransition("GET", existsState);
		// start cooking the toast
		existsState.addTransition("PUT", cookingState);
		// stop the toast cooking
		cookingState.addTransition("DELETE", idleState);
		idleState.addTransition(existsState);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState), createMockMetadata());
		Collection<ResourceInteractionModel> children = rim.getChildren();
		// find the resource interaction model for the 'cooking' state
		HTTPHypermediaRIM cookingStateRIM = null;
		for (ResourceInteractionModel r : children) {
			if (r.getResourcePath().equals("/machines/toaster/cooking")) {
				cookingStateRIM = (HTTPHypermediaRIM) r;
			}
		}
		// mock the Link header
		HttpHeaders mockHeaders = mock(HttpHeaders.class);
		List<String> links = new ArrayList<String>();
		links.add("</path>; rel=\"toaster.cooking>toaster.idle\"");
		when(mockHeaders.getRequestHeader("Link")).thenReturn(links);
		Response response = cookingStateRIM.delete(mockHeaders, "id", mockEmptyUriInfo());
		
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

	/*
	 * This test is for a POST request that creates a new resource.
	 */
	@Test
	public void testBuildResponseWith201Created() throws Exception {
		/*
		 * construct an InteractionContext that simply mocks the result of 
		 * deleting a resource, with no updated resource for the user agent
		 * to re-display
		 */
		ResourceState initialState = new ResourceState("home", "initial", mockActions(), "/machines");
		ResourceState createPsuedoState = new ResourceState(initialState, "create", mockActions());
		ResourceState individualMachine = new ResourceState(initialState, "machine", mockActions(), "/{id}");
		
		// create new machine
		initialState.addTransition("POST", createPsuedoState);
		// an auto transition to the new resource
		createPsuedoState.addTransition(individualMachine);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState, new BeanTransformer()), createMockMetadata());
		Response response = rim.post(mock(HttpHeaders.class), "id", mockEmptyUriInfo(), mockEntityResourceWithId("123"));

		// null resource
		@SuppressWarnings("rawtypes")
		GenericEntity ge = (GenericEntity) response.getEntity();
		assertNotNull(ge);
		RESTResource resource = (RESTResource) ge.getEntity();
		assertNotNull(resource);
		/*
		 *  201 "Created" informs the user agent that 'the request has been fulfilled and resulted 
		 *  in a new resource being created'.  It can be accessed by a GET to the resource specified 
		 *  by the 'Location' header
		 */
		assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		List<Object> locationHeader = response.getMetadata().get("Location");
		assertNotNull(locationHeader);
        assertEquals(1, locationHeader.size());
        assertEquals("/baseuri/machines/123", locationHeader.get(0));
	}
	
	private EntityResource<Object> mockEntityResourceWithId(final String id) {
		return new EntityResource<Object>(new MockEntity(id));
	}
	
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testBuildResponseWithLinks() throws Exception {
		// construct an InteractionContext that simply mocks the result of loading a resource
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");
		InteractionContext testContext = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), initialState, mock(Metadata.class));
		testContext.setResource(new EntityResource<Object>(null));
		// mock 'new InteractionContext()' in call to get
		whenNew(InteractionContext.class).withArguments(any(MultivaluedMap.class), any(MultivaluedMap.class), any(ResourceState.class), any(Metadata.class)).thenReturn(testContext);
		
		List<Link> links = new ArrayList<Link>();
		links.add(new Link("id", "self", "href", null, null));
		
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState), createMockMetadata());
		Response response = rim.get(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		
		RESTResource resourceWithLinks = (RESTResource) ((GenericEntity<?>)response.getEntity()).getEntity();
		assertNotNull(resourceWithLinks.getLinks());
		assertFalse(resourceWithLinks.getLinks().isEmpty());
		assertEquals(1, resourceWithLinks.getLinks().size());
		Link link = (Link) resourceWithLinks.getLinks().toArray()[0];
		assertEquals("self", link.getRel());
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testBuildResponseEntityName() throws Exception {
		// construct an InteractionContext that simply mocks the result of loading a resource
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");
		InteractionContext testContext = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), initialState, mock(Metadata.class));
		testContext.setResource(new EntityResource<Object>(null));
		// mock 'new InteractionContext()' in call to get
		whenNew(InteractionContext.class).withArguments(any(MultivaluedMap.class), any(MultivaluedMap.class), any(ResourceState.class), any(Metadata.class)).thenReturn(testContext);
		
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockNoopCommandController(), new ResourceStateMachine(initialState), createMockMetadata());
		Response response = rim.get(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		
		RESTResource resource = (RESTResource) ((GenericEntity<?>)response.getEntity()).getEntity();
		assertNotNull(resource.getEntityName());
		assertEquals("entity", resource.getEntityName());
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
		when(commandController.isValidCommand(anyString())).thenReturn(true);
		when(commandController.fetchCommand(anyString())).thenReturn(testCommand);
		return commandController;
	}
	
	/*
	 * This test is for an OPTIONS request.
	 * A OPTIONS request uses a GET command, the response must include an Allow header
	 * and no body plus HttpStatus 204 "No Content".
	 */
	@Test
	public void testOPTIONSBuildResponseWithNoContent() throws Exception {
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");
		initialState.addTransition(HttpMethod.GET, initialState);
		/*
		 * Construct an InteractionCommand that simply mocks the result of 
		 * a successful command.
		 */
		InteractionCommand mockCommand = new InteractionCommand() {
			@Override
			public Result execute(InteractionContext ctx) {
				ctx.setResource(new EntityResource<Object>(null));
				return Result.SUCCESS;
			}
		};
		
		// create mock command controller
		NewCommandController mockCommandController = new NewCommandController();
		mockCommandController.addCommand("GET", mockCommand);
		mockCommandController.addCommand("DO", mockCommand);

		// RIM with command controller that issues our mock InteractionCommand
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState), createMockMetadata());
		Response response = rim.options(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		
		// 204 http status for no content
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		// check Allow header
		Object allow = response.getMetadata().getFirst("Allow");
        assertNotNull(allow);
        String[] allows = allow.toString().split(", ");
		assertEquals(3, allows.length);
		List<String> allowsList = Arrays.asList(allows);
        assertTrue(allowsList.contains("GET"));
        assertTrue(allowsList.contains("OPTIONS"));
        assertTrue(allowsList.contains("HEAD"));
	}

}
