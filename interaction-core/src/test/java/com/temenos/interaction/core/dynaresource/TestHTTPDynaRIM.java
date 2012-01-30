package com.temenos.interaction.core.dynaresource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.TransitionCommandSpec;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.state.ResourceInteractionModel;


public class TestHTTPDynaRIM {

	@Test
	public void testRIMsCRUD() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "");
	
		begin.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);
		exists.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);
		exists.addTransition(new TransitionCommandSpec("DELETE", "{id}"), end);
		
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, "NOTE", "/notes", begin, new HashSet<String>(), null, cc);
		verify(cc).fetchGetCommand("/notes");
		Collection<ResourceInteractionModel> resources = parent.createChildResources();
		assertEquals(1, resources.size());
		verify(cc, times(1)).fetchGetCommand("/notes");
		verify(cc, times(1)).fetchGetCommand("/notes/{id}");
		verify(cc).fetchStateTransitionCommand("PUT", "/notes/{id}");
		verify(cc).fetchStateTransitionCommand("DELETE", "/notes/{id}");
	}

	@Test
	public void testGetStates() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "");
	
		begin.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);
		exists.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);
		exists.addTransition(new TransitionCommandSpec("DELETE", "{id}"), end);
		
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, "NOTE", "/notes", begin, new HashSet<String>(), null, cc);
		Collection<ResourceState> states = parent.getStates();
		assertEquals("Number of states", 3, states.size());
		assertTrue(states.contains(begin));
		assertTrue(states.contains(exists));
		assertTrue(states.contains(end));
	}

	@Test
	public void testInteractionMap() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "");
	
		begin.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);
		exists.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);
		exists.addTransition(new TransitionCommandSpec("DELETE", "{id}"), end);
		
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, "NOTE", "/notes", begin, new HashSet<String>(), null, cc);

		Map<String, Set<String>> interactionMap = parent.getInteractionMap();
		assertEquals("Number of resources", 1, interactionMap.size());
		Set<String> entrySet = interactionMap.keySet();
		assertTrue(entrySet.contains("{id}"));
		Collection<String> interactions = interactionMap.get("{id}");
		assertEquals("Number of interactions", 2, interactions.size());
		assertTrue(interactions.contains("PUT"));
		assertTrue(interactions.contains("DELETE"));
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
