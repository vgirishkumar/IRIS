package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.*;

import org.junit.Test;

import com.temenos.interaction.core.hypermedia.TransitionCommandSpec;

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
	public void testResetRequired() {
		TransitionCommandSpec cs = new TransitionCommandSpec("GET", "", Transition.RESET_CONTENT);
		assertTrue(cs.isResetRequired());
	}

	@Test
	public void testResetForEach() {
		TransitionCommandSpec cs = new TransitionCommandSpec("GET", "", Transition.RESET_CONTENT | Transition.FOR_EACH);
		assertTrue(cs.isResetRequired());
		assertTrue(cs.isForEach());
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
		TransitionCommandSpec tcs3 = new TransitionCommandSpec("GET", "/test", Transition.RESET_CONTENT);
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
		assertEquals("GET (0)", new TransitionCommandSpec("GET", "", 0).toString());
		assertEquals("GET /test (11)", new TransitionCommandSpec("GET", "/test", Transition.FOR_EACH | Transition.RESET_CONTENT).toString());
		assertEquals("GET (1)", new TransitionCommandSpec("GET", null, Transition.FOR_EACH).toString());
		assertEquals("GET (10)", new TransitionCommandSpec("GET", null, Transition.RESET_CONTENT).toString());
	}

}
