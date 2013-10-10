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


import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;

/**
 * This class holds information about a resource state
 */
public class IMNavigationState extends IMState {

	private boolean toCollectionResource;		//true collection state, false entity state
	private IMResourceStateMachine resourceStateMachine;		//Target RSM

	public IMNavigationState(String name, String path, IMResourceStateMachine resourceStateMachine, boolean toCollectionResource) {
		super(name, path, null);
		this.resourceStateMachine = resourceStateMachine;
		this.toCollectionResource = toCollectionResource;
	}
	
	public IMResourceStateMachine getTargetResourceStateMachine() {
		 return resourceStateMachine;
	}
	
	/**
	 * Returns whether this is a navigation to a collection
	 * or an entity resource.
	 * @return true or false
	 */
	public boolean isNavigationToCollectionResource() {
		return toCollectionResource;
	}
}
