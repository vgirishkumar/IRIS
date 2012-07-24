package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.*;

import org.junit.Test;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.hypermedia.TransitionCommandSpec;

public class TestTransition {

	@Test
	public void testEquality() {
		ResourceState begin = new ResourceState("begin", "", "/");
		ResourceState begin2 = new ResourceState("begin", "", "/");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), begin2);
		Transition t2 = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), begin2);
		assertEquals(t, t2);
		assertEquals(t.hashCode(), t2.hashCode());
	}
	
	@Test
	public void testEqualityNullSource() {
		ResourceState begin2 = new ResourceState("begin", "", "/");
		Transition t = new Transition(null, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), begin2);
		Transition t2 = new Transition(null, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), begin2);
		assertEquals(t, t2);
		assertEquals(t.hashCode(), t2.hashCode());
	}

	@Test 
	public void testInequality() {
		ResourceState begin = new ResourceState("entity", "begin", "");
		ResourceState exists = new ResourceState("entity", "exists", "{id}");
		ResourceState end = new ResourceState("entity", "end", "");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), end);
		Transition t2 = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), exists);
		assertFalse(t.equals(t2));
		assertFalse(t.hashCode() == t2.hashCode());
		
		Transition t3 = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), end);
		Transition t4 = new Transition(begin, new TransitionCommandSpec(null, "stuffed", Transition.AUTO), end);
		assertFalse(t3.equals(t4));
		assertFalse(t3.hashCode() == t4.hashCode());

	}
	
	@Test
	public void testGetId() {
		ResourceState begin = new ResourceState("entity", "begin", "{id}");
		ResourceState end = new ResourceState("entity", "end", "{id}");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", 0), end);
		assertEquals("entity.begin>entity.end", t.getId());
	}

	@Test
	public void testToString() {
		ResourceState begin = new ResourceState("entity", "begin", "/begin");
		ResourceState end = new ResourceState("entity", "end", "/end");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", 0), end);
		assertEquals("entity.begin>entity.end", t.toString());
	}

}
