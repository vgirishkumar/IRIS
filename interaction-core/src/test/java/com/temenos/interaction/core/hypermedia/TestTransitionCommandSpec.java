package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTransitionCommandSpec {

	@Test
	public void testTransitionCommandSpec() {
		new TransitionCommandSpec("GET", "", 0);
	}

	@Test
	public void testForEach() {
		TransitionCommandSpec cs = new TransitionCommandSpec("GET", "", Transition.FOR_EACH);
		assertTrue(cs.isForEach());
	}

	@Test
	public void testAutoForEach() {
		TransitionCommandSpec cs = new TransitionCommandSpec("GET", "", Transition.AUTO | Transition.FOR_EACH);
		assertTrue(cs.isAutoTransition());
		assertTrue(cs.isForEach());
	}

	@Test
	public void testAuto() {
		TransitionCommandSpec cs = new TransitionCommandSpec(null, "/path", Transition.AUTO);
		assertTrue(cs.isAutoTransition());
	}

	@Test
	public void testEquality() {
		TransitionCommandSpec tcs = new TransitionCommandSpec("GET", "/test", 0);
		TransitionCommandSpec tcs2 = new TransitionCommandSpec("GET", "/test", 0);
		assertEquals(tcs, tcs2);
		assertEquals(tcs.hashCode(), tcs2.hashCode());
	}

	@Test
	public void testInequality() {
		TransitionCommandSpec tcs = new TransitionCommandSpec("GET", "/test", 0);
		TransitionCommandSpec tcs2 = new TransitionCommandSpec("PUT", "/test", 0);
		TransitionCommandSpec tcs3 = new TransitionCommandSpec("GET", "/test", Transition.FOR_EACH);
		TransitionCommandSpec tcs4 = new TransitionCommandSpec("GET", "/test2", 0);
		assertFalse(tcs.equals(tcs2));
		assertFalse(tcs.hashCode() == tcs2.hashCode());
		assertFalse(tcs.equals(tcs3));
		assertFalse(tcs.hashCode() == tcs3.hashCode());
		assertFalse(tcs.equals(tcs4));
		assertFalse(tcs.hashCode() == tcs4.hashCode());
	}

	@Test
	public void testEqualityNull() {
		TransitionCommandSpec tcs = new TransitionCommandSpec(null, null, 0);
		TransitionCommandSpec tcs2 = new TransitionCommandSpec(null, null, 0);
		assertEquals(tcs, tcs2);
		assertEquals(tcs2, tcs);
		assertEquals(tcs.hashCode(), tcs2.hashCode());
	}

	@Test
	public void testInequalityNull() {
		TransitionCommandSpec tcs = new TransitionCommandSpec(null, null, 0);
		TransitionCommandSpec tcs2 = new TransitionCommandSpec("PUT", "", 0);
		TransitionCommandSpec tcs3 = new TransitionCommandSpec("GET", null, 0);
		TransitionCommandSpec tcs4 = new TransitionCommandSpec(null, "", 0);
		assertFalse(tcs.equals(tcs2));
		assertFalse(tcs.hashCode() == tcs2.hashCode());
		assertFalse(tcs.equals(tcs3));
		assertFalse(tcs.hashCode() == tcs3.hashCode());
		assertFalse(tcs.equals(tcs4));
		assertTrue(tcs.hashCode() == tcs4.hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("GET", new TransitionCommandSpec("GET", "", 0).toString());
		assertEquals("*GET", new TransitionCommandSpec("GET", null, Transition.FOR_EACH).toString());
		assertEquals("GET", new TransitionCommandSpec("GET", null, Transition.AUTO).toString());
	}

}
