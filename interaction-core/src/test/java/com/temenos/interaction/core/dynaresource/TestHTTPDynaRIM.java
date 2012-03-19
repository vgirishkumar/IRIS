package com.temenos.interaction.core.dynaresource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.Test;

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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(ENTITY_NAME, initial), "/notes/{id}", initial, rr, cc);
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
		ResourceStateMachine stateMachine = new ResourceStateMachine(ENTITY_NAME, initial);
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
		ResourceStateMachine stateMachine = new ResourceStateMachine(ENTITY_NAME, initial);
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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(ENTITY_NAME, begin), "/bookings/{id}", begin, rr, cc);
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
		ResourceRegistry rr = new ResourceRegistry();
		CommandController cc = mock(CommandController.class);
		
		// create the dynamic resource, this also registers itself with the ResourceRegistry
		HTTPDynaRIM resource = new HTTPDynaRIM(null, new ResourceStateMachine(ENTITY_NAME, initial), "/notes/{id}", initial, rr, cc);
		HateoasContext context = resource.getHateoasContext();
		
		// every dynamic resource should have the information to link to itself
        LinkableInfo result = context.getLinkableInfo(ENTITY_NAME);
        assertNotNull(result);
        assertEquals("NOTE", result.getId());
        assertEquals("GET", result.getHttpMethod());
        assertEquals("/notes/{id}", result.getMethodPath());
        assertEquals("lookup label from EDMX", result.getLabel());
        assertEquals("lookup description from EDMX", result.getDescription());
//        assertEquals(DummyDto.class, result.getTemplateClass());
	}
	
	@Test
	public void testEquality() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin");
		String OTHER_ENTITY_NAME = "DIFFERENT";
		ResourceState begin2 = new ResourceState(OTHER_ENTITY_NAME, "begin2");
		CommandController cc = mock(CommandController.class);
		CommandController cc2 = mock(CommandController.class);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(null, new ResourceStateMachine(ENTITY_NAME, begin), "/notes", null, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(null, new ResourceStateMachine(OTHER_ENTITY_NAME, begin2), "/notes", null, null, cc2);
		
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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(PARENT_ENTITY_NAME, parentBegin), "/root", null, null, cc);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(parent, new ResourceStateMachine(ENTITY_NAME, begin), "/notes", null, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(parent, new ResourceStateMachine(DIFFERENT_ENTITY_NAME, differentBegin), "/notes", null, null, cc);
		
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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine(PARENT_ENTITY_NAME, parentBegin), "/notes", null, null, cc);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(parent, new ResourceStateMachine(ENTITY_NAME, begin), "/{id}", null, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(parent, new ResourceStateMachine(ENTITY_NAME, begin), "/{id}/different", null, null, cc);
		HTTPDynaRIM rim3 = new HTTPDynaRIM(null, new ResourceStateMachine(ENTITY_NAME, begin), "/notes1", null, null, cc);
		HTTPDynaRIM rim4 = new HTTPDynaRIM(null, new ResourceStateMachine(ENTITY_NAME, begin), "/notes", null, null, cc);

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
