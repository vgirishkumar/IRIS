package com.temenos.interaction.core.dynaresource;

import static org.junit.Assert.assertEquals;
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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine("NOTE", initial), "/notes/{id}", null, cc);
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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, stateMachine, "/notes/{id}", null, cc);
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

//	@Test
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
		HTTPDynaRIM parent = new HTTPDynaRIM(null, stateMachine, "/notes/{id}", null, cc);
		verify(cc).fetchGetCommand("/notes/{id}");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(3, resources.size());
		verify(cc, times(1)).fetchGetCommand("/notes/{id}");
		verify(cc, times(1)).fetchGetCommand("/notes/{id}/draft");
		verify(cc).fetchStateTransitionCommand("PUT", "/notes/{id}/draft");
		verify(cc).fetchStateTransitionCommand("DELETE", "/notes/{id}/draft");
		verify(cc, times(1)).fetchGetCommand("/notes/{id}/published");
		verify(cc).fetchStateTransitionCommand("DELETE", "/notes/{id}/published");
		verify(cc).fetchStateTransitionCommand("PUT", "/notes/{id}/published");
		verify(cc).fetchStateTransitionCommand("DELETE", "/notes/{id}");
	}

//	@Test
	public void testRIMsMultipleSubstates1() {

		// the booking
		ResourceState begin = new ResourceState("begin", "/{id}");
  		ResourceState bookingCreated = new ResourceState("bookingCreated", "/{id}");
  		ResourceState bookingCancellation = new ResourceState("cancellation", "/{id}/cancellation");
  		ResourceState end = new ResourceState("end", "/{id}");

		begin.addTransition("PUT", bookingCreated);
		bookingCreated.addTransition("PUT", bookingCancellation);
		bookingCancellation.addTransition("DELETE", end);

		// the payment
		ResourceState payment = new ResourceState("payment", "/{id}/payment");
		ResourceState confirmation = new ResourceState("pconfirmation", "/{id}/payment/pconfirmation");
		ResourceState waitingForConfirmation = new ResourceState("pwaiting", "/{id}/payment/pwaiting");

		payment.addTransition("PUT", waitingForConfirmation);
		payment.addTransition("PUT", confirmation);
		waitingForConfirmation.addTransition("PUT", confirmation);
		
		// linking the two state machines together
		bookingCreated.addTransition("PUT", payment);  // TODO needs to be conditional
		confirmation.addTransition("PUT", bookingCancellation);
		
		
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, new ResourceStateMachine("BOOKING", begin), "/bookings", null, cc);
		verify(cc).fetchGetCommand("/bookings");
		Collection<ResourceInteractionModel> resources = parent.getChildren();
		assertEquals(5, resources.size());
		verify(cc, times(1)).fetchGetCommand("/bookings");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}/cancellation");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}/payment");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}/payment/pconfirmation");
		verify(cc, times(1)).fetchGetCommand("/bookings/{id}/payment/pwaiting");
		verify(cc).fetchStateTransitionCommand("PUT", "/bookings/{id}");
		verify(cc).fetchStateTransitionCommand("PUT", "/bookings/{id}/cancellation");
		verify(cc).fetchStateTransitionCommand("PUT", "/bookings/{id}/payment");
		verify(cc).fetchStateTransitionCommand("DELETE", "/bookings/{id}");
	}

	/*
	 * TODO implement if required
	@Test
	public void testEquality() {
		ResourceState begin = new ResourceState("begin");
		CommandController cc = new CommandController("");
		HTTPDynaRIM rim1 = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc);
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testInequality() {
		ResourceState begin = new ResourceState("begin");
		ResourceState begin2 = new ResourceState("begin2");
		CommandController cc = new CommandController("");
		CommandController cc2 = new CommandController("");
		HTTPDynaRIM rim1 = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(null, "NOTE1", "/notes", begin, null, cc);
		HTTPDynaRIM rim3 = new HTTPDynaRIM(null, "NOTE", "/notes1", begin, null, cc);
		HTTPDynaRIM rim4 = new HTTPDynaRIM(null, "NOTE", "/notes", begin2, null, cc);
		HTTPDynaRIM rim5 = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc2);

		assertFalse(rim1.equals(rim2));
		assertFalse(rim1.hashCode() == rim2.hashCode());

		assertFalse(rim1.equals(rim3));
		assertFalse(rim1.hashCode() == rim3.hashCode());

		assertFalse(rim1.equals(rim4));
		assertFalse(rim1.hashCode() == rim4.hashCode());

		assertFalse(rim1.equals(rim5));
		assertFalse(rim1.hashCode() == rim5.hashCode());
	}
*/
}
