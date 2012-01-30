package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTransition {

	@Test
	public void testEquality() {
		ResourceState begin = new ResourceState("begin");
		ResourceState begin2 = new ResourceState("begin");

		Transition t = new Transition(begin, new CommandSpec("stuff", "PUT"), begin2);
		Transition t2 = new Transition(begin, new CommandSpec("stuff", "PUT"), begin2);
		assertEquals(t, t2);
		assertEquals(t.hashCode(), t2.hashCode());
	}
	
	@Test 
	public void testInequality() {
		ResourceState begin = new ResourceState("begin");
		ResourceState exists = new ResourceState("exists");
		ResourceState end = new ResourceState("end");

		Transition t = new Transition(begin, new CommandSpec("stuff", "PUT"), end);
		Transition t2 = new Transition(begin, new CommandSpec("stuff", "PUT"), exists);
		assertFalse(t.equals(t2));
		assertFalse(t.hashCode() == t2.hashCode());
		
		Transition t3 = new Transition(begin, new CommandSpec("stuff", "PUT"), end);
		Transition t4 = new Transition(begin, new CommandSpec("stuffed", "PUT"), end);
		assertFalse(t3.equals(t4));
		assertFalse(t3.hashCode() == t4.hashCode());

	}
	
}
