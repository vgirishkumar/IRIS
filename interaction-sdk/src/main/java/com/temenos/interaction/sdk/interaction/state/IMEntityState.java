package com.temenos.interaction.sdk.interaction.state;

/*
 * #%L
 * interaction-sdk
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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


import java.util.HashSet;
import java.util.Set;

/**
 * This class holds information about a resource state
 */
public class IMEntityState extends IMState implements IMAction {

	private Set<String> actions = new HashSet<String>();		//Actions
	
	/**
	 * Construct a new resource state 
	 * @param name resource state name
	 * @param path URI path associated to this resource state
	 */
	public IMEntityState(String name, String path) {
		super(name, path, null);
	}
	
	/**
	 * Construct a new resource state 
	 * @param name resource state name
	 * @param path URI path associated to this resource state
	 * @param view View to execute
	 */
	public IMEntityState(String name, String path, String view) {
		super(name, path, view);
	}

	/**
	 * Construct a new resource state which triggers an action
	 * @param name resource state name
	 * @param path URI path associated to this resource state
	 * @param relations link relations
	 * @param action action to execute
	 */
	public IMEntityState(String name, String path, String relations, String action) {
		super(name, path, null, relations);
		this.actions.add(action);
	}
	
	public boolean hasView() {
		return super.getView() != null;
	}

	@Override
	public boolean hasActions() {
		return actions.size() > 0;
	}
	
	@Override
	public Set<String> getActions() {
		return actions;
	}
}
