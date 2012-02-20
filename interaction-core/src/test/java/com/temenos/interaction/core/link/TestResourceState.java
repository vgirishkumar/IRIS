package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestResourceState {

	@Test
	public void testCollection() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState end = new ResourceState("end", "");
		
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(end);

		Set<ResourceState> states2 = new HashSet<ResourceState>();
		states2.add(begin);
		states2.add(exists);
		states2.add(end);
		
		states.removeAll(states2);
		assertEquals(0, states.size());
	}

	@Test
	public void testSelfState() {
		ResourceState initial = new ResourceState("initial");
		ResourceState archived = new ResourceState("archived", "/archived");
		assertTrue(initial.isSelfState());
		assertFalse(archived.isSelfState());
	}
	
	@Test
	public void testGetCommand() {
		ResourceState begin = new ResourceState("begin", "{id}");
		ResourceState exists = new ResourceState("exists", "{id}");
		begin.addTransition("PUT", exists);
		assertEquals("PUT", begin.getTransition(exists).getCommand().getMethod());
		assertEquals("{id}", begin.getTransition(exists).getCommand().getPath());
	}

	@Test
	public void testEquality() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState begin2 = new ResourceState("begin", "");
		assertEquals(begin, begin2);
		assertEquals(begin.hashCode(), begin2.hashCode());
	}

	@Test
	public void testInequality() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState end = new ResourceState("end", "");
		assertFalse(begin.equals(end));
		assertFalse(begin.hashCode() == end.hashCode());
	}
	
	@Test
	public void testEqualityNull() {
		ResourceState begin = new ResourceState("begin", null);
		ResourceState end = new ResourceState("begin", null);
		assertEquals(begin, end);
		assertEquals(end, begin);
		assertEquals(begin.hashCode(), end.hashCode());
	}

	@Test
	public void testInequalityNull() {
		ResourceState begin = new ResourceState("begin", null);
		ResourceState end = new ResourceState("end", "/test");
		assertFalse(begin.equals(end));
		assertFalse(begin.hashCode() == end.hashCode());
	}

	@Test
	public void testInequalityBothNull() {
		ResourceState begin = new ResourceState("begin", null);
		ResourceState end = new ResourceState("end", null);
		assertFalse(begin.equals(end));
		assertFalse(begin.hashCode() == end.hashCode());
	}

	@Test
	public void testEndState() {
		ResourceState begin = new ResourceState("begin", "");
		ResourceState end = new ResourceState("end", "");
		begin.addTransition("DELETE", end);
		assertFalse(begin.isFinalState());
		assertTrue(end.isFinalState());
	}

}
