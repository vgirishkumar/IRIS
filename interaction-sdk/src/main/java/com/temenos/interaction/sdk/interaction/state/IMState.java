package com.temenos.interaction.sdk.interaction.state;

import java.util.ArrayList;
import java.util.List;

import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.transition.IMCollectionStateTransition;
import com.temenos.interaction.sdk.interaction.transition.IMEntityStateTransition;
import com.temenos.interaction.sdk.interaction.transition.IMStateTransition;
import com.temenos.interaction.sdk.interaction.transition.IMTransition;

/**
 * This class holds information about a resource state
 */
public abstract class IMState {

	private String name;										//Name
	private String path;										//Path
	private String view;										//View
	private List<IMTransition> transitions = new ArrayList<IMTransition>(); 
	
	/**
	 * Construct a new resource state 
	 * @param name resource state name
	 * @param path URI path associated to this resource state
	 */
	public IMState(String name, String path, String view) {
		this.name = name;
		this.path = path;
		this.view = view;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getView() {
		return view;
	}
	
	public void setView(String view) {
		this.view = view;
	}

	/**
	 * Add a transition to pseudo state
	 * @param title				Transition label
	 * @param targetState		Target state
	 * @param method			HTTP command
	 * @param boundToCollection Bound to collection state
	 */
	public void addTransitionToPseudoState(String title, IMState targetState, String method, boolean boundToCollection) {
		this.addTransition(title, targetState, method, false, boundToCollection);
	}
	
	/**
	 * Add a transition to another resource state
	 * @param title				Transition label
	 * @param targetState		Target state
	 * @param method			HTTP command
	 * @param boundToCollection Transition is bound to the collection resource
	 */
	public void addTransition(String title, IMState targetState, String method, boolean boundToCollection) {
		this.addTransition(title, targetState, method, false, boundToCollection);
	}
	
	/**
	 * Add a transition to a collection state of a resource state machine
	 * @param title Transition label
	 * @param targetResourceStateMachine Target resource state machine
	 * @param targetState Target state
	 * @param method HTTP command
	 * @param filter Filter expression on collection
	 */
	public void addTransitionToCollectionState(String title, IMResourceStateMachine targetResourceStateMachine, IMState targetState, String method, String filter) {
		transitions.add(new IMCollectionStateTransition(targetResourceStateMachine, targetState, targetState.getName(), filter, title, method));
	}
	
	/**
	 * Add a transition to an entity state of a resource state machine
	 * @param title
	 * @param targetResourceStateMachine
	 * @param targetState
	 * @param method
	 * @param linkProperty
	 */
	public void addTransitionToEntityState(String title, IMResourceStateMachine targetResourceStateMachine, IMState targetState, String method, String linkProperty) {
		transitions.add(new IMEntityStateTransition(targetResourceStateMachine, targetState, linkProperty, title, method));
	}

	/* Add a transition  
	 * @param title Transition label
	 * @param targetState Target state
	 * @param method HTTP command
	 * @param auto true if this is an auto transition
	 * @param boundToCollection true if this transition is to be bound to the collection state of an entity
	 */
	protected void addTransition(String title, IMState targetState, String method, boolean auto, boolean boundToCollection) {
		transitions.add(new IMStateTransition(title, targetState, method, auto, boundToCollection));
	}
	
	/**
	 * Return a list of outgoing transitions
	 * @return
	 */
	public List<IMTransition> getTransitions() {
		return transitions;
	}
	
	public boolean equals(Object other) {
	    if ( this == other ) return true;
	    if ( !(other instanceof IMState) ) return false;
	    IMState otherState = (IMState) other;
	    return name.equals(otherState.name);
	}
}
