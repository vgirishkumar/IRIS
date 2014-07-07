package com.temenos.interaction.example.hateoas.dynamic;

/*
 * #%L
 * interaction-example-hateoas-dynamic
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

import com.temenos.interaction.core.hypermedia.ResourceLocator;
import com.temenos.interaction.core.hypermedia.ResourceLocatorProvider;

/**
 * A mock resource locator provider
 *
 * @author mlambert
 *
 */
public class NoteResourceLocatorProvider implements ResourceLocatorProvider {
	private Map<String, ResourceLocator> aliasToResourceLocator = new HashMap<String, ResourceLocator>();
	
	/**
	 * @param aliasToResourceLocator
	 */
	public NoteResourceLocatorProvider(Map<String, ResourceLocator> aliasToResourceLocator) {
		this.aliasToResourceLocator = aliasToResourceLocator;
	}

	@Override
	public ResourceLocator get(String name) {
		return aliasToResourceLocator.get(name);			
	}
}
