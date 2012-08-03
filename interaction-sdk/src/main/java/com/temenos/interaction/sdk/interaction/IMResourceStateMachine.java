package com.temenos.interaction.sdk.interaction;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information about a resource state machine
 */
public class IMResourceStateMachine {

	private String entityName;											//Entity name
	private String collectionStateName;									//Name of collection resource state
	private String entityStateName;										//Name of individual entity resource state
	private String mappedEntityProperty;								//Entity property to which the URI template parameter maps to
	private List<IMTransition> transitions = new ArrayList<IMTransition>();		//Transitions
	
	
	public IMResourceStateMachine(String entityName, String collectionStateName, String entityStateName, String mappedEntityProperty) {
		this.entityName = entityName;
		this.collectionStateName = collectionStateName;
		this.entityStateName = entityStateName;
		this.mappedEntityProperty = mappedEntityProperty;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public String getCollectionStateName() {
		return collectionStateName;
	}

	public String getEntityStateName() {
		return entityStateName;
	}
	
	public String getMappedEntityProperty() {
		return mappedEntityProperty;
	}
	
	public void addTransition(String targetEntityName, String targetStateName, boolean isCollectionState, IMResourceStateMachine targetResourceStateMachine) {
		transitions.add(new IMTransition(targetEntityName, targetStateName, isCollectionState, targetResourceStateMachine));
	}
	
	public List<IMTransition> getTransitions() {
		return transitions;
	}
}
