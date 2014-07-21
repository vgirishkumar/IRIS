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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.ResourceLocator;
import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * A mock resource locator
 *
 * @author mlambert
 *
 */
public class NoteResourceLocator implements ResourceLocator {	
	private final Map<String, ResourceState> aliasToResourceState = new HashMap<String, ResourceState>();
	
	public NoteResourceLocator(ResourceState state, Map<String, ResourceState> additionalAliasToResourceState) {
		collectResourceStatesByName(aliasToResourceState, new ArrayList<ResourceState>(), state);
		
		aliasToResourceState.putAll(additionalAliasToResourceState);
	}
		
	@Override
	public ResourceState resolve(Object... alias) {
		if(aliasToResourceState.containsKey(alias[0])) {
			 return aliasToResourceState.get(alias[0]);	
		} else {
			throw new RuntimeException("Invalid resource state supplied: " + alias[0]);							
		}		
	}
		
	private void collectResourceStatesByName(Map<String, ResourceState> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) {
			return;
		}
		
		states.add(currentState);
		
		// add current state to results
		result.put(currentState.getName(), currentState);
		
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.equals(currentState)) {
				String name = next.getName();
				result.put(name, next);
			}
			
			// Recurse
			collectResourceStatesByName(result, states, next);
		}
	}	
}
