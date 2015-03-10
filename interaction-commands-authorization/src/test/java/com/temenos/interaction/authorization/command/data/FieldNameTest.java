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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.authorization.command.data.FieldName;

public class FieldNameTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testConstruct() {

		FieldName name = new FieldName("aname");

		assertEquals("aname", name.getName());
	}
	
	@Test
	public void testEquivalence() {

		FieldName name1 = new FieldName("aname");
		FieldName name2 = new FieldName("aname");
		FieldName name3 = new FieldName("aothername");

		assertEquals(name1, name2);
		assertFalse(name1.equals(name3));
	}
	
	@Test
	public void testHashEquivalence() {

		FieldName name1 = new FieldName("aname");
		FieldName name2 = new FieldName("aname");
		FieldName name3 = new FieldName("aothername");

		assertTrue(name1.hashCode() == name2.hashCode());
		assertFalse(name1.hashCode() == name3.hashCode());
	}
}
