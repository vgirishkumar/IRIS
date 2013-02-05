package com.temenos.interaction.sdk.interaction.state;

import java.util.HashSet;
import java.util.Set;

/**
 * This class holds information about a resource state
 */
public class IMPseudoState extends IMState {

	private Set<String> actions = new HashSet<String>();		//Actions
	private String pseudoStateId = null;						//Pseudo state Id or null if this is not a pseudo state
	private String relations = null;							//Link relations to this state

	public IMPseudoState(String name, String path, String view, String pseudoStateId, String relations, String action) {
		super(name, path, view);
		this.pseudoStateId = pseudoStateId;
		this.relations = relations;
		if(action != null) {
			this.actions.add(action);
		}
	}
	
	/**
	 * Returns whether this is a pseudo state.
	 * A pseudo state is an intermediate state to handle an action
	 * @return true or false
	 */
	public boolean isPseudoState() {
		return pseudoStateId != null;
	}
	
	public String getRelations() {
		return relations;
	}
	
	public boolean hasRelations() {
		return relations != null;		
	}
	
	/**
	 * Add a transition triggering a state change in the underlying resource manager
	 * @param targetState		Target state
	 * @param method			HTTP command
	 */
	public void addAutoTransition(IMState targetState, String method) {
		this.addTransition(null, targetState, method, true, false);
	}
	
	public Set<String> getActions() {
		return actions;
	}
}
