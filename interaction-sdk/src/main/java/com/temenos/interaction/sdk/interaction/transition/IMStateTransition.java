package com.temenos.interaction.sdk.interaction.transition;

import com.temenos.interaction.sdk.interaction.state.IMResourceState;

/**
 * This class holds information about a state transition 
 */
public class IMStateTransition extends IMTransition {

	private boolean auto;					//Auto transition
	private boolean boundToCollection;		// the pseudo state is to be bound to the collection path 
	
	public IMStateTransition(String title, 
			IMResourceState targetState, 
			String method, 
			boolean auto, 
			boolean boundToCollection) {
		super(title, targetState, method);
		this.auto = auto;
		this.boundToCollection = boundToCollection;
	}
	
	public boolean isAutoTransition() {
		return auto;
	}
	
	public boolean isBoundToCollection() {
		return boundToCollection;
	}
}
