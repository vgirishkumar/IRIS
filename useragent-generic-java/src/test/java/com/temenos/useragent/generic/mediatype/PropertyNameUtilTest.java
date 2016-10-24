package com.temenos.useragent.generic.mediatype;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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

		try {
			parts = PropertyNameUtil.flattenPropertyName("");
			fail("IllegalArgumentException should be thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

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
