package com.temenos.interaction.sdk;

/*
 * #%L
 * interaction-sdk
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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestJPAEntityInfo {

	@Test
	public void testGetPackageAsPath() {
		EntityInfo ei = new EntityInfo("BlahClass", "com", null, null);
		assertEquals("com", ei.getPackageAsPath());
		EntityInfo ei1 = new EntityInfo("BlahClass", "com.temenos", null, null);
		assertEquals("com/temenos", ei1.getPackageAsPath());
		EntityInfo ei2 = new EntityInfo("BlahClass", "com.temenos.stuff", null, null);
		assertEquals("com/temenos/stuff", ei2.getPackageAsPath());
		EntityInfo ei3 = new EntityInfo("BlahClass", "", null, null);
		assertEquals("", ei3.getPackageAsPath());
		EntityInfo ei4 = new EntityInfo("BlahClass", null, null, null);
		assertEquals("", ei4.getPackageAsPath());
	}

	@Test
	public void testGetFQTypeName() {
		EntityInfo ei = new EntityInfo("BlahClass", "com.temenos.stuff", null, null);
		assertEquals("com.temenos.stuff.BlahClass", ei.getFQTypeName());
	}

	@Test (expected = AssertionError.class)
	public void testGetFQTypeNameNullClass() {
		EntityInfo ei = new EntityInfo(null, "com.temenos.stuff", null, null);
		ei.getFQTypeName();
		assertNotNull(ei.getClazz());
	}

	@Test
	public void testGetFQTypeNameNullPackage() {
		EntityInfo ei = new EntityInfo("BlahClass", null, null, null);
		assertEquals("BlahClass", ei.getFQTypeName());
	}

}
