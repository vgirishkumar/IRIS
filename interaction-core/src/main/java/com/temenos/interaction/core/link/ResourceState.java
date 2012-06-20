package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceState implements Comparable<ResourceState> {

	/* the name of the entity which this is a state of */
	private final String entityName;
	/* the name for this state */
	private final String name;
	/* the path to the create the resource which represents this state of the entity */
	private final String path;
	/* the path parameter to use as the resource identifier */
	private final String pathIdParameter;
	/* a child state of the same entity */
	private final boolean selfState;
	/* is an intial state */
	private boolean initial;
	private Map<TransitionCommandSpec, Transition> transitions = new HashMap<TransitionCommandSpec, Transition>();

	
	/**
	 * Construct a 'self' ResourceState.  A transition to one's self will not create a new resource.
	 * @param entityName the name of the entity that this object is a state of
	 * @param name this states name
	 * @param path the uri to this state
	 */
	public ResourceState(ResourceState parent, String name) {
		this(parent.getEntityName(), name, parent.getPath(), null, true);
	}

	/**
	 * Construct a substate ResourceState.  A transition to a substate state will create a new resource.
	 * @param entityName the name of the entity that this object is a state of
	 * @param name this states name
	 * @param path the uri to this state
	 */
	public ResourceState(String entityName, String name, String path) {
		this(entityName, name, path, null, false);
	}

	/**
	 * Construct a substate ResourceState.  A transition to a substate state will create a new resource.
	 * @param entityName the name of the entity that this object is a state of
	 * @param name this states name
	 * @param path the uri to this state
	 * @param pathIdParameter override the default {id} path parameter and use the value instead
	 */
	public ResourceState(String entityName, String name, String path, String pathIdParameter) {
		this(entityName, name, path, pathIdParameter, false);
	}

	private ResourceState(String entityName, String name, String path, String pathIdParameter, boolean selfState) {
		assert(name != null);
		this.entityName = entityName;
		this.name = name;
		this.path = path;
		this.pathIdParameter = pathIdParameter;
		this.initial = false;
		this.selfState = selfState;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return entityName + "." + name;
	}

	public String getPath() {
		return path;
	}

	public String getPathIdParameter() {
		return pathIdParameter;
	}

	public boolean isSelfState() {
		return selfState;
	}
	
	public boolean isInitial() {
		return initial;
	}
	
	public void setInitial(boolean flag) {
		initial = flag;
	}
	
	/**
	 * Return the transition to get to this state.
	 * @return
	 */	
	public Transition getSelfTransition() {
		return new Transition(this, new TransitionCommandSpec("GET", getPath()), this);
	}
	
	/**
	 * Normal transitions, transition this entities state to another state.
	 * @param httpMethod
	 * @param targetState
	 */
	public void addTransition(String httpMethod, ResourceState targetState) {
		addTransition(httpMethod, targetState, null);
	}
	
	public void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap) {
		String resourcePath = targetState.getPath();
		// a destructive command acts on this state, a constructive command acts on the target state
		// TODO define set of destructive methods
		if (httpMethod.equals("DELETE")) {
			resourcePath = getPath();
		}
		addTransition(httpMethod, targetState, uriLinkageMap, resourcePath, false);
	}
	
	public void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, String resourcePath, boolean forEach) {
		assert null != targetState;
//		if (resourcePath == null)
//			resourcePath = targetState.getPath();
		// replace uri elements with linkage entity element name
		if (uriLinkageMap != null) {
			for (String templateElement : uriLinkageMap.keySet()) {
				resourcePath = resourcePath.replaceAll("\\{" + templateElement + "\\}", "\\{" + uriLinkageMap.get(templateElement) + "\\}");
			}
		}
		TransitionCommandSpec commandSpec = new TransitionCommandSpec(httpMethod, resourcePath, forEach);
		transitions.put(commandSpec, new Transition(this, commandSpec, targetState));
	}

	/**
	 * Add transition to another resource interaction model.
	 * @param httpMethod
	 * @param resourceStateModel
	 */
	public void addTransition(String httpMethod, ResourceStateMachine resourceStateModel) {
		assert resourceStateModel != null;
		TransitionCommandSpec commandSpec = new TransitionCommandSpec(httpMethod, resourceStateModel.getInitial().getPath());
		transitions.put(commandSpec, new Transition(this, commandSpec, resourceStateModel.getInitial()));
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
				assert(foundTransition == null);  // transition must be defined more than once
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
	    return entityName.equals(otherState.entityName) &&
	    	name.equals(otherState.name) &&
	    	((path == null && otherState.path == null) || (path != null && path.equals(otherState.path))) &&
	    	transitions.equals(otherState.transitions);
	}
	
	public int hashCode() {
		// TODO proper implementation of hashCode, important as we intend to use the in our DSL validation
		return entityName.hashCode() +
			name.hashCode() +
			(path != null ? path.hashCode() : 0) +
			transitions.hashCode();
	}
	
	public String toString() {
		return entityName + "." + name;
	}

	@Override
	public int compareTo(ResourceState other) {
	    if ( this == other ) return 0;
		return other.getId().compareTo(getId());
	}
}
