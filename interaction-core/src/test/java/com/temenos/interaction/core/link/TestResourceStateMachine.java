package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestResourceStateMachine {

	@Test
	public void testStates() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState unauthorised = new ResourceState("INAU", "unauthorised/{id}");
		ResourceState authorised = new ResourceState("LIVE", "authorised/{id}");
		ResourceState reversed = new ResourceState("RNAU", "reversed/{id}");
		ResourceState history = new ResourceState("REVE", "history/{id}");
		ResourceState end = new ResourceState("end", "");
	
		begin.addTransition(new TransitionCommandSpec("PUT", "unauthorised/{id}"), unauthorised);
		
		unauthorised.addTransition(new TransitionCommandSpec("PUT", "unauthorised/{id}"), unauthorised);
		unauthorised.addTransition(new TransitionCommandSpec("PUT", "authorised/{id}"), authorised);
		unauthorised.addTransition(new TransitionCommandSpec("DELETE", "unauthorised/{id}"), end);
		
		authorised.addTransition(new TransitionCommandSpec("PUT", "history/{id}"), history);
		
		history.addTransition(new TransitionCommandSpec("PUT", "reversed/{id}"), reversed);
		
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
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
	
}
