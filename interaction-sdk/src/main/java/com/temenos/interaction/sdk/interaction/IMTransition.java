package com.temenos.interaction.sdk.interaction;

/**
 * This class holds information about a resource state machine
 */
public class IMTransition {

	private String targetEntityName;		//Entity name associated to target state
	private String targetStateName;			//Name of target state
	private boolean isCollectionState;		//Indicates if target is a collection state
	private IMResourceStateMachine targetResourceStateMachine;	//Resource state machine of target state
	
	public IMTransition(String targetEntityName, String targetStateName, boolean isCollectionState, IMResourceStateMachine targetResourceStateMachine) {
		this.targetEntityName = targetEntityName;
		this.targetStateName = targetStateName;
		this.isCollectionState = isCollectionState;
		this.targetResourceStateMachine = targetResourceStateMachine;
	}
	
	public String getTargetEntityName() {
		return targetEntityName;
	}

	public String getTargetStateName() {
		return targetStateName;
	}

	public boolean isCollectionState() {
		return isCollectionState;
	}

	public IMResourceStateMachine getTargetResourceStateMachine() {
		return targetResourceStateMachine;
	}
}
