package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestResourceStateMachine {

	@Test
	public void testStates() {
		ResourceState begin = new ResourceState("begin");
		ResourceState unauthorised = new ResourceState("INAU");
		ResourceState authorised = new ResourceState("LIVE");
		ResourceState reversed = new ResourceState("RNAU");
		ResourceState history = new ResourceState("REVE");
		ResourceState end = new ResourceState("end");
	
		begin.addTransition(new CommandSpec("INPUT", "PUT"), unauthorised);
		
		unauthorised.addTransition(new CommandSpec("INPUT", "PUT"), unauthorised);
		unauthorised.addTransition(new CommandSpec("AUTHORISE", "PUT"), authorised);
		unauthorised.addTransition(new CommandSpec("DELETE", "PUT"), end);
		
		authorised.addTransition(new CommandSpec("REVERSE", "PUT"), reversed);
		
		reversed.addTransition(new CommandSpec("AUTHORISE", "PUT"), history);
		
		
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
