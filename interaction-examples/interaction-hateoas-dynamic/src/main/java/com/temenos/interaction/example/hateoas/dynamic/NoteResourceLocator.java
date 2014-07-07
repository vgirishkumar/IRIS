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
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

/**
 * A mock resource locator
 *
 * @author mlambert
 *
 */
public class NoteResourceLocator implements ResourceLocator {
	private ResourceStateMachine stateMachine;
	
	private static final Map<String, String> aliasToResourceName = new HashMap<String, String>();
	
	static {
		aliasToResourceName.put("{Author}", "noteDynmc");
	}
	
	/**
	 * @param stateMachine
	 */
	public NoteResourceLocator(ResourceStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	@Override
	public ResourceState resolve(Object id, String... alias) {

		if(aliasToResourceName.containsKey(alias[0])) {
			String resourceName = aliasToResourceName.get(alias[0]);
			
			return stateMachine.getResourceStateByName(resourceName);	
		} else {
			throw new IllegalArgumentException("Invalid resource state supplied: " + alias[0]);
		}		
	}
}
