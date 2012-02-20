package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class TestResourceStateMachine {

	@Test
	public void testStates() {
		ResourceState begin = new ResourceState("begin", "{id}");
		ResourceState unauthorised = new ResourceState("INAU", "unauthorised/{id}");
		ResourceState authorised = new ResourceState("LIVE", "authorised/{id}");
		ResourceState reversed = new ResourceState("RNAU", "reversed/{id}");
		ResourceState history = new ResourceState("REVE", "history/{id}");
		ResourceState end = new ResourceState("end", "{id}");
	
		begin.addTransition("PUT", unauthorised);
		
		unauthorised.addTransition("PUT", unauthorised);
		unauthorised.addTransition("PUT", authorised);
		unauthorised.addTransition("PUT", end);
		
		authorised.addTransition("PUT", history);
		
		history.addTransition("PUT", reversed);
		
		
		ResourceStateMachine sm = new ResourceStateMachine("", begin);
		assertEquals(6, sm.getStates().size());
		
		Set<ResourceState> testStates = new HashSet<ResourceState>();
		testStates.add(begin);
		testStates.add(unauthorised);
		testStates.add(authorised);
		testStates.add(reversed);
		testStates.add(history);
		testStates.add(end);
		for (ResourceState s : testStates) {
			assertTrue(sm.getStates().contains(s));
		}
		
		System.out.println(new ASTValidation().graph(sm));
	}
	
	@Test
	public void testGetStates() {
		ResourceState begin = new ResourceState("begin", "{id}");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "{id}");
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine("", begin);
		Collection<ResourceState> states = sm.getStates();
		assertEquals("Number of states", 3, states.size());
		assertTrue(states.contains(begin));
		assertTrue(states.contains(exists));
		assertTrue(states.contains(end));
	}

	@Test
	public void testInteractionMap() {
		ResourceState begin = new ResourceState("begin", "{id}");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "{id}");
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine("", begin);

		Map<String, Set<String>> interactionMap = sm.getInteractionMap();
		assertEquals("Number of resources", 1, interactionMap.size());
		Set<String> entrySet = interactionMap.keySet();
		assertTrue(entrySet.contains("{id}"));
		Collection<String> interactions = interactionMap.get("{id}");
		assertEquals("Number of interactions", 2, interactions.size());
		assertTrue(interactions.contains("PUT"));
		assertTrue(interactions.contains("DELETE"));
	}

	@Test
	public void testInteractions() {
		ResourceState begin = new ResourceState("begin", "{id}");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "{id}");
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine("", begin);

		Set<String> interactions = sm.getInteractions(begin);
		assertEquals("Number of interactions", 2, interactions.size());
		assertTrue(interactions.contains("PUT"));
		assertTrue(interactions.contains("DELETE"));
	}

	@Test
	public void testSubstateInteractions() {
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
		
		ResourceStateMachine sm = new ResourceStateMachine("", initial);

		Set<String> initialInteractions = sm.getInteractions(initial);
		assertNull("Number of interactions", initialInteractions);

		Set<String> draftInteractions = sm.getInteractions(draft);
		assertEquals("Number of interactions", 2, draftInteractions.size());
		assertTrue(draftInteractions.contains("PUT"));
		assertTrue(draftInteractions.contains("DELETE"));

		Set<String> publishInteractions = sm.getInteractions(published);
		assertEquals("Number of interactions", 2, publishInteractions.size());
		assertTrue(publishInteractions.contains("PUT"));
		assertTrue(publishInteractions.contains("DELETE"));

		Set<String> deletedInteractions = sm.getInteractions(deleted);
		assertNull("Number of interactions", deletedInteractions);

	}

	@Test
	public void testStateMap() {
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
		
		ResourceStateMachine sm = new ResourceStateMachine("", initial);

		Map<String, ResourceState> stateMap = sm.getStateMap();
		assertEquals("Number of states", 2, stateMap.size());
		Set<String> entrySet = stateMap.keySet();
		assertTrue(entrySet.contains("/published"));
		assertTrue(entrySet.contains("/draft"));
		assertEquals(published, stateMap.get("/published"));
		assertEquals(draft, stateMap.get("/draft"));
	}

	@Test
	public void testGetState() {
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
		
		ResourceStateMachine sm = new ResourceStateMachine("", initial);

		assertEquals(initial, sm.getState(null));
		assertEquals(published, sm.getState("/published"));
		assertEquals(draft, sm.getState("/draft"));
	}

	@Test(expected=AssertionError.class)
	public void testInteractionsInvalidState() {
		ResourceState begin = new ResourceState("begin", "{id}");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "{id}");
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine("", begin);

		ResourceState other = new ResourceState("other", "/other");
		sm.getInteractions(other);
	}

}
