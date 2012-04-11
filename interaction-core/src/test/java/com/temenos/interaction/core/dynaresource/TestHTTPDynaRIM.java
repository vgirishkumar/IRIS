package com.temenos.interaction.core.dynaresource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.odata4j.edm.EdmDataServices;

import com.jayway.jaxrs.hateoas.HateoasContext;
import com.jayway.jaxrs.hateoas.LinkableInfo;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;
import com.temenos.interaction.core.state.ResourceInteractionModel;


public class TestHTTPDynaRIM {

	@Test
	public void testRIMsCRUD() {
		String ENTITY_NAME = "NOTE";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists");
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted");

		// create
		initial.addTransition("PUT", exists);
		// update
		exists.addTransition("PUT", exists);
		// delete
		exists.addTransition("DELETE", deleted);
		
		ResourceRegistry rr = mock(ResourceRegistry.class);
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(initial), "/notes/{id}", initial, rr, cc);
		verify(cc).fetchGetCommand("/notes/{id}");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(0, resources.size());
		verify(cc, times(1)).fetchGetCommand("/notes/{id}");
		verify(cc, times(1)).fetchStateTransitionCommand("PUT", "/notes/{id}");
		verify(cc, times(1)).fetchStateTransitionCommand("DELETE", "/notes/{id}");
	}

	@Test
	public void testRIMsSubstate() {
		String ENTITY_NAME = "DraftNote";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists");
		ResourceState draft = new ResourceState(ENTITY_NAME, "draft", "/draft");
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted");
	
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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, stateMachine, "/notes/{id}", initial, rr, cc);
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
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial");
		ResourceState published = new ResourceState(ENTITY_NAME, "published", "/published");
		ResourceState draft = new ResourceState(ENTITY_NAME, "draft", "/draft");
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted");
	
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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, stateMachine, "/notes/{id}", initial, rr, cc);
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

		// the booking
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin");
  		ResourceState bookingCreated = new ResourceState(ENTITY_NAME, "bookingCreated");
  		ResourceState bookingCancellation = new ResourceState(ENTITY_NAME, "cancellation", "/cancellation");
  		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted");

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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(begin), "/bookings/{id}", begin, rr, cc);
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
	 * state.  Test we return the hateoas context correctly for our test resource.
	 */
	@Test
	public void testLinksSelf() {
		String ENTITY_NAME = "NOTE";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial");
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPDynaRIM>());
		CommandController cc = mock(CommandController.class);
		
		// create the dynamic resource, this also registers itself with the ResourceRegistry
		HTTPDynaRIM resource = new HTTPDynaRIM(null, new ResourceStateMachine(initial), "/notes/{id}", initial, rr, cc);
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
		ResourceState serviceRoot = new ResourceState(ENTITY_NAME, "home");

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
		HTTPDynaRIM resource = new HTTPDynaRIM(null, new ResourceStateMachine(serviceRoot), "", serviceRoot, rr, cc);
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
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin");
		String OTHER_ENTITY_NAME = "DIFFERENT";
		ResourceState begin2 = new ResourceState(OTHER_ENTITY_NAME, "begin2");
		CommandController cc = mock(CommandController.class);
		CommandController cc2 = mock(CommandController.class);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(null, new ResourceStateMachine(begin), "/notes", begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(null, new ResourceStateMachine(begin2), "/notes", begin2, null, cc2);
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testEqualityParent() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin");
		String PARENT_ENTITY_NAME = "PARENT";
		ResourceState parentBegin = new ResourceState(PARENT_ENTITY_NAME, "begin");
		String DIFFERENT_ENTITY_NAME = "DIFFERENT";
		ResourceState differentBegin = new ResourceState(DIFFERENT_ENTITY_NAME, "begin");
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(parentBegin), "/root", parentBegin, null, cc);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(parent, new ResourceStateMachine(begin), "/notes", begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(parent, new ResourceStateMachine(differentBegin), "/notes", differentBegin, null, cc);
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testInequality() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin");
		String PARENT_ENTITY_NAME = "PARENT";
		ResourceState parentBegin = new ResourceState(PARENT_ENTITY_NAME, "begin");
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(parentBegin), "/notes", parentBegin, null, cc);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(parent, new ResourceStateMachine(begin), "/{id}", begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(parent, new ResourceStateMachine(begin), "/{id}/different", begin, null, cc);
		HTTPDynaRIM rim3 = new HTTPDynaRIM(null, new ResourceStateMachine(begin), "/notes1", begin, null, cc);
		HTTPDynaRIM rim4 = new HTTPDynaRIM(null, new ResourceStateMachine(begin), "/notes", begin, null, cc);

		// both with parent
		assertFalse(rim1.equals(rim2));
		assertFalse(rim1.hashCode() == rim2.hashCode());

		// both without parent
		assertFalse(parent.equals(rim3));
		assertFalse(parent.hashCode() == rim3.hashCode());

		// one with parent
		assertFalse(rim1.equals(rim4));
		assertFalse(rim1.hashCode() == rim4.hashCode());
	}

}
