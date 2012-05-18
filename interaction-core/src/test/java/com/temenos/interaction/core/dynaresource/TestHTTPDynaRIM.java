package com.temenos.interaction.core.dynaresource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.odata4j.edm.EdmDataServices;

import com.jayway.jaxrs.hateoas.HateoasContext;
import com.jayway.jaxrs.hateoas.HateoasLink;
import com.jayway.jaxrs.hateoas.LinkableInfo;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.state.ResourceInteractionModel;
import com.temenos.interaction.core.web.RequestContext;


public class TestHTTPDynaRIM {

	@Before
	public void setup() {
		// initialise the thread local request context with requestUri and baseUri
		UriBuilder baseUri = UriBuilder.fromUri("/baseuri");
		String requestUri = "/baseuri/";
        RequestContext ctx = new RequestContext(baseUri, requestUri, null);
        RequestContext.setRequestContext(ctx);
	}
	
	@Test
	public void testResourcePath() {
		String ENTITY_NAME = "NOTE";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "/notes/{id}");
		ResourceRegistry rr = mock(ResourceRegistry.class);
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM resource = new HTTPDynaRIM(new ResourceStateMachine(initial), rr, cc);
		assertEquals("/notes/{id}", resource.getResourcePath());
	}
	
	@Test
	public void testRIMsCRUD() {
		String ENTITY_NAME = "NOTE";
		String resourcePath = "/notes/{id}";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		ResourceState exists = new ResourceState(initial, "exists");
		ResourceState deleted = new ResourceState(initial, "deleted");

		// create
		initial.addTransition("PUT", exists);
		// update
		exists.addTransition("PUT", exists);
		// delete
		exists.addTransition("DELETE", deleted);
		
		ResourceRegistry rr = mock(ResourceRegistry.class);
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(initial), initial, rr, cc);
		verify(cc).fetchGetCommand(resourcePath);
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(0, resources.size());
		verify(cc, times(1)).fetchGetCommand(resourcePath);
		verify(cc, times(1)).fetchStateTransitionCommand("PUT", resourcePath);
		verify(cc, times(1)).fetchStateTransitionCommand("DELETE", resourcePath);
	}

	@Test
	public void testRIMsSubstate() {
		String ENTITY_NAME = "DraftNote";
		String resourcePath = "/notes/{id}";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		ResourceState exists = new ResourceState(initial, "exists");
		ResourceState draft = new ResourceState(ENTITY_NAME, "draft", "/draft");
		ResourceState deleted = new ResourceState(initial, "deleted");
	
		// create
		initial.addTransition("PUT", exists);
		// create draft
		initial.addTransition("PUT", draft);
		// updated draft
		draft.addTransition("PUT", draft);
		// publish
		draft.addTransition("PUT", exists);
		// delete draft
		draft.addTransition("DELETE", deleted);
		// delete published
		exists.addTransition("DELETE", deleted);
		
		ResourceRegistry rr = mock(ResourceRegistry.class);
		CommandController cc = mock(CommandController.class);
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, stateMachine, initial, rr, cc);
		verify(cc).fetchGetCommand("/notes/{id}");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(1, resources.size());
		verify(cc, times(1)).fetchGetCommand("/notes/{id}");
		verify(cc, times(1)).fetchGetCommand("/notes/{id}/draft");
		verify(cc).fetchStateTransitionCommand("PUT", "/notes/{id}");
		verify(cc).fetchStateTransitionCommand("PUT", "/notes/{id}/draft");
		verify(cc).fetchStateTransitionCommand("DELETE", "/notes/{id}/draft");
		verify(cc).fetchStateTransitionCommand("DELETE", "/notes/{id}");
	}

	@Test
	public void testRIMsMultipleSubstates() {
		String ENTITY_NAME = "PublishNote";
		String resourcePath = "/notes/{id}";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		ResourceState published = new ResourceState(ENTITY_NAME, "published", "/published");
		ResourceState draft = new ResourceState(ENTITY_NAME, "draft", "/draft");
		ResourceState deleted = new ResourceState(initial, "deleted");
	
		// create draft
		initial.addTransition("PUT", draft);
		// updated draft
		draft.addTransition("PUT", draft);
		// publish
		draft.addTransition("PUT", published);
		// delete draft
		draft.addTransition("DELETE", deleted);
		// delete published
		published.addTransition("DELETE", deleted);
		
		ResourceRegistry rr = mock(ResourceRegistry.class);
		CommandController cc = mock(CommandController.class);
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, stateMachine, initial, rr, cc);
		verify(cc).fetchGetCommand("/notes/{id}");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(2, resources.size());
		verify(cc, times(1)).fetchGetCommand("/notes/{id}");
		verify(cc, times(1)).fetchGetCommand("/notes/{id}/draft");
		verify(cc).fetchStateTransitionCommand("PUT", "/notes/{id}/draft");
		verify(cc).fetchStateTransitionCommand("DELETE", "/notes/{id}/draft");
		verify(cc, times(1)).fetchGetCommand("/notes/{id}/published");
		verify(cc).fetchStateTransitionCommand("DELETE", "/notes/{id}/published");
		verify(cc).fetchStateTransitionCommand("PUT", "/notes/{id}/published");
	}

	@Test
	public void testRIMsMultipleSubstates1() {
		String ENTITY_NAME = "BOOKING";
		String resourcePath = "/bookings/{id}";
		
		// the booking
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", resourcePath);
  		ResourceState bookingCreated = new ResourceState(begin, "bookingCreated");
  		ResourceState bookingCancellation = new ResourceState(ENTITY_NAME, "cancellation", "/cancellation");
  		ResourceState deleted = new ResourceState(begin, "deleted");

		begin.addTransition("PUT", bookingCreated);
		bookingCreated.addTransition("PUT", bookingCancellation);
		bookingCancellation.addTransition("DELETE", deleted);

		// the payment
		ResourceState payment = new ResourceState(ENTITY_NAME, "payment", "/payment");
		ResourceState confirmation = new ResourceState(ENTITY_NAME, "pconfirmation", "/payment/pconfirmation");
		ResourceState waitingForConfirmation = new ResourceState(ENTITY_NAME, "pwaiting", "/payment/pwaiting");

		payment.addTransition("PUT", waitingForConfirmation);
		payment.addTransition("PUT", confirmation);
		waitingForConfirmation.addTransition("PUT", confirmation);
		
		// linking the two state machines together
		bookingCreated.addTransition("PUT", payment);  // TODO needs to be conditional
		confirmation.addTransition("PUT", bookingCancellation);
		
		
		ResourceRegistry rr = mock(ResourceRegistry.class);
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(begin), begin, rr, cc);
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(4, resources.size());
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}/cancellation");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}/payment");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}/payment/pconfirmation");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}/payment/pwaiting");
		verify(cc).fetchStateTransitionCommand("PUT", "/bookings/{id}");
		verify(cc).fetchStateTransitionCommand("PUT", "/bookings/{id}/cancellation");
		verify(cc).fetchStateTransitionCommand("PUT", "/bookings/{id}/payment");
		verify(cc).fetchStateTransitionCommand("PUT", "/bookings/{id}/payment/pwaiting");
		verify(cc).fetchStateTransitionCommand("PUT", "/bookings/{id}/payment/pconfirmation");
		verify(cc).fetchStateTransitionCommand("DELETE", "/bookings/{id}/cancellation");
	}

	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the link to 'self' correctly for our test resource.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
