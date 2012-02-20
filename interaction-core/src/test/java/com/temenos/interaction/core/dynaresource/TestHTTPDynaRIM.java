package com.temenos.interaction.core.dynaresource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.Test;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;
import com.temenos.interaction.core.state.ResourceInteractionModel;


public class TestHTTPDynaRIM {

	@Test
	public void testRIMsCRUD() {
		ResourceState initial = new ResourceState("initial");
		ResourceState exists = new ResourceState("exists");
		ResourceState deleted = new ResourceState("deleted");

		// create
		initial.addTransition("PUT", exists);
		// update
		exists.addTransition("PUT", exists);
		// delete
		exists.addTransition("DELETE", deleted);
		
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine("NOTE", initial), "/notes/{id}", initial, null, cc);
		verify(cc).fetchGetCommand("/notes/{id}");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(0, resources.size());
		verify(cc, times(1)).fetchGetCommand("/notes/{id}");
		verify(cc, times(1)).fetchStateTransitionCommand("PUT", "/notes/{id}");
		verify(cc, times(1)).fetchStateTransitionCommand("DELETE", "/notes/{id}");
	}

	@Test
	public void testRIMsSubstate() {
  		ResourceState initial = new ResourceState("initial");
		ResourceState exists = new ResourceState("exists");
		ResourceState draft = new ResourceState("draft", "/draft");
		ResourceState deleted = new ResourceState("deleted");
	
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
		
		CommandController cc = mock(CommandController.class);
		ResourceStateMachine stateMachine = new ResourceStateMachine("DraftNote", initial);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, stateMachine, "/notes/{id}", initial, null, cc);
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
  		ResourceState initial = new ResourceState("initial");
		ResourceState published = new ResourceState("published", "/published");
		ResourceState draft = new ResourceState("draft", "/draft");
		ResourceState deleted = new ResourceState("deleted");
	
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
		
		CommandController cc = mock(CommandController.class);
		ResourceStateMachine stateMachine = new ResourceStateMachine("PublishNote", initial);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, stateMachine, "/notes/{id}", initial, null, cc);
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

		// the booking
		ResourceState begin = new ResourceState("begin");
  		ResourceState bookingCreated = new ResourceState("bookingCreated");
  		ResourceState bookingCancellation = new ResourceState("cancellation", "/cancellation");
  		ResourceState deleted = new ResourceState("deleted");

		begin.addTransition("PUT", bookingCreated);
		bookingCreated.addTransition("PUT", bookingCancellation);
		bookingCancellation.addTransition("DELETE", deleted);

		// the payment
		ResourceState payment = new ResourceState("payment", "/payment");
		ResourceState confirmation = new ResourceState("pconfirmation", "/payment/pconfirmation");
		ResourceState waitingForConfirmation = new ResourceState("pwaiting", "/payment/pwaiting");

		payment.addTransition("PUT", waitingForConfirmation);
		payment.addTransition("PUT", confirmation);
		waitingForConfirmation.addTransition("PUT", confirmation);
		
		// linking the two state machines together
		bookingCreated.addTransition("PUT", payment);  // TODO needs to be conditional
		confirmation.addTransition("PUT", bookingCancellation);
		
		
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine("BOOKING", begin), "/bookings/{id}", begin, null, cc);
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

	@Test
	public void testEquality() {
		ResourceState begin = new ResourceState("begin");
		ResourceState begin2 = new ResourceState("begin2");
		CommandController cc = mock(CommandController.class);
		CommandController cc2 = mock(CommandController.class);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(null, new ResourceStateMachine("NOTE", begin), "/notes", null, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(null, new ResourceStateMachine("DIFFERENT", begin2), "/notes", null, null, cc2);
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testEqualityParent() {
		ResourceState begin = new ResourceState("begin");
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine("PARENT", begin), "/root", null, null, cc);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(parent, new ResourceStateMachine("NOTE", begin), "/notes", null, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(parent, new ResourceStateMachine("DIFFERENT", begin), "/notes", null, null, cc);
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testInequality() {
		ResourceState begin = new ResourceState("begin");
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine("PARENT", begin), "/notes", null, null, cc);
		HTTPDynaRIM rim1 = new HTTPDynaRIM(parent, new ResourceStateMachine("NOTE", begin), "/{id}", null, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(parent, new ResourceStateMachine("NOTE", begin), "/{id}/different", null, null, cc);
		HTTPDynaRIM rim3 = new HTTPDynaRIM(null, new ResourceStateMachine("NOTE", begin), "/notes1", null, null, cc);
		HTTPDynaRIM rim4 = new HTTPDynaRIM(null, new ResourceStateMachine("NOTE", begin), "/notes", null, null, cc);

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
