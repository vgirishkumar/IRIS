package com.temenos.interaction.authorization.command.data;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.authorization.command.data.RowFilter.Relation;

public class RowFilterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testConstruct() {
		
		FieldName name = new FieldName("aname");

		RowFilter filter = new RowFilter(name, Relation.EQ, "avalue");

		assertEquals("aname", filter.getFieldName().getName());
		assertEquals(Relation.EQ, filter.getRelation());
		assertEquals("avalue", filter.getValue());
	}
	
	@Test
	public void testStringConstruct() {
		
		RowFilter filter = new RowFilter("aname", Relation.EQ, "avalue");

		assertEquals("aname", filter.getFieldName().getName());
		assertEquals(Relation.EQ, filter.getRelation());
		assertEquals("avalue", filter.getValue());
	}
}
