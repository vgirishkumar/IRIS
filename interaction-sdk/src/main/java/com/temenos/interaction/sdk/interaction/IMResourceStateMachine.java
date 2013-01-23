package com.temenos.interaction.sdk.interaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.temenos.interaction.sdk.interaction.state.IMNavigationState;
import com.temenos.interaction.sdk.interaction.state.IMPseudoState;
import com.temenos.interaction.sdk.interaction.state.IMResourceState;
import com.temenos.interaction.sdk.interaction.transition.IMTransition;

/**
 * This class holds information about a resource state machine.
 * An RSM holds information about interactions between different
 * states on an entity.
 */
public class IMResourceStateMachine {

	private String entityName;											//Entity name
	private IMResourceState collectionState;							//Collection state
	private IMResourceState entityState;								//Entity state
	private String mappedEntityProperty;								//Entity property to which the URI template parameter maps to
	private String pathParametersTemplate;								//Path parameters defined in URI template

	private Map<String, IMResourceState> resourceStates = new HashMap<String, IMResourceState>();	//Resource states 
	
	
	public IMResourceStateMachine(String entityName, String collectionStateName, String entityStateName, String mappedEntityProperty, String pathParametersTemplate) {
		this.entityName = entityName;
		this.collectionState = new IMResourceState(collectionStateName, "/" + collectionStateName + "()");
		resourceStates.put(collectionStateName, collectionState);
		this.entityState = new IMResourceState(entityStateName, "/" + collectionStateName + "(" + pathParametersTemplate + ")");
		resourceStates.put(entityStateName, entityState);
		this.mappedEntityProperty = mappedEntityProperty;
		this.pathParametersTemplate = pathParametersTemplate;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public IMResourceState getCollectionState() {
		return collectionState;
	}
	
	public IMResourceState getEntityState() {
		return entityState;
	}
	
	public String getMappedEntityProperty() {
		return mappedEntityProperty;
	}
	
	public String getPathParametersTemplate() {
		return pathParametersTemplate;
	}

	/**
	 * Add a transition to a collection state
	 * @param sourceStateName source state name
	 * @param targetResourceStateMachine Target resource state machine
	 * @param targetStateName target state name
	 * @param filter filter expression on collection
	 * @param title Transition label
	 */
	public void addTransitionToCollectionState(String sourceStateName, IMResourceStateMachine targetResourceStateMachine, String targetStateName, String filter, String title) {
		this.addResourceTransition(sourceStateName, targetResourceStateMachine, targetStateName, "GET", title, targetStateName, true, null, filter);
	}

	/**
	 * Add a transition to an entity state
	 * @param sourceStateName source state name
	 * @param targetResourceStateMachine target resource state machine
	 * @param targetStateName target state name
	 * @param linkProperty linkage property
	 * @param title transition label
	 */
	public void addTransitionToEntityState(String sourceStateName, IMResourceStateMachine targetResourceStateMachine, String targetStateName, String linkProperty, String title) {
		this.addResourceTransition(sourceStateName, targetResourceStateMachine, targetStateName, "GET", title, targetStateName, false, linkProperty, null);
	}

	/* Add a transition to a collection or entity state
	 * @param sourceStateName
	 * @param targetResourceStateMachine
	 * @param targetStateName
	 * @param method
	 * @param title
	 * @param path
	 * @param isToCollectionState
	 * @param linkProperty
	 * @param filter
	 */
	protected void addResourceTransition(String sourceStateName, IMResourceStateMachine targetResourceStateMachine, String targetStateName, String method, String title, String path, boolean isToCollectionState, String linkProperty, String filter) {
		//Create resource states if required
		if(!resourceStates.containsKey(sourceStateName)) {
			resourceStates.put(sourceStateName, new IMResourceState(sourceStateName, path));
		}
		if(!resourceStates.containsKey(targetStateName)) {
			if(isToCollectionState) { 
				resourceStates.put(targetStateName, new IMNavigationState(targetStateName, path, targetResourceStateMachine, true));
			}
			else {
				resourceStates.put(targetStateName, new IMNavigationState(targetStateName, path, targetResourceStateMachine, false));
			}
		}
		
		//Add transition
		IMResourceState sourceState = getResourceState(sourceStateName);
		IMResourceState targetState = getResourceState(targetStateName);
		if(isToCollectionState) {
			sourceState.addTransitionToCollectionState(title, targetResourceStateMachine, targetState, method, filter);
		}
		else {
			sourceState.addTransitionToEntityState(title, targetResourceStateMachine, targetState, method, linkProperty);
		}
	}
	
	/**
	 * Add a transition to a resource state
	 * @param title				Transition label
	 * @param sourceStateName	Source state
	 * @param targetStateName	Target state
	 * @param method			HTTP command
	 * @param action			Action to execute
	 * @param relations			Relations
	 * @param boundToCollection	true if this transition should be bound to a collection state
	 */
	public void addStateTransition(String sourceStateName, String targetStateName, String method, String title, String action, String relations, boolean boundToCollection) {
		this.addStateTransition(sourceStateName, targetStateName, null, method, title, action, relations, false, boundToCollection);
	}
	
	/**
	 * Add a transition to a pseudo state.
	 * A pseudo state is an intermediate state to handle an action
	 * @param title				Transition label
	 * @param sourceStateName	Source state 
	 * @param pseudoStateId		Pseudo state identifier 
	 * @param method			HTTP command
	 * @param action			Action to execute
	 * @param relations			Relations
	 * @param boundToCollection	true if this transition should be bound to a collection state
	 */
	public IMPseudoState addPseudoStateTransition(String sourceStateName, String pseudoStateId, String method, String title, String action, String relations, boolean boundToCollection) {
		this.addStateTransition(sourceStateName, sourceStateName, pseudoStateId, method, title, action, relations, false, boundToCollection);
		return (IMPseudoState) getResourceState(sourceStateName + "_" + pseudoStateId);
	}
	
	/*
	 * Add a transition triggering a state change in the underlying resource manager
	 * @param title				Transition label
	 * @param sourceStateName	Source state
	 * @param targetStateName	Target state
	 * @param pseudoStateId		Pseudo state identifier 
	 * @param method			HTTP command
	 * @param action			Action to execute
	 * @param relations			Relations
	 * @param boundToCollection	true if this transition should be bound to a collection state
	 */
	protected void addStateTransition(String sourceStateName, String targetStateName, String pseudoStateId, String method, String title, String action, String relations, boolean auto, boolean boundToCollection) {
		String path = entityState.getPath();
		if(!(targetStateName.equals(sourceStateName) && targetStateName.equals(entityState.getName()))) {
			path += "/" + targetStateName;
		}
		if(pseudoStateId != null && !pseudoStateId.equals("")) {
			//This is a pseudo state
			targetStateName = targetStateName + "_" + pseudoStateId;
		}
		if(boundToCollection) {
			path = collectionState.getPath();
		}
		
		//Create resources states if required
		if(!resourceStates.containsKey(sourceStateName)) {
			resourceStates.put(sourceStateName, new IMResourceState(sourceStateName, path));
		}
		if(!resourceStates.containsKey(targetStateName)) {
			IMResourceState targetState;
			if(pseudoStateId != null) {
				targetState = new IMPseudoState(targetStateName, path, pseudoStateId, relations, action);				
			}
			else {
				targetState = new IMResourceState(targetStateName, path);				
			}
			resourceStates.put(targetStateName, targetState);
		}
		
		//Add transition
		IMResourceState sourceState = getResourceState(sourceStateName);
		IMResourceState targetState = getResourceState(targetStateName);
		if(targetState instanceof IMPseudoState) {
			sourceState.addTransitionToPseudoState(title, targetState, method, boundToCollection);
		}
		else {
			sourceState.addTransition(title, targetState, method);
		}
	}
	
	/**
	 * Return the outgoing transitions on the specified resource state
	 * @param resourceStateName resource state name
	 * @return
	 */
	public List<IMTransition> getTransitions(String resourceStateName) {
		if(resourceStates.containsKey(resourceStateName)) {
			return resourceStates.get(resourceStateName).getTransitions();
		}
		return null;
	}

	/**
	 * Obtain a list of transitions bound to the collection state resource
	 * @return
	 */
	public List<IMTransition> getCollectionStateTransitions() {
		List<IMTransition> transitions = new ArrayList<IMTransition>();
		transitions.addAll(collectionState.getTransitions());
		transitions.addAll(entityState.getTransitions());		//Collection resources should also have those of the entity state
		return transitions;
	}

	/**
	 * Obtain a list of transitions bound to the entity state resource 
	 * @return
	 */
	public List<IMTransition> getEntityStateTransitions() {
		return entityState.getTransitions();
	}
	
	/**
	 * Obtain a sorted list of resource states
	 * @return resource states
	 */
	public Collection<IMResourceState> getResourceStates() {
		List<IMResourceState> states = new ArrayList<IMResourceState>(resourceStates.values());
		if(states.size() > 0) {
			//Return sorted list of resource states
			Collections.sort(states, new Comparator<IMResourceState>() {
				@Override
				public int compare(final IMResourceState s1, final IMResourceState s2) {
					//Ensure collection and entity states appear first
					if(s1.equals(collectionState) && s2.equals(entityState)) {
						return -1;
					}
					else if(s1.equals(entityState) && s2.equals(collectionState)) {
						return 1;
					}
					else if(s1.equals(collectionState) || s1.equals(entityState)) {
						return -1;
					}
					else if(s2.equals(collectionState) || s2.equals(entityState)) {
						return 1;
					}
					else {
						return s1.getName().compareToIgnoreCase(s2.getName());
					}
				}
			} );
		}		
		return states;
	}
	
	/**
	 * Return a resource state
	 * @param stateName resource state name
	 * @return
	 */
	public IMResourceState getResourceState(String stateName) {
		return resourceStates.get(stateName);
	}
	
}
