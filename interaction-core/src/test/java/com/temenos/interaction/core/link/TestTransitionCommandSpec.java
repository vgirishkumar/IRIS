package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTransitionCommandSpec {

	@Test
	public void testTransitionCommandSpec() {
		new TransitionCommandSpec("GET", "");
	}

	@Test
	public void testEquality() {
		TransitionCommandSpec tcs = new TransitionCommandSpec("GET", "/test");
		TransitionCommandSpec tcs2 = new TransitionCommandSpec("GET", "/test");
		assertEquals(tcs, tcs2);
		assertEquals(tcs.hashCode(), tcs2.hashCode());
	}

	@Test
	public void testInequality() {
		TransitionCommandSpec tcs = new TransitionCommandSpec("GET", "/test");
		TransitionCommandSpec tcs2 = new TransitionCommandSpec("PUT", "/test");
		assertFalse(tcs.equals(tcs2));
		assertFalse(tcs.hashCode() == tcs2.hashCode());
	}

	@Test
	public void testEqualityNull() {
		TransitionCommandSpec tcs = new TransitionCommandSpec("GET", null);
		TransitionCommandSpec tcs2 = new TransitionCommandSpec("GET", null);
		assertEquals(tcs, tcs2);
		assertEquals(tcs2, tcs);
		assertEquals(tcs.hashCode(), tcs2.hashCode());
	}

	@Test
	public void testInequalityNull() {
		TransitionCommandSpec tcs = new TransitionCommandSpec("GET", null);
		TransitionCommandSpec tcs2 = new TransitionCommandSpec("PUT", "/test");
		assertFalse(tcs.equals(tcs2));
		assertFalse(tcs.hashCode() == tcs2.hashCode());
	}

	@Test
	public void testInequalityBothNull() {
		TransitionCommandSpec tcs = new TransitionCommandSpec("GET", null);
		TransitionCommandSpec tcs2 = new TransitionCommandSpec("PUT", null);
		assertFalse(tcs.equals(tcs2));
		assertFalse(tcs.hashCode() == tcs2.hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("GET", new TransitionCommandSpec("GET", "").toString());
		assertEquals("GET /test", new TransitionCommandSpec("GET", "/test").toString());
		assertEquals("GET", new TransitionCommandSpec("GET", null).toString());
	}

}
