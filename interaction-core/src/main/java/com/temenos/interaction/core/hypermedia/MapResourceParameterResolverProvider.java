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
 * A simple map based resource parameter resolver provider 
 *
 * @author mlambert
 *
 */
public class MapResourceParameterResolverProvider implements ResourceParameterResolverProvider {
	private Map<String, ResourceParameterResolver> nameToParameterResolver = new HashMap<String, ResourceParameterResolver>();

	/**
	 * @param nameToLocator
	 */
	public MapResourceParameterResolverProvider(Map<String, ResourceParameterResolver> nameToParameterResolver) {
		if(nameToParameterResolver == null || nameToParameterResolver.isEmpty()) {
			throw new IllegalArgumentException("nameToParameterResolver must be a non empty map");
		}
		
		this.nameToParameterResolver = nameToParameterResolver;
	}


	@Override
	public ResourceParameterResolver get(String name) {
		
		if(nameToParameterResolver.containsKey(name)) {
			return nameToParameterResolver.get(name);
		}
		
		throw new IllegalArgumentException("Invalid resource locator name: " + name);
	}

}
