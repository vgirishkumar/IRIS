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


import java.util.HashMap;
import java.util.Map;


/**
 * A simple map based resource locator provider 
 *
 * @author mlambert
 *
 */
public class MapResourceLocatorProvider implements ResourceLocatorProvider {
	private Map<String, ResourceLocator> nameToLocator = new HashMap<String, ResourceLocator>();

	/**
	 * @param nameToLocator
	 */
	public MapResourceLocatorProvider(Map<String, ResourceLocator> nameToLocator) {
		if(nameToLocator == null || nameToLocator.isEmpty()) {
			throw new IllegalArgumentException("nameToLocator must be a non empty map");
		}
		
		this.nameToLocator = nameToLocator;
	}


	@Override
	public ResourceLocator get(String name) {
		
		if(nameToLocator.containsKey(name)) {
			return nameToLocator.get(name);
		}
		
		throw new IllegalArgumentException("Invalid resource locator name: " + name);
	}

}
