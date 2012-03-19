package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestResourceState {

	@Test
	public void testCollection() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "");
		
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
		String ENTITY_NAME = "entity";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial");
		ResourceState archived = new ResourceState(ENTITY_NAME, "archived", "/archived");
		assertTrue(initial.isSelfState());
		assertFalse(archived.isSelfState());
	}
	
	@Test
	public void testGetCommand() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		begin.addTransition("PUT", exists);
		assertEquals("PUT", begin.getTransition(exists).getCommand().getMethod());
		assertEquals("{id}", begin.getTransition(exists).getCommand().getPath());
	}

	@Test
	public void testEquality() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "");
		ResourceState begin2 = new ResourceState(ENTITY_NAME, "begin", "");
		assertEquals(begin, begin2);
		assertEquals(begin.hashCode(), begin2.hashCode());
	}

	@Test
	public void testInequality() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "");
		assertFalse(begin.equals(end));
		assertFalse(begin.hashCode() == end.hashCode());
	}
	
	@Test
	public void testEqualityNull() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", null);
		ResourceState end = new ResourceState(ENTITY_NAME, "begin", null);
		assertEquals(begin, end);
		assertEquals(end, begin);
		assertEquals(begin.hashCode(), end.hashCode());
	}

	@Test
	public void testInequalityNull() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", null);
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "/test");
		assertFalse(begin.equals(end));
		assertFalse(begin.hashCode() == end.hashCode());
	}

	@Test
	public void testInequalityBothNull() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", null);
		ResourceState end = new ResourceState(ENTITY_NAME, "end", null);
		assertFalse(begin.equals(end));
		assertFalse(begin.hashCode() == end.hashCode());
	}

	@Test
	public void testEndState() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "");
		begin.addTransition("DELETE", end);
		assertFalse(begin.isFinalState());
		assertTrue(end.isFinalState());
	}

}
