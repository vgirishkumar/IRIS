package com.temenos.interaction.sdk.interaction.state;

import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;

/**
 * This class holds information about a resource state
 */
public class IMNavigationState extends IMState {

	private boolean toCollectionResource;		//true collection state, false entity state
	private IMResourceStateMachine resourceStateMachine;		//Target RSM

	public IMNavigationState(String name, String path, IMResourceStateMachine resourceStateMachine, boolean toCollectionResource) {
		super(name, path);
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
