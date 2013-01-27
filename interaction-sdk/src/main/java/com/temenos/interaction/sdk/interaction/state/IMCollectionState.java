package com.temenos.interaction.sdk.interaction.state;

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
		super(name, path);
		this.entityState = entityState;
	}
	
	/**
	 * Returns the entity state associated to this collection state
	 * @return entity state
	 */
	public IMEntityState getEntityState() {
		return entityState;
	}
}
