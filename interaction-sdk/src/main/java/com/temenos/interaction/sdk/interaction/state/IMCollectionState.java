package com.temenos.interaction.sdk.interaction.state;

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
