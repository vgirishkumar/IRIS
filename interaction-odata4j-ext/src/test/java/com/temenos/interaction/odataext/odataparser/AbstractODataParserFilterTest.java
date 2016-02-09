package com.temenos.interaction.odataext.odataparser;

/*
 * Base test class for the oData parser/printer filter operations.
 * 
 * Not too concerned with the intermediate format of data but it must survive the 'round trip' into intermediate format
 * and back to a string.
 */

/* 
 * #%L
 * interaction-odata4j-ext
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractODataParserFilterTest {

    // Test round trip for a valid filter
    protected void testValid(String expected) {

        String actual = null;
        boolean threw = false;
        try {
            actual = ODataParser.toFilters(ODataParser.parseFilters(expected));
        } catch (Exception e) {
            threw = true;
        }

        assertFalse(threw);
        assertEquals(expected, actual);
    }

    // Test round trip for a valid old style filter
    @Deprecated
    protected void testOldValid(String expected) {

        String actual = null;
        boolean threw = false;
        try {
            actual = ODataParser.toFilter(ODataParser.parseFilter(expected));
        } catch (Exception e) {
            threw = true;
        }

        assertFalse(threw);
        assertEquals(expected, actual);
    }

    // Test invalid filter throws
    protected void testInvalid(String expected) {
        String actual = null;
        boolean threw = false;
        try {
            actual = ODataParser.toFilters(ODataParser.parseFilters(expected));
        } catch (Exception e) {
            threw = true;
        }
        assertTrue("Didn't throw. Expected \"" + expected + "\"Actual is \"" + actual + "\"", threw);
    }
}
