package com.temenos.interaction.odataext.odataparser.data;

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
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.producer.resources.OptionsQueryParser;

import com.temenos.interaction.odataext.odataparser.ODataParser;

public class RowFiltersTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testConstruct() { 
	    String expectedStr = "aname eq avalue";
	    BoolCommonExpression expected = OptionsQueryParser.parseFilter(expectedStr);
		
		RowFilters filters = new RowFilters(expected);
		
		assertEquals(expected, filters.getBoolCommonExpression());
	}
	
	@Test
	public void testStringConstruct() {
        String expectedStr = "aname eq avalue";
        
        RowFilters filters = new RowFilters(expectedStr);
        
        assertEquals(expectedStr, ODataParser.toFilters(filters));
	}
	
	@Test
    public void testStringConstructEmpty() {
        String expectedStr = "";
        RowFilters filters = new RowFilters("");
        
        assertEquals(null, filters.getBoolCommonExpression());
        assertEquals(expectedStr, ODataParser.toFilters(filters));
    }
	
	@Test
    public void testAddFilter() {
        String expectedStr1 = "aname eq avalue";
        String expectedStr2 = "aname ne avalue";
        String expected = expectedStr1 + " and "  + expectedStr2;
        
        RowFilters filters = new RowFilters(expectedStr1);
        filters.addFilter(expectedStr2);
        
        assertEquals(expected, ODataParser.toFilters(filters));
    }
	
	@Test
	@Deprecated
    public void testToRowFilter() {
        String expectedStr = "aname eq avalue and aname ne avalue";
        
        RowFilters filters = new RowFilters(expectedStr);
        
        List<RowFilter> list = filters.asRowFilters();
        
        assertEquals(2, list.size());
        
        assertEquals("aname", list.get(0).getFieldName().getName());
        assertEquals(Relation.EQ, list.get(0).getRelation());
        assertEquals("avalue", list.get(0).getValue());

        assertEquals("aname", list.get(1).getFieldName().getName());
        assertEquals(Relation.NE, list.get(1).getRelation());
        assertEquals("avalue", list.get(1).getValue());
    }
	
	@Test
    @Deprecated
    public void testToEmptyRowFilter() {
        String expectedStr = "";
        
        RowFilters filters = new RowFilters(expectedStr);
        
        List<RowFilter> list = filters.asRowFilters();
        
        assertEquals(0, list.size());
    }
}
