package com.temenos.interaction.core.rim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.BeanTransformer;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.web.RequestContext;

public class TestHTTPHypermediaRIM {

	@Before
	public void setup() {
		// initialise the thread local request context with requestUri and baseUri
        RequestContext ctx = new RequestContext("http://localhost/myservice.svc", "/baseuri/", null);
        RequestContext.setRequestContext(ctx);
	}

	private List<Action> mockActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action("DO", Action.TYPE.ENTRY));
		actions.add(new Action("GET", Action.TYPE.VIEW));
		return actions;
	}
	
	private NewCommandController mockCommandController() {
		NewCommandController cc = mock(NewCommandController.class);
		when(cc.fetchCommand("DO")).thenReturn(mock(InteractionCommand.class));
		when(cc.fetchCommand("GET")).thenReturn(mock(InteractionCommand.class));
		return cc;
	}

	private NewCommandController mockCommandController(InteractionCommand mockCommand) {
		NewCommandController cc = mock(NewCommandController.class);
		when(cc.fetchCommand("DO")).thenReturn(mockCommand);
		when(cc.fetchCommand("GET")).thenReturn(mockCommand);
		return cc;
	}

	@Test
	public void testResourcePath() {
		String ENTITY_NAME = "NOTE";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", mockActions(), "/notes/{id}");
		HTTPHypermediaRIM resource = new HTTPHypermediaRIM(mockCommandController(), new ResourceStateMachine(initial));
		assertEquals("/notes/{id}", resource.getResourcePath());
	}

	/* We decode the query parameters to workaround an issue in Wink */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testDecodeQueryParameters() {
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/test");
		// this test simply mocks a command to test the context query parameters is initialised properly
		InteractionCommand mockCommand = mock(InteractionCommand.class);
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController(mockCommand), new ResourceStateMachine(initialState));
		
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPathParameters(true)).thenReturn(mock(MultivaluedMap.class));
		MultivaluedMap<String, String> queryMap = new MultivaluedMapImpl<String, String>();
		queryMap.add("$filter", "this+that");
		when(uriInfo.getQueryParameters(anyBoolean())).thenReturn(queryMap);
		
		rim.get(mock(HttpHeaders.class), "id", uriInfo);
		verify(mockCommand).execute((InteractionContext) argThat(new InteractionContextArgumentMatcher()));
	}

	class InteractionContextArgumentMatcher extends ArgumentMatcher<InteractionContext> {
		public boolean matches(Object o) {
			if (o instanceof InteractionContext) {
				InteractionContext ctx = (InteractionContext) o;
				MultivaluedMap<String, String> mvmap = ctx.getQueryParameters();
				if (!mvmap.getFirst("$filter").equals("this that")) {
					return false;
				}
				return true;
			}
            return false;
        }
    }

	/* We decode the query parameters to workaround an issue in Wink */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testDecodeQueryParametersNullValue() {
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/test");
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController(), new ResourceStateMachine(initialState));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPathParameters(true)).thenReturn(mock(MultivaluedMap.class));
		MultivaluedMap<String, String> queryMap = new MultivaluedMapImpl();
		queryMap.add(null, null);
		when(uriInfo.getQueryParameters(anyBoolean())).thenReturn(queryMap);

		// should get past here without a NullPointerException
		rim.get(mock(HttpHeaders.class), "id", uriInfo);
	}

	/*
	 * This test is for a GET request where the command succeeds, but 
	 * does not return a resource.
	 * A successful GET command should set the requested resource onto
	 * the InteractionContext; we test this with an assertion.
	 */
	@Test(expected = AssertionError.class)
	public void testSuccessfulGETCommandNoResourceShouldFail() throws Exception {
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");

		// this test incorrectly supplies a resource as a result of the command.
		InteractionCommand mockCommand = new InteractionCommand() {
			public Result execute(InteractionContext ctx) {
				ctx.setResource(null);
				return Result.SUCCESS;
			}
		};

		// create mock command controller
		NewCommandController mockCommandController = mockCommandController(mockCommand);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState));
		rim.get(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
	}

	/*
	 * This test is for a GET request where the command succeeds.
	 * A successful GET command should set the requested resource onto
	 * the InteractionContext.
	 */
	public void testSuccessfulGETCommand() throws Exception {
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");

		// this test incorrectly supplies a resource as a result of the command.
		InteractionCommand mockCommand = new InteractionCommand() {
			public Result execute(InteractionContext ctx) {
				ctx.setResource(new EntityResource<Object>());
				return Result.SUCCESS;
			}
		};

		// create mock command controller
		NewCommandController mockCommandController = mock(NewCommandController.class);
		when(mockCommandController.fetchCommand("DO")).thenReturn(mockCommand);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState));
		Response response = rim.get(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
		assertNotNull(response.getEntity());
	}

	/*
	 * This test is for a DELETE request where the command returns a resource.
	 * A successful DELETE command should not return a new resource and we test
	 * this with an assertion.
	 */
	@Test(expected = AssertionError.class)
	public void testDeleteCommandReturnsResourceShouldFail() throws Exception {
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/path");
		initialState.addTransition("DELETE", initialState);

		// this test incorrectly supplies a resource as a result of the command.
		InteractionCommand mockCommand = new InteractionCommand() {
			public Result execute(InteractionContext ctx) {
				ctx.setResource(new EntityResource<Object>());
				return Result.SUCCESS;
			}
		};

		// create mock command controller
		NewCommandController mockCommandController = mockCommandController(mockCommand);
		
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController, new ResourceStateMachine(initialState));
		rim.delete(mock(HttpHeaders.class), "id", mockEmptyUriInfo());
	}

	/* Test the contract for PUT commands.
	 * A PUT command should should receive an InteractionContext that has the new
	 * resource set; enabling the command to getResource.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPutCommandReceivesResource() {
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/test");
		initialState.addTransition("PUT", initialState);
		// create a mock command to test the context is initialised correctly
		InteractionCommand mockCommand = mock(InteractionCommand.class);
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController(mockCommand), new ResourceStateMachine(initialState));
		
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPathParameters(anyBoolean())).thenReturn(mock(MultivaluedMap.class));
		when(uriInfo.getQueryParameters(anyBoolean())).thenReturn(mock(MultivaluedMap.class));
		
		rim.put(mock(HttpHeaders.class), "id", uriInfo, new EntityResource<Object>("test resource"));
		verify(mockCommand).execute((InteractionContext) argThat(new CommandReceivesResourceArgumentMatcher()));
	}

	class CommandReceivesResourceArgumentMatcher extends ArgumentMatcher<InteractionContext> {
		public boolean matches(Object o) {
			if (o instanceof InteractionContext) {
				InteractionContext ctx = (InteractionContext) o;
				if (ctx.getResource() == null)
					return false;
				return true;
			}
            return false;
        }
    }

	/* Test the contract for POST commands.
	 * A POST command should could receive an InteractionContext that has the new
	 * resource set; enabling the command to getResource.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPOSTCommandReceivesResource() {
		ResourceState initialState = new ResourceState("entity", "state", mockActions(), "/test");
		initialState.addTransition("POST", initialState);
		// create a mock command to test the context is initialised correctly
		InteractionCommand mockCommand = mock(InteractionCommand.class);
		// RIM with command controller that issues commands that always return SUCCESS
		HTTPHypermediaRIM rim = new HTTPHypermediaRIM(mockCommandController(mockCommand), new ResourceStateMachine(initialState));
		
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPathParameters(anyBoolean())).thenReturn(mock(MultivaluedMap.class));
		when(uriInfo.getQueryParameters(anyBoolean())).thenReturn(mock(MultivaluedMap.class));
		
		rim.post(mock(HttpHeaders.class), "id", uriInfo, new EntityResource<Object>("test resource"));
		verify(mockCommand).execute((InteractionContext) argThat(new CommandReceivesResourceArgumentMatcher()));
	}

	
	@Test(expected = RuntimeException.class)
	public void testBootstrapInvalidCommandControllerConfigurationPUT() {
		String resourcePath = "/notes/{id}";
		ResourceState exists = new ResourceState("entity", "exists", mockActions(), resourcePath);
		exists.addTransition("PUT", exists);
		
		NewCommandController cc = mock(NewCommandController.class);
		new HTTPHypermediaRIM(cc, new ResourceStateMachine(exists));
	}

	@Test(expected = RuntimeException.class)
	public void testBootstrapInvalidCommandControllerConfigurationPOST() {
		String resourcePath = "/notes/{id}";
		ResourceState exists = new ResourceState("entity", "exists", mockActions(), resourcePath);
		exists.addTransition("POST", exists);
		
		NewCommandController cc = mock(NewCommandController.class);
		new HTTPHypermediaRIM(cc, new ResourceStateMachine(exists));
	}

	@Test(expected = RuntimeException.class)
	public void testBootstrapInvalidCommandControllerConfigurationDELETE() {
		String resourcePath = "/notes/{id}";
		ResourceState exists = new ResourceState("entity", "exists", mockActions(), resourcePath);
		exists.addTransition("DELETE", exists);
		
		NewCommandController cc = mock(NewCommandController.class);
		new HTTPHypermediaRIM(cc, new ResourceStateMachine(exists));
	}

	@Test(expected = RuntimeException.class)
	public void testBootstrapInvalidCommandControllerConfigurationGET() {
		String resourcePath = "/notes/{id}";
		ResourceState exists = new ResourceState("entity", "exists", mockActions(), resourcePath);
		exists.addTransition("GET", exists);
		
		NewCommandController cc = mock(NewCommandController.class);
		new HTTPHypermediaRIM(cc, new ResourceStateMachine(exists));
	}

	/*
	@Test
	public void testBootstrapRIMsCRUD() {
		String ENTITY_NAME = "NOTE";
		String resourcePath = "/notes/{id}";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", mockActions(), resourcePath);
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", mockActions(), resourcePath);
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted", mockActions(), resourcePath);

		// create
		initial.addTransition("PUT", exists);
		// update
		exists.addTransition("PUT", exists);
		// delete
		exists.addTransition("DELETE", deleted);
		
		// mock command controller to do nothing
		NewCommandController cc = mock(NewCommandController.class);
		when(cc.fetchCommand(anyString(), anyString())).thenReturn(mock(InteractionCommand.class));
		
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(cc, new ResourceStateMachine(initial));
		verify(cc).fetchCommand("GET", resourcePath);
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(0, resources.size());
		verify(cc, times(1)).fetchCommand("GET", resourcePath);
		verify(cc, times(1)).fetchCommand("PUT", resourcePath);
		verify(cc, times(1)).fetchCommand("DELETE", resourcePath);
	}

	@Test
	public void testBootstrapRIMsSubstate() {
		String ENTITY_NAME = "DraftNote";
		String resourcePath = "/notes/{id}";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		ResourceState exists = new ResourceState(initial, "exists", "/exists");
		ResourceState deleted = new ResourceState(exists, "deleted", null);
		ResourceState draft = new ResourceState(ENTITY_NAME, "draft", "/notes/{id}/draft");
		ResourceState deletedDraft = new ResourceState(draft, "deleted");
	
		// create
		initial.addTransition("PUT", exists);
		// create draft
		initial.addTransition("PUT", draft);
		// updated draft
		draft.addTransition("PUT", draft);
		// publish
		draft.addTransition("PUT", exists);
		// delete draft
		draft.addTransition("DELETE", deletedDraft);
		// delete published
		exists.addTransition("DELETE", deleted);
		
		// mock command controller to do nothing
		NewCommandController cc = mock(NewCommandController.class);
		when(cc.fetchCommand(anyString(), anyString())).thenReturn(mock(InteractionCommand.class));

		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(cc, stateMachine);
		verify(cc).fetchCommand("GET", "/notes/{id}");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(2, resources.size());
		assertEquals(draft, resources.iterator().next().getCurrentState());
		verify(cc, times(1)).fetchCommand("GET", "/notes/{id}/exists");
		verify(cc).fetchCommand("DELETE", "/notes/{id}/exists");
		verify(cc).fetchCommand("PUT", "/notes/{id}/exists");
		verify(cc, times(1)).fetchCommand("GET", "/notes/{id}/draft");
		verify(cc).fetchCommand("DELETE", "/notes/{id}/draft");
		verify(cc).fetchCommand("PUT", "/notes/{id}/draft");
	}

	@Test
	public void testBootstrapRIMsMultipleSubstates() {
		String ENTITY_NAME = "PublishNote";
		String resourcePath = "/notes/{id}";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		ResourceState published = new ResourceState(ENTITY_NAME, "published", "/notes/{id}/published");
		ResourceState publishedDeleted = new ResourceState(published, "publishedDeleted", null);
		ResourceState draft = new ResourceState(ENTITY_NAME, "draft", "/notes/{id}/draft");
		ResourceState deletedDraft = new ResourceState(draft, "draftDeleted");
	
		// create draft
		initial.addTransition("PUT", draft);
		// updated draft
		draft.addTransition("PUT", draft);
		// publish
		draft.addTransition("PUT", published);
		// delete draft
		draft.addTransition("DELETE", deletedDraft);
		// delete published
		published.addTransition("DELETE", publishedDeleted);
		
		// mock command controller to do nothing
		NewCommandController cc = mock(NewCommandController.class);
		when(cc.fetchCommand(anyString(), anyString())).thenReturn(mock(InteractionCommand.class));
		
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(cc, stateMachine);
		verify(cc).fetchCommand("GET", "/notes/{id}");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(2, resources.size());
		verify(cc, times(1)).fetchCommand("GET", "/notes/{id}");
		verify(cc, times(1)).fetchCommand("GET", "/notes/{id}/draft");
		verify(cc).fetchCommand("PUT", "/notes/{id}/draft");
		verify(cc).fetchCommand("DELETE", "/notes/{id}/draft");
		verify(cc, times(1)).fetchCommand("GET", "/notes/{id}/published");
		verify(cc).fetchCommand("DELETE", "/notes/{id}/published");
		verify(cc).fetchCommand("PUT", "/notes/{id}/published");
	}

	@Test
	public void testBootstrapRIMsMultipleSubstates1() {
		String ENTITY_NAME = "BOOKING";
		String resourcePath = "/bookings";
		
		// the booking
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", resourcePath);
  		ResourceState bookingCreated = new ResourceState(begin, "bookingCreated", "/{id}");
  		ResourceState bookingCancellation = new ResourceState(bookingCreated, "cancellation", "/cancellation");
  		ResourceState deleted = new ResourceState(bookingCancellation, "deleted", null);

		begin.addTransition("PUT", bookingCreated);
		bookingCreated.addTransition("PUT", bookingCancellation);
		bookingCancellation.addTransition("DELETE", deleted);

		// the payment
		ResourceState payment = new ResourceState(bookingCreated, "payment", "/payment");
		ResourceState confirmation = new ResourceState(payment, "pconfirmation", "/pconfirmation");
		ResourceState waitingForConfirmation = new ResourceState(payment, "pwaiting", "/pwaiting");

		payment.addTransition("PUT", waitingForConfirmation);
		payment.addTransition("PUT", confirmation);
		waitingForConfirmation.addTransition("PUT", confirmation);
		
		// linking the two state machines together
		bookingCreated.addTransition("PUT", payment);  // TODO needs to be conditional
		confirmation.addTransition("PUT", bookingCancellation);
		
		// mock command controller to do nothing
		NewCommandController cc = mock(NewCommandController.class);
		when(cc.fetchCommand(anyString(), anyString())).thenReturn(mock(InteractionCommand.class));
		
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(cc, new ResourceStateMachine(begin));
		verify(cc, times(1)).fetchCommand("GET", "/bookings");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(5, resources.size());
		verify(cc, times(1)).fetchCommand("GET", "/bookings/{id}");
		verify(cc).fetchCommand("PUT", "/bookings/{id}");
		verify(cc, times(1)).fetchCommand("GET", "/bookings/{id}/cancellation");
		verify(cc).fetchCommand("DELETE", "/bookings/{id}/cancellation");
		verify(cc).fetchCommand("PUT", "/bookings/{id}/cancellation");
		verify(cc, times(1)).fetchCommand("GET", "/bookings/{id}/payment");
		verify(cc).fetchCommand("PUT", "/bookings/{id}/payment");
		verify(cc, times(1)).fetchCommand("GET", "/bookings/{id}/payment/pconfirmation");
		verify(cc).fetchCommand("PUT", "/bookings/{id}/payment/pconfirmation");
		verify(cc, times(1)).fetchCommand("GET", "/bookings/{id}/payment/pwaiting");
		verify(cc).fetchCommand("PUT", "/bookings/{id}/payment/pwaiting");
	}
*/
	@Test
	public void testChildrenRIMsSubstate() {
		String ENTITY_NAME = "DraftNote";
		String resourcePath = "/notes/{id}";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", mockActions(), resourcePath);
		ResourceState draft = new ResourceState(ENTITY_NAME, "draft", mockActions(), "/draft");
	
		// create draft
		initial.addTransition("PUT", draft);
		// updated draft
		draft.addTransition("PUT", draft);
		
		// supply a transformer to check that this is copied into child resource
		BeanTransformer transformer = new BeanTransformer();
		
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial, transformer);
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(mockCommandController(), stateMachine);
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(1, resources.size());
		assertEquals(draft.getPath(), resources.iterator().next().getResourcePath());
		assertEquals(transformer, ((HTTPHypermediaRIM) resources.iterator().next()).getHypermediaEngine().getTransformer());
	}

	@Test
	public void testChildrenRIMsDifferentEntity() {
  		ResourceState initial = new ResourceState("Note", "initial", mockActions(), "/note/{id}");
		ResourceState comment = new ResourceState("Comment", "draft", mockActions(), "/comments/{noteid}");
	
		// example uri linkage uses 'id' from Note entity to transition to 'noteid' of comments resource
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("noteid", "id");
		// create comment for note
		initial.addTransition("PUT", comment, uriLinkageMap);
		// update comment
		comment.addTransition("PUT", comment);
		
		// supply a transformer to check that this is copied into child resource
		BeanTransformer transformer = new BeanTransformer();
		
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial, transformer);
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(mockCommandController(), stateMachine);
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(1, resources.size());
		assertEquals(comment.getPath(), resources.iterator().next().getResourcePath());
		assertEquals(transformer, ((HTTPHypermediaRIM) resources.iterator().next()).getHypermediaEngine().getTransformer());
	}

	@SuppressWarnings({ "unchecked" })
	private UriInfo mockEmptyUriInfo() {
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPathParameters(true)).thenReturn(mock(MultivaluedMap.class));
		when(uriInfo.getQueryParameters(true)).thenReturn(mock(MultivaluedMap.class));
		return uriInfo;
	}

}
