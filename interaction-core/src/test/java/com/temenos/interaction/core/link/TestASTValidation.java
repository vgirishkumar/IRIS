package com.temenos.interaction.core.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestASTValidation {

	@Test
	public void testValidateStatesValid() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "");
	
		begin.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);		
		exists.addTransition(new TransitionCommandSpec("DELETE", "{id}"), end);
		
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		ASTValidation v = new ASTValidation();
		assertTrue(v.validate(states, sm));	
	}

	@Test
	public void testValidateStatesUnreachable() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "");
	
		begin.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);		
		exists.addTransition(new TransitionCommandSpec("DELETE", "{id}"), end);
		
		ResourceState unreachableState = new ResourceState("unreachable", "");
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(unreachableState);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		ASTValidation v = new ASTValidation();
		assertFalse(v.validate(states, sm));	
	}

	@Test
	public void testDOT() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "");
	
		begin.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);		
		exists.addTransition(new TransitionCommandSpec("DELETE", "{id}"), end);
				
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		ASTValidation v = new ASTValidation();
		assertEquals("digraph G {\n    begin->exists[style=bold,label=PUT {id}]\n    exists->end[style=bold,label=DELETE {id}]\n}", v.graph(sm));	
	}

}
