package com.temenos.interaction.sdk.interaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds information about a resource state machine
 */
public class IMResourceStateMachine {

	private String entityName;											//Entity name
	private IMResourceState collectionState;							//Collection state
	private IMResourceState entityState;								//Entity state
	private String mappedEntityProperty;								//Entity property to which the URI template parameter maps to
	private String pathParametersTemplate;								//Path parameters defined in URI template
	private List<IMTransition> transitions = new ArrayList<IMTransition>();		//Transition to other resources
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
	
	public String getCollectionStateName() {
		return collectionState.getName();
	}

	public IMResourceState getCollectionState() {
		return collectionState;
	}
	
	public String getEntityStateName() {
		return entityState.getName();
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
	 * Return a list of target resource state machines to which there are transitions
	 * @return target resource state machines
	 */
	public List<IMResourceStateMachine> getTargetResourceStateMachines() {
		List<IMResourceStateMachine> targetRsms = new ArrayList<IMResourceStateMachine>();
		for(IMTransition transition : transitions) {
			if(!targetRsms.contains(transition.getTargetResourceStateMachine()) &&
					transition.isCollectionState()) {
				targetRsms.add(transition.getTargetResourceStateMachine());
			}
		}
		return targetRsms;
	}
	
	/**
	 * Add a transition to a collection resource state
	 * @param targetStateName target state 
	 * @param targetEntityName name of entity associated to target state
	 * @param targetResourceStateMachine target resource state machine
	 */
	public void addTransitionToCollectionResource(String targetStateName, String targetEntityName, IMResourceStateMachine targetResourceStateMachine, String filter, String title) {
		addTransition(targetEntityName, targetStateName, targetStateName, true, "", targetResourceStateMachine, filter, title, null, null, null, false);
	}

	/**
	 * Add a transition to an entity resource state
	 * @param targetStateName target state 
	 * @param targetEntityName name of entity associated to target state
	 * @param targetResourceStateMachine target resource state machine
	 */
	public void addTransitionToEntityResource(String targetStateName, String linkProperty, String targetEntityName, IMResourceStateMachine targetResourceStateMachine) {
		addTransition(targetEntityName, linkProperty, targetStateName, false, "", targetResourceStateMachine, null, null, null, null, null, false);
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
		addTransition(targetEntityName, linkProperty, targetStateName, isCollectionState, reciprocalLinkState, targetResourceStateMachine, null, null, null, null, null, false);
	}
	
	/**
	 * Add a transition to a pseudo state
	 * @param targetEntityName Entity associated to target RSM
	 * @param targetStateName Resource state of source RSM to which we want to move
	 * @param action the command that will be executed when the entity is to be transitioned to this state
	 * @param boundToCollection a flag to control whether the state is of the collection or the entity
	 * @precondition action must be supplied
	 */
	public void addTransition(String targetEntityName, String targetStateName, String method, String action, String relations, boolean boundToCollection) {
		assert(method != null);
		assert(action != null);
		addTransition(targetEntityName, null, targetStateName, false, null, null, null, null, method, action, relations, boundToCollection);
	}

	/**
	 * Add a transition to another state
	 * @param targetEntityName Entity associated to target RSM
	 * @param linkProperty Navigation property linking to the target RSM
	 * @param targetStateName Resource state of source RSM to which we want to move 
	 * @param isCollectionState Specifies if target resource state is a collection
	 * @param reciprocalLinkState Resource state of target RSM which leads us back to the source RSM. Leave null or empty to avoid reciprocal links.
	 * @param targetResourceStateMachine Target RSM
	 * @param filter Filter for transitions to collection states
	 */
	public void addTransition(String targetEntityName, String linkProperty, String targetStateName, boolean isCollectionState, String reciprocalLinkState, IMResourceStateMachine targetResourceStateMachine, String filter, String title) {
		addTransition(targetEntityName, linkProperty, targetStateName, isCollectionState, reciprocalLinkState, targetResourceStateMachine, filter, title, null, null, null, false);
	}

	protected void addTransition(String targetEntityName, String linkProperty, String targetStateName, boolean isCollectionState, String reciprocalLinkState, IMResourceStateMachine targetResourceStateMachine, String filter, String title, String method, String action, String relations, boolean boundToCollection) {
		IMTransition transition = new IMTransition(targetEntityName, 
				linkProperty, 
				targetStateName, 
				isCollectionState, 
				reciprocalLinkState, 
				targetResourceStateMachine, 
				filter != null ? filter : "",
				title,
				method,
				action,
				relations,
				boundToCollection);
		
		//Workaround - if there are multiple transitions to the same state => create intermediate 'navigation' states 
		for(IMTransition t : transitions) {
			if((t.getTargetResourceStateMachine() != null && targetResourceStateMachine != null)
					&& t.getTargetResourceStateMachine().getEntityStateName().equals(targetResourceStateMachine.getEntityStateName()) ) {
				t.notUniqueTransition();
				transition.notUniqueTransition();
			}
		}

		transitions.add(transition);
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
		String path = entityState.getPath() + "/" + targetStateName;
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
	
	public List<IMTransition> getTransitions() {
		return transitions;
	}

	/**
	 * Return the outgoing transitions on the specified resource state
	 * @param resourceStateName resource state name
	 * @return
	 */
	public List<IMStateTransition> getTransitions(String resourceStateName) {
		if(resourceStates.containsKey(resourceStateName)) {
			return resourceStates.get(resourceStateName).getTransitions();
		}
		return null;
	}

	/**
	 * Obtain a list of transitions bound to the collection state resource
	 * @return
	 */
	public List<IMStateTransition> getTransitionsBoundToCollectionState() {
		return collectionState.getTransitions();
	}

	/**
	 * Obtain a list of transitions bound to the entity state resource 
	 * @return
	 */
	public List<IMStateTransition> getTransitionsBoundToEntityState() {
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
					return s1.getName().compareTo(s2.getName());
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
