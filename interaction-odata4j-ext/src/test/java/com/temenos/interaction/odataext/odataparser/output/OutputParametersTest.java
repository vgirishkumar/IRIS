package com.temenos.interaction.odataext.odataparser.output;

/*
 * Test class for the oData parameter printer.
 * 
 * Tests correct handling of parameter lists. Does not check printing of individual expressions. That is the visitors job.
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.Expression;

public class OutputParametersTest {

    /*
     * Test appending a single named parameter
     */
    @Test
    public void testSingleNamedParameter() {
        String expectedName = "name";
        String expectedValue = "value";

        StringBuffer sb = new StringBuffer();
        EntitySimpleProperty property = Expression.simpleProperty(expectedValue);

        ParameterPrinter printer = new ParameterPrinter();
        printer.appendParameter(sb, expectedName, property, true);

        assertEquals(expectedName + "=" + expectedValue, sb.toString());
    }

    /*
     * Test appending a single unnamed parameter
     */
    @Test
    public void testSingleUnNamedParameter() {
        String expectedValue = "value";

        StringBuffer sb = new StringBuffer();
        EntitySimpleProperty property = Expression.simpleProperty(expectedValue);

        ParameterPrinter printer = new ParameterPrinter();
        boolean first = printer.appendParameter(sb, property, true);

        assertFalse(first);
        assertEquals(expectedValue, sb.toString());
    }

    /*
     * Null single parameters should throw.
     */
    @Test(expected = NullPointerException.class)
    public void testNullUnnamedParameter() {
        StringBuffer sb = new StringBuffer();
        ParameterPrinter printer = new ParameterPrinter();
        printer.appendParameter(sb, null, true);
    }

    /*
     * Null named parameters should not be added.
     */
    @Test
    public void testNullNamedParameter() {
        String expectedName = "name";

        StringBuffer sb = new StringBuffer();
        ParameterPrinter printer = new ParameterPrinter();
        boolean first = printer.appendParameter(sb, expectedName, null, true);

        assertTrue(first);
        assertTrue(sb.toString().isEmpty());
    }

    /*
     * Test appending a multiple named parameters
     */
    @Test
    public void testMultipleNamedParameter() {
        String expectedName1 = "name";
        String expectedValue1 = "value";
        String expectedName2 = "anothername";
        String expectedValue2 = "anothervalue";

        StringBuffer sb = new StringBuffer();
        EntitySimpleProperty property1 = Expression.simpleProperty(expectedValue1);
        EntitySimpleProperty property2 = Expression.simpleProperty(expectedValue2);

        boolean first = true;
        ParameterPrinter printer = new ParameterPrinter();
        first = printer.appendParameter(sb, expectedName1, property1, first);
        assertFalse(first);
        first = printer.appendParameter(sb, expectedName2, property2, first);
        assertFalse(first);

        assertEquals(expectedName1 + "=" + expectedValue1 + "&" + expectedName2 + "=" + expectedValue2, sb.toString());
    }

    /*
     * Test appending a single unnamed parameters
     */
    @Test
    public void testMultipleUnNamedParameter() {
        String expectedValue1 = "value";
        String expectedValue2 = "anothervalue";

        StringBuffer sb = new StringBuffer();
        EntitySimpleProperty property1 = Expression.simpleProperty(expectedValue1);
        EntitySimpleProperty property2 = Expression.simpleProperty(expectedValue2);

        boolean first = true;
        ParameterPrinter printer = new ParameterPrinter();
        first = printer.appendParameter(sb, property1, first);
        first = printer.appendParameter(sb, property2, first);

        assertEquals(expectedValue1 + "&" + expectedValue2, sb.toString());
    }

    /*
     * Test appending a multiple named parameters as a list
     */
    @Test
    public void testListParameter() {
        String expectedName1 = "name";
        String expectedValue1 = "value";
        String expectedValue2 = "anothervalue";

        StringBuffer sb = new StringBuffer();
        EntitySimpleProperty property1 = Expression.simpleProperty(expectedValue1);
        EntitySimpleProperty property2 = Expression.simpleProperty(expectedValue2);

        List<EntitySimpleProperty> list = new ArrayList<EntitySimpleProperty>();
        list.add(property1);
        list.add(property2);

        ParameterPrinter printer = new ParameterPrinter();
        boolean first = printer.appendParameter(sb, expectedName1, list, true);
        assertFalse(first);

        assertEquals(expectedName1 + "=" + expectedValue1 + ", " + expectedValue2, sb.toString());
    }

    /*
     * Test empty list
     */
    @Test
    public void testListEmpty() {
        StringBuffer sb = new StringBuffer();
        List<EntitySimpleProperty> list = new ArrayList<EntitySimpleProperty>();
        ParameterPrinter printer = new ParameterPrinter();
        boolean first = printer.appendParameter(sb, list, true);
        assertTrue(first);
        assertTrue(sb.toString().isEmpty());
    }
}
