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
public class IMPseudoState extends IMState implements IMAction {

	private Set<String> actions = new HashSet<String>();		//Actions
	private String pseudoStateId = null;						//Pseudo state Id or null if this is not a pseudo state

	public IMPseudoState(String name, String path, String view, String pseudoStateId, String relations, String action) {
		super(name, path, view, relations);
		this.pseudoStateId = pseudoStateId;
		if(action != null) {
			this.actions.add(action);
		}
	}
	
	/**
	 * Returns whether this is a pseudo state.
	 * A pseudo state is an intermediate state to handle an action
	 * @return true or false
	 */
	public boolean isPseudoState() {
		return pseudoStateId != null;
	}
	
	/**
	 * Add a transition triggering a state change in the underlying resource manager
	 * @param targetState		Target state
	 * @param method			HTTP command
	 */
	public void addAutoTransition(IMState targetState, String method) {
		this.addTransition(null, targetState, method, true, false);
	}
	
	@Override
	public Set<String> getActions() {
		return actions;
	}

	@Override
	public boolean hasActions() {
		return actions.size() > 0;
	}
}
