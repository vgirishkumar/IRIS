package com.temenos.interaction.sdk.interaction.state;

import com.temenos.interaction.sdk.command.Commands;

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
	
	@Override
	public String getView() {
		return super.getView() != null ? super.getView() : Commands.GET_ENTITY;
	}
}
