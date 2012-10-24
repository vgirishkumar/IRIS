package com.temenos.interaction.sdk.interaction;

/**
 * This class holds information about a resource state machine
 */
public class IMTransition {

	private String targetEntityName;		//Entity name associated to target state
	private String linkProperty;			//The depend element of a referential constraint (i.e. the property that specifies the target resource)
	private String targetStateName;			//Name of target state
	private boolean isCollectionState;		//Indicates if target is a collection state
	private String reciprocalLinkState;		//State which leads a target state back to the current state
	private IMResourceStateMachine targetResourceStateMachine;	//Resource state machine of target state
	
	public IMTransition(String targetEntityName, String linkProperty, String targetStateName, boolean isCollectionState, String reciprocalLinkState, IMResourceStateMachine targetResourceStateMachine) {
		this.targetEntityName = targetEntityName;
		this.linkProperty = linkProperty;
		this.targetStateName = targetStateName;
		this.isCollectionState = isCollectionState;
		this.reciprocalLinkState = reciprocalLinkState;
		this.targetResourceStateMachine = targetResourceStateMachine;
	}
	
	public String getTargetEntityName() {
		return targetEntityName;
	}
	
	public String getLinkProperty() {
		return linkProperty;
	}

	public String getTargetStateName() {
		return targetStateName;
	}

	public boolean isCollectionState() {
		return isCollectionState;
	}

	/**
	 * Returns the resource state name of the target RSM which is either the
	 * reciprocal state or, if not defined, the entity state of the target RSM. 
	 * @return resource state name 
	 */
	public String getTargetRsmStateName() {
		return (reciprocalLinkState != null && !reciprocalLinkState.equals("")) ? reciprocalLinkState : targetResourceStateMachine.getEntityStateName();
	}
	
	public IMResourceStateMachine getTargetResourceStateMachine() {
		return targetResourceStateMachine;
	}
}
