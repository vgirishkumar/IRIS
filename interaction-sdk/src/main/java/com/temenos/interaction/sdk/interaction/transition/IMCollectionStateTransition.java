package com.temenos.interaction.sdk.interaction.transition;

import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.state.IMState;

/**
 * This class holds information about a transition to a collection state
 */
public class IMCollectionStateTransition extends IMTransition {

	private String linkProperty;			//The depend element of a referential constraint (i.e. the property that specifies the target resource)
	private IMResourceStateMachine targetResourceStateMachine;	//Resource state machine of target state
	private String filter;					//Filter expression for transitions to collection resources
	
	public IMCollectionStateTransition(
			IMResourceStateMachine targetResourceStateMachine, 
			IMState targetState, 
			String linkProperty,
			String filter, 
			String title, 
			String method) {
		super(title, targetState, method);
		this.linkProperty = linkProperty;
		this.targetResourceStateMachine = targetResourceStateMachine;
		this.filter = filter;
	}
	
	public String getLinkProperty() {
		return linkProperty;
	}

	public String getFilter() {
		return filter != null && !filter.equals("") ? filter : "1 eq 1";
	}
	
	public IMResourceStateMachine getTargetResourceStateMachine() {
		return targetResourceStateMachine;
	}
}
