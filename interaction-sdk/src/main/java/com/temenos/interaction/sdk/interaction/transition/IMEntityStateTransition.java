package com.temenos.interaction.sdk.interaction.transition;

import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.state.IMState;

/**
 * This class holds information about a transition to a collection state
 */
public class IMEntityStateTransition extends IMTransition {

	private String linkProperty;			//The depend element of a referential constraint (i.e. the property that specifies the target resource)
	private IMResourceStateMachine targetResourceStateMachine;	//Resource state machine of target state
	
	public IMEntityStateTransition(
			IMResourceStateMachine targetResourceStateMachine, 
			IMState targetState, 
			String linkProperty,
			String title, 
			String method) {
		super(title, targetState, method);
		this.linkProperty = linkProperty;
		this.targetResourceStateMachine = targetResourceStateMachine;
	}
	
	public String getLinkProperty() {
		return linkProperty;
	}

	public IMResourceStateMachine getTargetResourceStateMachine() {
		return targetResourceStateMachine;
	}
}
