package com.temenos.interaction.sdk.interaction.state;

/**
 * This class holds information about a resource state
 */
public class IMEntityState extends IMState {
	
	/**
	 * Construct a new resource state 
	 * @param name resource state name
	 * @param path URI path associated to this resource state
	 */
	public IMEntityState(String name, String path) {
		super(name, path);
	}
}
