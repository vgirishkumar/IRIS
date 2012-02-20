package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceState {

	private final String name;
	private final String path;
	private Map<TransitionCommandSpec, Transition> transitions = new HashMap<TransitionCommandSpec, Transition>();

	/**
	 * Construct a 'self' ResourceState.  A transition to one's self will not create a new resource.
	 * @param name
	 */
	public ResourceState(String name) {
		assert(name != null);
		this.name = name;
		this.path = null;
	}

	/**
	 * Construct a substate ResourceState.  A transition to a substate state will create a new resource.
	 * @param name
	 */
	public ResourceState(String name, String path) {
		assert(name != null);
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public boolean isSelfState() {
		return (path == null);
	}
	
	/**
	 * Normal transitions transition state to another state.
	 * @param httpMethod
	 * @param targetState
	 */
	public void addTransition(String httpMethod, ResourceState targetState) {
		assert null != targetState;
		String resourcePath = targetState.getPath();
		// a destructive command acts on this state, a constructive command acts on the target state
		// TODO define set of destructive methods
		if (httpMethod.equals("DELETE")) {
			resourcePath = getPath();
		}
		TransitionCommandSpec commandSpec = new TransitionCommandSpec(httpMethod, resourcePath);
		transitions.put(commandSpec, new Transition(this, commandSpec, targetState));
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
	    	((path == null && otherState.path == null) || (path != null && path.equals(otherState.path))) &&
	    	transitions.equals(otherState.transitions);
	}
	
	public int hashCode() {
		// TODO proper implementation of hashCode, important as we intend to use the in our DSL validation
		return name.hashCode() +
			(path != null ? path.hashCode() : 0) +
			transitions.hashCode();
	}
	
	public String toString() {
		return name;
	}
}
