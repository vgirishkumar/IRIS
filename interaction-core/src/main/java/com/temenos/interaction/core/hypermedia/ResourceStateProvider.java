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

import java.util.Map;
import java.util.Set;

public interface ResourceStateProvider {

	/**
	 * Return true if resource state for name can be found
	 * 
	 * @param name
	 * @return
	 */
	public boolean isLoaded(String name);

	/**
	 * Lookup and return a single {@link ResourceState} by name
	 * 
	 * @param name
	 * @return
	 */
	public ResourceState getResourceState(String name);

	/**
	 * Using the supplied event and path return the {@link ResourceState} that
	 * is being requested.
	 * 
	 * @param event
	 * @param resourcePath
	 * @return
	 */
	public ResourceState determineState(Event event, String resourcePath);

	/**
	 * Return a map of resource state names where the path is the key.
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getResourceStatesByPath();

	/**
	 * Return a map of resource methods accepted by a resources where the
	 * resource state name is the key.
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getResourceMethodsByState();

	/**
	 * Return a map to a resource path where the resource state name is the key.
	 * 
	 * @return
	 */
	public Map<String, String> getResourcePathsByState();
}
