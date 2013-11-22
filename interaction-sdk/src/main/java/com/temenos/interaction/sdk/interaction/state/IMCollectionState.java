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


import com.temenos.interaction.sdk.command.Commands;

/**
 * This class holds information about an entity state
 */
public class IMCollectionState extends IMState {

	private IMEntityState entityState;

	/**
	 * Construct a new resource state 
	 * @param name resource state name
	 * @param path URI path associated to this resource state
	 */
	public IMCollectionState(String name, String path, IMEntityState entityState) {
		super(name, path, null);
		this.entityState = entityState;
	}
	
	/**
	 * Construct a new resource state 
	 * @param name resource state name
	 * @param path URI path associated to this resource state
	 * @param view View to execute
	 */
	public IMCollectionState(String name, String path, String view, IMEntityState entityState) {
		super(name, path, view);
		this.entityState = entityState;
	}

	/**
	 * Construct a new resource state 
	 * @param name resource state name
	 * @param path URI path associated to this resource state
	 * @param view View to execute
	 * @param relations link relations
	 */
	public IMCollectionState(String name, String path, String view, String relations, IMEntityState entityState) {
		super(name, path, view, relations);
		this.entityState = entityState;
	}
	
	/**
	 * Returns the entity state associated to this collection state
	 * @return entity state
	 */
	public IMEntityState getEntityState() {
		return entityState;
	}
	
	@Override
	public String getView() {
		return super.getView() != null ? super.getView() : Commands.GET_ENTITIES;
	}
}
