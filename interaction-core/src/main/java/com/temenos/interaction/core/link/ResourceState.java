package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceState {

	private final String name;
	private Map<String, Transition> transitions = new HashMap<String, Transition>();
	private Set<String> interactions = new HashSet<String>();

	public ResourceState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addTransition(CommandSpec commandSpec, ResourceState targetState) {
		assert null != targetState;
		targetState.addInteraction(commandSpec.getMethod());
		transitions.put(commandSpec.getName(), new Transition(this, commandSpec, targetState));
	}
	
	/**
	 * Get the transition to the supplied target state.
	 * @param targetState
	 * @return
	 */
	public Transition getTransition(ResourceState targetState) {
		Transition foundTransition = null;
		for (Transition t : transitions.values()) {
			if (t.getTarget().equals(targetState)) {
				assert(foundTransition == null);
				foundTransition = t;
			}
		}
		return foundTransition;
	}

	public void addInteraction(String method) {
		interactions.add(method);
	}
	
	public Set<String> getInteractions() {
		return interactions;
	}

	public Collection<ResourceState> getAllTargets() {
		List<ResourceState> result = new ArrayList<ResourceState>();
		for (Transition t : transitions.values()) result.add(t.getTarget());
		return result;
	}
	
	/**
	 * A final state has no transitions.
	 * @return
	 */
	public boolean isFinalState() {
		return transitions.isEmpty();
	}
	
	public boolean equals(Object other) {
		//check for self-comparison
	    if ( this == other ) return true;
	    if ( !(other instanceof ResourceState) ) return false;
	    ResourceState otherState = (ResourceState) other;
	    return name.equals(otherState.name) &&
	    	transitions.equals(otherState.transitions);
	}
	
	public int hashCode() {
		// TODO proper implementation of hashCode, important as we intend to use the in our DSL validation
		return name.hashCode() +
			transitions.hashCode();
	}
}
