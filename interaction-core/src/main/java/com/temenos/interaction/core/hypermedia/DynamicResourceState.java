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


import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class represent placeholder states in use cases where transitions are determined by selection of an item from a prior response; at runtime the real target
 * state will be resolved using a resource locator 
 *
 * @author mlambert
 *
 */
public class DynamicResourceState extends ResourceState {
	private String resourceLocatorName;
	private String[] resourceLocatorArgs;
	
	/**
	 * @param entityName
	 * @param name
	 * @param actions
	 * @param path
	 */
	public DynamicResourceState(String entityName, String name, String resourceLocatorName, String... resourceLocatorArgs) {
		super(entityName, name, new ArrayList<Action>(), "DYNAMIC");
		this.resourceLocatorName = resourceLocatorName;
		this.resourceLocatorArgs = resourceLocatorArgs;
	}		

	/**
	 * @param parent
	 * @param name
	 * @param actions
	 * @param path
	 */
	public DynamicResourceState(ResourceState parent, String name, List<Action> actions, String path, String resourceLocatorName, String... resourceLocatorArgs) {
		super(parent, name, actions, path);
		this.resourceLocatorName = resourceLocatorName;
		this.resourceLocatorArgs = resourceLocatorArgs;		
	}

	@Override
	public boolean isPseudoState() {
		// True as any instance of this class represents a placeholder state
		return true;
	}

	/**
	 * @return the resourceLocatorName
	 */
	public String getResourceLocatorName() {
		return resourceLocatorName;
	}

	/**
	 * @return the resourceLocatorArgs
	 */
	public String[] getResourceLocatorArgs() {
		return resourceLocatorArgs;
	}		
}
