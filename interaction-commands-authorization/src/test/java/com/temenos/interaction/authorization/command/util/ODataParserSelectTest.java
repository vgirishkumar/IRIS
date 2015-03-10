package com.temenos.interaction.authorization.command.util;

/*
 * Test class for the oData parser/printer select operations.
 * 
 * Not too concerned with the intermediate format of data but it must survive the 'round trip' into intermediate format
 * and back to a string.
 */

/* 
 * #%L
 * interaction-commands-authorization
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.authorization.command.util.ODataParser;

public class ODataParserSelectTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test valid Selects work
	 */
	@Test
	public void testSimpleSelect() {
		testValid("a");
	}

	/**
	 * Test empty Selects.
	 */
	@Test
	public void testEmptySelect() {
		testValid("");
	}

	/*
	 * Test Select containing multiple terms
	 */
	@Test
	public void testMultipleSelect() {
		testValid("a, b, c");
	}

	/*
	 * Test Select containing quoted elements
	 * 
	 * This appears to be invalid. Doc implies that spaces in oData col names
	 * are illegal. However if this is incorrect feel free to amend this test.
	 */
	@Test
	public void testQuotesSelect() {
		testInvalid("'a b'");
	}

	/**
	 * Test invalid Selects throw.
	 */
	@Test
	public void testBadSelect() {
		// Bad condition. Not sure this is 'bad'.
		// testInvalid(",,");

		// Can't parse a null string.
		testInvalid(null);

		// Wrong number of element (unquoted)
		testInvalid("a b");
		testInvalid("a b c");
	}

	/**
	 * Test null intermediate select.
	 */
	@Test
	public void testNullSelect() {

		String actual = null;
		boolean threw = false;
		try {
			actual = ODataParser.toSelect(null);
		} catch (Exception e) {
			threw = true;
		}
		assertTrue("Didn't throw. Expected \"" + null + "\"Actual is \"" + actual + "\"", threw);
	}

	// Test round trip for a valid Select
	private void testValid(String expected) {

		Exception e = null;

		String actual = null;
		boolean threw = false;
		try {
			actual = ODataParser.toSelect(ODataParser.parseSelect(expected));
		} catch (Exception caught) {
			threw = true;
			e = caught;
		}

		assertFalse("Threw : " + e, threw);

		// Order may have been changed so we have to do out own parsing (which
		// could also be wrong).
		List<String> expectedList = Arrays.asList(expected.split("\\s*,\\s*"));
		List<String> actualList = Arrays.asList(actual.split("\\s*,\\s*"));

		for (String str : expectedList) {
			assertTrue("Expected \"" + expected + "\"Actual is \"" + actual + "\"", actualList.contains(str));
		}

		for (String str : actualList) {
			assertTrue("Expected \"" + expected + "\"Actual is \"" + actual + "\"", expectedList.contains(str));
		}
	}

	// Test invalid Select throws
	private void testInvalid(String expected) {

		boolean threw = false;
		try {
			ODataParser.toSelect(ODataParser.parseSelect(expected));
		} catch (Exception e) {
			threw = true;
		}
		assertTrue(threw);
	}

}