// TODO disabled until we support build(Map) uri
	//	@Test
	public void testGetLinksSelf() {
		String ENTITY_NAME = "NOTE";
		String resourcePath = "/notes/{id}";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		CommandController cc = new CommandController();
		
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		ResourceGetCommand testCommand = mock(ResourceGetCommand.class);
		when(testCommand.get(anyString(), any(MultivaluedMap.class))).thenReturn(new RESTResponse(Status.OK, testResponseEntity));
		
		/* 
		 * Create the dynamic resource (no parent).
		 * No resource registry indicates we'll set the links on the resource
		 * and not use the HateoasContext.
		 */
		cc.setGetCommand(resourcePath, testCommand);
		HTTPDynaRIM resource = new HTTPDynaRIM(new ResourceStateMachine(initial), null, cc);
				
		// call the get and populate the links
		Response response = resource.get(null, "id", null);
		RESTResource resourceWithLinks = (RESTResource) ((GenericEntity) response.getEntity()).getEntity();
		assertNotNull(resourceWithLinks.getLinks());
		assertFalse(resourceWithLinks.getLinks().isEmpty());
		assertEquals(1, resourceWithLinks.getLinks().size());
		HateoasLink link = (HateoasLink) resourceWithLinks.getLinks().toArray()[0];
		assertEquals("self", link.getRel());
		assertEquals("/baseuri/notes/{id}", link.getHref());
		assertEquals("NOTE.initial", link.getId());
	}

	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the links to other resource in our state
	 * machine.
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testGetLinksOtherResources() {
		HTTPDynaRIM resource = createDynaResourceWithLinks();
				
		// call the get and populate the links
		Response response = resource.get(null, "id", null);
		RESTResource resourceWithLinks = (RESTResource) ((GenericEntity) response.getEntity()).getEntity();
		assertNotNull(resourceWithLinks.getLinks());
		assertFalse(resourceWithLinks.getLinks().isEmpty());
		assertEquals(3, resourceWithLinks.getLinks().size());
		/*
		 * expect 3 links
		 * 'self'
		 * 'collection notes'
		 * 'colleciton persons'
		 */
		List<HateoasLink> links = new ArrayList<HateoasLink>(resourceWithLinks.getLinks());
		// sort the links so we have a predictable order for this test
		Collections.sort(links, new Comparator<HateoasLink>() {
			@Override
			public int compare(HateoasLink o1, HateoasLink o2) {
				return o1.getId().compareTo(o2.getId());
			}
			
		});
		assertEquals(3, links.size());
		// service root
		assertEquals("self", links.get(0).getRel());
		assertEquals("/baseuri/", links.get(0).getHref());
		assertEquals("root.initial", links.get(0).getId());
		// notes
		assertEquals("NOTE.collection", links.get(1).getRel());
		assertEquals("/baseuri/notes", links.get(1).getHref());
		assertEquals("root.initial>NOTE.collection", links.get(1).getId());
		// persons
		assertEquals("PERSON.collection", links.get(2).getRel());
		assertEquals("/baseuri/persons", links.get(2).getHref());
		assertEquals("root.initial>PERSON.collection", links.get(2).getId());
	}
	
	@SuppressWarnings({ "unchecked" })
	private HTTPDynaRIM createDynaResourceWithLinks() {
		String notesResourcePath = "/";
		ResourceState initial = new ResourceState("root", "initial", notesResourcePath);
		String NOTE_ENTITY = "NOTE";
		String noteResourcePath = "/notes";
		ResourceState notesResource = new ResourceState(NOTE_ENTITY, "collection", noteResourcePath);
		String PERSON_ENTITY = "PERSON";
		String personResourcePath = "/persons";
		ResourceState personsResource = new ResourceState(PERSON_ENTITY, "collection", personResourcePath);
		
		// create the transitions (links)
		initial.addTransition("GET", notesResource);
		initial.addTransition("GET", personsResource);
		
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		ResourceGetCommand testCommand = mock(ResourceGetCommand.class);
		when(testCommand.get(anyString(), any(MultivaluedMap.class))).thenReturn(new RESTResponse(Status.OK, testResponseEntity));
		
		/* 
		 * Create the dynamic resource (no parent).
		 * No resource registry indicates we'll set the links on the resource
		 * and not use the HateoasContext.
		 */
		CommandController cc = new CommandController();
		cc.setGetCommand(notesResourcePath, testCommand);
		cc.setGetCommand(noteResourcePath, testCommand);
		cc.setGetCommand(personResourcePath, testCommand);
		HTTPDynaRIM resource = new HTTPDynaRIM(new ResourceStateMachine(initial), null, cc);
		return resource;
	}
	
	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the hateoas context correctly for our test resource.
	 */
	@Test
	public void testLinksSelf() {
		String ENTITY_NAME = "NOTE";
		String resourcePath = "/notes/{id}";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPDynaRIM>());
		CommandController cc = mock(CommandController.class);
		
		// create the dynamic resource, this also registers itself with the ResourceRegistry
		HTTPDynaRIM resource = new HTTPDynaRIM(null, new ResourceStateMachine(initial), initial, rr, cc);
		HateoasContext context = resource.getHateoasContext();
		
		// every dynamic resource should have the information to link to itself
        LinkableInfo result = context.getLinkableInfo(ENTITY_NAME);
        assertNotNull(result);
        assertEquals("NOTE", result.getId());
        assertEquals("GET", result.getHttpMethod());
        assertEquals("/notes/{id}", result.getMethodPath());
        assertEquals("lookup label from EDMX", result.getLabel());
        assertEquals("lookup description from EDMX", result.getDescription());
	}

	@Test
	public void testLinksApplicationState() {
		String ENTITY_NAME = "SERVICE";
		ResourceState serviceRoot = new ResourceState(ENTITY_NAME, "home", "");

		ResourceState customers = new ResourceState("CUSTOMER", "customers", "/customers");
		ResourceStateMachine customerSM = new ResourceStateMachine(customers);
		ResourceState accounts = new ResourceState("ACCOUNT", "accounts", "/accounts");
		ResourceStateMachine accountSM = new ResourceStateMachine(accounts);
		ResourceState transactions = new ResourceState("TRANSACTION", "transactions", "/txns");
		ResourceStateMachine txnSM = new ResourceStateMachine(transactions);

		// Create links from service root
		serviceRoot.addTransition("GET", customerSM);
		serviceRoot.addTransition("GET", accountSM);
		serviceRoot.addTransition("GET", txnSM);

		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPDynaRIM>());
		CommandController cc = mock(CommandController.class);
		
		// create the dynamic resource, this also registers itself with the ResourceRegistry
		HTTPDynaRIM resource = new HTTPDynaRIM(null, new ResourceStateMachine(serviceRoot), serviceRoot, rr, cc);
		// this creates and registers the child resources / links
		resource.getChildren();
		
		HateoasContext context = resource.getHateoasContext();
		// every dynamic resource should have the information to link to itself
        LinkableInfo result = context.getLinkableInfo(ENTITY_NAME);
        assertNotNull(result);
        assertEquals("SERVICE", result.getId());
        assertEquals("GET", result.getHttpMethod());
        assertEquals("", result.getMethodPath());

        // customers
        LinkableInfo linkToCustomers = context.getLinkableInfo("CUSTOMER.customers");
        assertNotNull(linkToCustomers);
        assertEquals("CUSTOMER.customers", linkToCustomers.getId());
        assertEquals("GET", linkToCustomers.getHttpMethod());
        assertEquals("/customers", linkToCustomers.getMethodPath());

        // accounts
        LinkableInfo linkToAccounts = context.getLinkableInfo("ACCOUNT.accounts");
        assertNotNull(linkToAccounts);
        assertEquals("ACCOUNT.accounts", linkToAccounts.getId());
        assertEquals("GET", linkToAccounts.getHttpMethod());
        assertEquals("/accounts", linkToAccounts.getMethodPath());

        // transactions
        LinkableInfo linkToTxns = context.getLinkableInfo("TRANSACTION.transactions");
        assertNotNull(linkToTxns);
        assertEquals("TRANSACTION.transactions", linkToTxns.getId());
        assertEquals("GET", linkToTxns.getHttpMethod());
        assertEquals("/txns", linkToTxns.getMethodPath());

	}

	@Test
	public void testEquality() {
		String ENTITY_NAME = "NOTE";
		// use same resource path, vital to this equality test
		String resourcePath = "/notes";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", resourcePath);
		String OTHER_ENTITY_NAME = "DIFFERENT";
		ResourceState begin2 = new ResourceState(OTHER_ENTITY_NAME, "begin2", resourcePath);
		CommandController cc = mock(CommandController.class);
		CommandController cc2 = mock(CommandController.class);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(null, new ResourceStateMachine(begin), begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(null, new ResourceStateMachine(begin2), begin2, null, cc2);
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testEqualityParent() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "/{id}");
		String PARENT_ENTITY_NAME = "PARENT";
		ResourceState parentBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", "/notes");
		String DIFFERENT_ENTITY_NAME = "DIFFERENT";
		ResourceState differentBegin = new ResourceState(DIFFERENT_ENTITY_NAME, "begin", "/{id}");
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(parentBegin), parentBegin, null, cc);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(parent, new ResourceStateMachine(begin), begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(parent, new ResourceStateMachine(differentBegin), differentBegin, null, cc);
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testInequality() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "/{id}");
		ResourceState different = new ResourceState(ENTITY_NAME, "begin", "/{id}/different");
		String PARENT_ENTITY_NAME = "PARENT";
		ResourceState parentBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", "/notes");
		ResourceState parentDiffBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", "/notes1");
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(parentBegin), parentBegin, null, cc);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(parent, new ResourceStateMachine(begin), begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(parent, new ResourceStateMachine(different), different, null, cc);
		HTTPDynaRIM rim3 = new HTTPDynaRIM(null, new ResourceStateMachine(parentDiffBegin), parentDiffBegin, null, cc);
		HTTPDynaRIM rim4 = new HTTPDynaRIM(null, new ResourceStateMachine(begin), begin, null, cc);

		// both with parent (different)
		assertFalse(rim1.equals(rim2));
		assertFalse(rim1.hashCode() == rim2.hashCode());

		// both without parent (different)
		assertFalse(parent.equals(rim3));
		assertFalse(parent.hashCode() == rim3.hashCode());

		// one with parent
		assertFalse(rim1.equals(rim4));
		assertFalse(rim1.hashCode() == rim4.hashCode());
	}

}
