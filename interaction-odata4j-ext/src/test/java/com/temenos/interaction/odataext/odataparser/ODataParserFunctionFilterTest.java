package com.temenos.interaction.odataext.odataparser;

/*
 * Test class for the oData unary function parser/printer filter operations.
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

import org.junit.Test;

import com.temenos.interaction.odataext.odataparser.data.Relation;
import com.temenos.interaction.odataext.odataparser.output.OutputExpressionVisitor;

public class ODataParserFunctionFilterTest extends AbstractODataParserFilterTest {

    @Test
    public void testUnaaryFunctionsFilter() {
        for (Relation rel : Relation.values()) {
            if (rel.isFunctionCall() && (1 == rel.getExpectedArgumentCount())) {
                if (Relation.ISOF != rel) {
                    testValid("a eq " + rel.getoDataString() + "(a)");
                } else {
                    // ISOF requires literal arguments.
                    testValid("a eq " + rel.getoDataString() + "('a')");
                }
            }
        }
    }

    @Test
    public void testBinaryFunctionsFilter() {
        for (Relation rel : Relation.values()) {
            if (rel.isFunctionCall() && (2 == rel.getExpectedArgumentCount())) {
                testValid("a eq " + rel.getoDataString() + "(a, b)");
            }
        }
    }

    /*
     * Test the few ternary operators
     */
    @Test
    public void testTernaryFunctionsFilter() {
        // Test the 3 argument version of substring
        testValid("a eq substring('a', 'b', 'c')");

        for (Relation rel : Relation.values()) {
            if (rel.isFunctionCall() && (3 == rel.getExpectedArgumentCount())) {
                testValid("a eq " + rel.getoDataString() + "(a, b, c)");
            }
        }
    }

    /*
     * Test a few special function.
     */
    @Test
    public void testSpecialFunctionsFilter() {
        // Substr can also be called with 3 args
        testValid("a eq " + Relation.SUBSTR.getoDataString() + "(a, b, c)");
    }

    /*
     * Test the visitor passing interface.
     */
    @Test
    public void testVisitorFunctionsFilter() {
        String expected = "a eq " + Relation.SUBSTR.getoDataString() + "(a, b, c)";
        String actual = ODataParser.toFilters(ODataParser.parseFilters(expected), new OutputExpressionVisitor());
        assertEquals(expected, actual);
    }
}
