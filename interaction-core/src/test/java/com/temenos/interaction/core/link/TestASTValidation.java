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
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "{id}");
	
		begin.addTransition("PUT", exists);		
		exists.addTransition("DELETE", end);
		
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(ENTITY_NAME, begin);
		ASTValidation v = new ASTValidation();
		assertTrue(v.validate(states, sm));	
	}

	@Test
	public void testValidateStatesUnreachable() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "{id}");
	
		begin.addTransition("PUT", exists);		
		exists.addTransition("DELETE", end);
		
		ResourceState unreachableState = new ResourceState(ENTITY_NAME, "unreachable", "");
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(unreachableState);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(ENTITY_NAME, begin);
		ASTValidation v = new ASTValidation();
		assertFalse(v.validate(states, sm));	
	}

	@Test
	public void testDOT() {
		String expected = "digraph G {\n    initial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    initial->exists[label=\"PUT {id}\"]\n    exists->deleted[label=\"DELETE {id}\"]\n    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n    deleted->final[label=\"\"]\n}";
		
		String ENTITY_NAME = "G";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted", "{id}");
	
		initial.addTransition("PUT", exists);		
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine(ENTITY_NAME, initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTMultipleFinalStates() {
		String expected = "digraph CRUD_ENTITY {\n    initial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    initial->exists[label=\"PUT\"]\n    exists->deleted[label=\"DELETE\"]\n    exists->archived[label=\"PUT /archived\"]\n"
			+ "    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    deleted->final[label=\"\"]\n"
			+ "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    archived->final1[label=\"\"]\n}";
		
		String ENTITY_NAME = "CRUD_ENTITY";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists");
		ResourceState archived = new ResourceState(ENTITY_NAME, "archived", "/archived");
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted");
	
		initial.addTransition("PUT", exists);		
		exists.addTransition("PUT", archived);
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine(ENTITY_NAME, initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		assertEquals(expected, result);	
	}

}
