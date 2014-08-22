package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestMapResourceLocatorProvider {

	@Test(expected=IllegalArgumentException.class)
	public void testNullConstructor() {
		new MapResourceLocatorProvider(null);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEmptyConstructor() {
		new MapResourceLocatorProvider(null);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetInvalidResourceLocator() {
		Map<String, ResourceLocator> nameToResourceLocator = new HashMap<String, ResourceLocator>();
		nameToResourceLocator.put("dummy", mock(ResourceLocator.class));
				
		ResourceLocatorProvider resourceLocatorProvider = new MapResourceLocatorProvider(nameToResourceLocator);
		resourceLocatorProvider.get("fred");
	}
	
	@Test
	public void testGetValidResourceLocator() {
		Map<String, ResourceLocator> nameToResourceLocator = new HashMap<String, ResourceLocator>();
		ResourceLocator expectedLocator = mock(ResourceLocator.class);
		nameToResourceLocator.put("dummy", expectedLocator);
				
		ResourceLocatorProvider resourceLocatorProvider = new MapResourceLocatorProvider(nameToResourceLocator);
		ResourceLocator actualLocator = resourceLocatorProvider.get("dummy");
		
		assertEquals(expectedLocator, actualLocator);
	}		
}
