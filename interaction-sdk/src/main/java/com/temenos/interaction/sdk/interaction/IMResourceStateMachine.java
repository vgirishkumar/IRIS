package com.temenos.interaction.sdk.interaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.temenos.interaction.sdk.interaction.state.IMCollectionState;
import com.temenos.interaction.sdk.interaction.state.IMEntityState;
import com.temenos.interaction.sdk.interaction.state.IMNavigationState;
import com.temenos.interaction.sdk.interaction.state.IMPseudoState;
import com.temenos.interaction.sdk.interaction.state.IMState;
import com.temenos.interaction.sdk.interaction.transition.IMTransition;

/**
 * This class holds information about a resource state machine.
 * An RSM holds information about interactions between different
 * states on an entity.
 */
public class IMResourceStateMachine {

	private String entityName;											//Entity name
	private IMState collectionState;							//Collection state
	private IMState entityState;								//Entity state
	private String mappedEntityProperty;								//Entity property to which the URI template parameter maps to
	private String pathParametersTemplate;								//Path parameters defined in URI template

	private Map<String, IMState> resourceStates = new HashMap<String, IMState>();	//Resource states 
	
	
	public IMResourceStateMachine(String entityName, String collectionStateName, String entityStateName, String mappedEntityProperty, String pathParametersTemplate) {
		this.entityName = entityName;
		this.entityState = new IMEntityState(entityStateName, "/" + collectionStateName + "(" + pathParametersTemplate + ")");
		resourceStates.put(entityStateName, entityState);
		this.collectionState = new IMCollectionState(collectionStateName, "/" + collectionStateName + "()", (IMEntityState) entityState);
		resourceStates.put(collectionStateName, collectionState);
		this.mappedEntityProperty = mappedEntityProperty;
		this.pathParametersTemplate = pathParametersTemplate;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public IMState getCollectionState() {
		return collectionState;
	}
	
	public IMState getEntityState() {
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
			resourceStates.put(sourceStateName, new IMEntityState(sourceStateName, path));
		}
		if(!resourceStates.containsKey(targetStateName)) {
			resourceStates.put(targetStateName, new IMNavigationState(targetStateName, path, targetResourceStateMachine, isToCollectionState));
		}
		
		//Add transition
		IMState sourceState = getResourceState(sourceStateName);
		IMState targetState = getResourceState(targetStateName);
		if(isToCollectionState) {
			sourceState.addTransitionToCollectionState(title, targetResourceStateMachine, targetState, method, filter);
		}
		else {
			sourceState.addTransitionToEntityState(title, targetResourceStateMachine, targetState, method, linkProperty);
		}
	}

	/**
	 * Add a new resource state and it's associated collection state. 
	 * @param stateId State identifier
	 * @param title Label representing this resource
	 */
	public void addCollectionAndEntityState(String stateId, String title) {
		//Add collection state
		addStateTransition(collectionState.getName(), collectionState.getName() + "_" + stateId, "GET", stateId, title, null, null);

		//Add entity state
		addStateTransition(collectionState.getName() + "_" + stateId, entityState.getName() + "_" + stateId, "GET", stateId, null, null, null);
	}
	
	/**
	 * Add a transition to a resource state
	 * @param title				Transition label
	 * @param sourceStateName	Source state
	 * @param targetStateName	Target state
	 * @param method			HTTP command
	 * @param action			Action to execute
	 * @param relations			Relations
	 */
	public void addStateTransition(String sourceStateName, String targetStateName, String method, String stateId, String title, String action, String relations) {
		boolean boundToCollection = sourceStateName.equals(collectionState.getName());	//rsm's collection state can only have transition to other collection states or to the rsm's entity state
		this.addStateTransition(sourceStateName, targetStateName, null, method, stateId, title, action, relations, false, boundToCollection);
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
		this.addStateTransition(sourceStateName, sourceStateName, pseudoStateId, method, null, title, action, relations, false, boundToCollection);
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
	 * @param boundToCollection	true if the target state should be bound to a collection state
	 */
	protected void addStateTransition(String sourceStateName, String targetStateName, String pseudoStateId, String method, String stateId, String title, String action, String relations, boolean auto, boolean boundToCollection) {
		String path;
		if(pseudoStateId != null && !pseudoStateId.equals("")) {
			//This is a pseudo state
			targetStateName = targetStateName + "_" + pseudoStateId;
			path = getResourceState(sourceStateName).getPath();		//Source states must be created before pseudo states
		}
		else {
			//This is a resource state
			path = sourceStateName.equals(collectionState.getName()) ? collectionState.getPath() : entityState.getPath();
			if(!(targetStateName.equals(sourceStateName) && (targetStateName.equals(entityState.getName()) || targetStateName.equals(collectionState.getName())))) {
				path += "/" + stateId;
			}
			//Create source state if necessary
			if(!resourceStates.containsKey(sourceStateName)) {
				resourceStates.put(sourceStateName, new IMEntityState(sourceStateName, path));
			}
		}
		
		//Create target state if required
		if(!resourceStates.containsKey(targetStateName)) {
			IMState targetState;
			if(pseudoStateId != null) {
				targetState = new IMPseudoState(targetStateName, path, pseudoStateId, relations, action);				
			}
			else if(boundToCollection) {
				//Create collection state (and entity state if required)
				String entityStateName = entityState.getName() + "_" + stateId;
				if(!resourceStates.containsKey(entityStateName)) {
					resourceStates.put(entityStateName, new IMEntityState(entityStateName, entityState.getPath() + "/" + stateId));
				}
				targetState = new IMCollectionState(targetStateName, path, (IMEntityState) getResourceState(entityStateName));				
			}
			else {
				targetState = new IMEntityState(targetStateName, path);				
			}
			resourceStates.put(targetStateName, targetState);
		}
		
		//Add transition
		IMState sourceState = getResourceState(sourceStateName);
		IMState targetState = getResourceState(targetStateName);
		if(targetState instanceof IMPseudoState) {
			sourceState.addTransitionToPseudoState(title, targetState, method, boundToCollection);
		}
		else {
			sourceState.addTransition(title, targetState, method, boundToCollection);
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
	public Collection<IMState> getResourceStates() {
		List<IMState> states = new ArrayList<IMState>(resourceStates.values());
		if(states.size() > 0) {
			//Return sorted list of resource states
			Collections.sort(states, new Comparator<IMState>() {
				@Override
				public int compare(final IMState s1, final IMState s2) {
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
	 * @return state
	 */
	public IMState getResourceState(String stateName) {
		return resourceStates.get(stateName);
	}
	
	/**
	 * Return a pseudo state
	 * @param sourceStateName State from which this pseudo state is accesible
	 * @param pseudoStateId Pseudo state id
	 * @return state
	 */
	public IMPseudoState getPseudoState(String sourceStateName, String pseudoStateId) {
		return (IMPseudoState) getResourceState(sourceStateName + "_" + pseudoStateId);
	}
	
}
