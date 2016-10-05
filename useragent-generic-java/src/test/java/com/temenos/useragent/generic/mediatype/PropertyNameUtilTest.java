package com.temenos.useragent.generic.mediatype;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyNameUtilTest {

	@Test
	public void testExtractIndex() {
		assertEquals(1, PropertyNameUtil.extractIndex("foo(1)"));
		assertEquals(0, PropertyNameUtil.extractIndex("foo"));

		try {
			PropertyNameUtil.extractIndex(null);
			fail("IllegalArgumentException should be thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			PropertyNameUtil.extractIndex("");
			fail("IllegalArgumentException should be thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testExtractPropertyName() {
		assertEquals("foo", PropertyNameUtil.extractPropertyName("foo(1)"));
		assertEquals("foo", PropertyNameUtil.extractPropertyName("foo"));
		assertEquals("", PropertyNameUtil.extractPropertyName(""));

		try {
			PropertyNameUtil.extractPropertyName(null);
			fail("IllegalArgumentException should be thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testFlattenPropertyName() {
		String[] parts = PropertyNameUtil.flattenPropertyName("foo");
		assertEquals(1, parts.length);
		assertEquals("foo", parts[0]);

		parts = PropertyNameUtil.flattenPropertyName("foo(0)/bar(1)/blah");
		assertEquals(3, parts.length);
		assertEquals("foo(0)", parts[0]);
		assertEquals("bar(1)", parts[1]);
		assertEquals("blah", parts[2]);

		parts = PropertyNameUtil.flattenPropertyName("");
		assertEquals(0, parts.length);

		try {
			PropertyNameUtil.flattenPropertyName(null);
			fail("IllegalArgumentException should be thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testIsPropertyNameWithIndex() {
		assertTrue(PropertyNameUtil.isPropertyNameWithIndex("foo(0)"));
		assertFalse(PropertyNameUtil.isPropertyNameWithIndex("foo"));
		assertFalse(PropertyNameUtil.isPropertyNameWithIndex(""));
		assertFalse(PropertyNameUtil.isPropertyNameWithIndex(null));
	}
}
