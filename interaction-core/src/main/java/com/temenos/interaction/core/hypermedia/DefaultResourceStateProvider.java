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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultResourceStateProvider implements ResourceStateProvider {

	private ResourceStateMachine hypermediaEngine;
	
	public DefaultResourceStateProvider(ResourceStateMachine hypermediaEngine) {
		this.hypermediaEngine = hypermediaEngine;
	}

	@Override
	public boolean isLoaded(String name) {
		return hypermediaEngine.getResourceStateByName(name) != null;
	}

	@Override
	public ResourceState getResourceState(String name) {
		return hypermediaEngine.getResourceStateByName(name);
	}

	@Override
	public ResourceState determineState(Event event, String resourcePath) {
		return hypermediaEngine.determineState(event, resourcePath);
	}

	@Override
	public Map<String, Set<String>> getResourceStatesByPath() {
		Map<String, Set<String>> results = new HashMap<String, Set<String>>();
		Map<String, Set<ResourceState>> statesByPath = hypermediaEngine.getResourceStatesByPath();
		for (String key : statesByPath.keySet()) {
			Set<ResourceState> states = statesByPath.get(key);
			Set<String> stateNames = new HashSet<String>();
			for (ResourceState state : states) {
				stateNames.add(state.getName());
			}
			results.put(key, stateNames);
		}
		return results;
	}

	@Override
	public Map<String, Set<String>> getResourceMethodsByState() {
		return hypermediaEngine.getInteractionByState();
	}

	@Override
	public Map<String, String> getResourcePathsByState() {
		Map<String, String> results = new HashMap<String, String>();
		Map<String, ResourceState> statesByName = hypermediaEngine.getResourceStateByName();
	
		for (String key : statesByName.keySet()) {
			ResourceState resourceState = statesByName.get(key);
			results.put(key, resourceState.getPath());
		}
		return results;
	}

}
