package com.temenos.interaction.core.resource;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
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

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

public class TestConfigLoader {

	@Test
	public void testIsExistClasspath() {
		ConfigLoader loader = new ConfigLoader();
		boolean found = loader.isExist("metadata-CountryList.xml");
		assertTrue(found);
	}

	@Test
	public void testNotFoundIsExistClasspath() {
		ConfigLoader loader = new ConfigLoader();
		boolean found = loader.isExist("somefilethatdoesnotexist");
		assertFalse(found);
	}

	@Test
	public void testLoadClasspath() throws FileNotFoundException, Exception {
		ConfigLoader loader = new ConfigLoader();
		InputStream in = loader.load("metadata-CountryList.xml");
		assertNotNull(in);
	}

}
