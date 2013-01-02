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
	private String pathParametersTemplate;								//Path parameters defined in URI template
	private List<IMTransition> transitions = new ArrayList<IMTransition>();		//Transitions
	
	
	public IMResourceStateMachine(String entityName, String collectionStateName, String entityStateName, String mappedEntityProperty, String pathParametersTemplate) {
		this.entityName = entityName;
		this.collectionStateName = collectionStateName;
		this.entityStateName = entityStateName;
		this.mappedEntityProperty = mappedEntityProperty;
		this.pathParametersTemplate = pathParametersTemplate;
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
	
	public String getPathParametersTemplate() {
		return pathParametersTemplate;
	}
	
	/**
	 * Add a transition to a collection resource state
	 * @param targetStateName target state 
	 * @param targetEntityName name of entity associated to target state
	 * @param targetResourceStateMachine target resource state machine
	 */
	public void addTransitionToCollectionResource(String targetStateName, String targetEntityName, IMResourceStateMachine targetResourceStateMachine, String queryParameters) {
		addTransition(targetEntityName, targetStateName, targetStateName, true, "", targetResourceStateMachine, queryParameters);
	}

	/**
	 * Add a transition to an entity resource state
	 * @param targetStateName target state 
	 * @param targetEntityName name of entity associated to target state
	 * @param targetResourceStateMachine target resource state machine
	 */
	public void addTransitionToEntityResource(String targetStateName, String linkProperty, String targetEntityName, IMResourceStateMachine targetResourceStateMachine) {
		addTransition(targetEntityName, linkProperty, targetStateName, false, "", targetResourceStateMachine, null);
	}
	
	/**
	 * Add a transition to another state
	 * @param targetEntityName Entity associated to target RSM
	 * @param linkProperty Navigation property linking to the target RSM
	 * @param targetStateName Resource state of source RSM to which we want to move 
	 * @param isCollectionState Specifies if target resource state is a collection
	 * @param reciprocalLinkState Resource state of target RSM which leads us back to the source RSM. Leave null or empty to avoid reciprocal links.
	 * @param targetResourceStateMachine Target RSM
	 */
	public void addTransition(String targetEntityName, String linkProperty, String targetStateName, boolean isCollectionState, String reciprocalLinkState, IMResourceStateMachine targetResourceStateMachine) {
		addTransition(targetEntityName, linkProperty, targetStateName, isCollectionState, reciprocalLinkState, targetResourceStateMachine, null);
	}
	
	/*
	 * Add a transition
	 */
	private void addTransition(String targetEntityName, String linkProperty, String targetStateName, boolean isCollectionState, String reciprocalLinkState, IMResourceStateMachine targetResourceStateMachine, String queryParameters) {
		IMTransition transition = new IMTransition(targetEntityName, linkProperty, targetStateName, isCollectionState, reciprocalLinkState, targetResourceStateMachine, queryParameters != null ? queryParameters : "");
		
		//Workaround - if there are multiple transitions to the same state => create intermediate 'navigation' states 
		for(IMTransition t : transitions) {
			if(t.getTargetResourceStateMachine().getEntityStateName().equals(targetResourceStateMachine.getEntityStateName()) ) {
				t.notUniqueTransition();
				transition.notUniqueTransition();
			}
		}
		
		transitions.add(transition);
	}
	
	public List<IMTransition> getTransitions() {
		return transitions;
	}
}
