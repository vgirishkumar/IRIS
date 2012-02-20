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
		ResourceState begin = new ResourceState("begin", "{id}");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "{id}");
	
		begin.addTransition("PUT", exists);		
		exists.addTransition("DELETE", end);
		
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine("", begin);
		ASTValidation v = new ASTValidation();
		assertTrue(v.validate(states, sm));	
	}

	@Test
	public void testValidateStatesUnreachable() {
		ResourceState begin = new ResourceState("begin", "{id}");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "{id}");
	
		begin.addTransition("PUT", exists);		
		exists.addTransition("DELETE", end);
		
		ResourceState unreachableState = new ResourceState("unreachable", "");
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(unreachableState);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine("", begin);
		ASTValidation v = new ASTValidation();
		assertFalse(v.validate(states, sm));	
	}

	@Test
	public void testDOT() {
		String expected = "digraph G {\n    initial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    initial->exists[label=\"PUT {id}\"]\n    exists->deleted[label=\"DELETE {id}\"]\n    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n    deleted->final[label=\"\"]\n}";
		
		ResourceState initial = new ResourceState("initial", "{id}");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState deleted = new ResourceState("deleted", "{id}");
	
		initial.addTransition("PUT", exists);		
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine("", initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTMultipleFinalStates() {
		String expected = "digraph G {\n    initial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    initial->exists[label=\"PUT\"]\n    exists->deleted[label=\"DELETE\"]\n    exists->archived[label=\"PUT /archived\"]\n"
			+ "    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    deleted->final[label=\"\"]\n"
			+ "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    archived->final1[label=\"\"]\n}";
		
		ResourceState initial = new ResourceState("initial");
		ResourceState exists = new ResourceState("exists");
		ResourceState archived = new ResourceState("archived", "/archived");
		ResourceState deleted = new ResourceState("deleted");
	
		initial.addTransition("PUT", exists);		
		exists.addTransition("PUT", archived);
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine("", initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		assertEquals(expected, result);	
	}

}
