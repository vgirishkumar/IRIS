package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceStateMachine {
	private final Logger logger = LoggerFactory.getLogger(ResourceStateMachine.class);

	public final String entityName;
	public final ResourceState initial;
	
	// TODO remove, only here to allow getSimpleResourceStateModel to work in Spring
	public ResourceStateMachine() {
		entityName = null;
		initial = null;
	}
	
	public ResourceStateMachine(String entityName, ResourceState initialState) {
		this.entityName = entityName;
		this.initial = initialState;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public ResourceState getInitial() {
		return initial;
	}
			
	public Collection<ResourceState> getStates() {
		List<ResourceState> result = new ArrayList<ResourceState>();
		collectStates(result, initial);
		return result;
	}

	private void collectStates(Collection<ResourceState> result, ResourceState currentState) {
		if (result.contains(currentState)) return;
		result.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.equals(initial)) {
				collectStates(result, next);
			}
		}
		
	}

	/**
	 * Return a map of the all paths (states), and transitions to other states
	 * @return
	 */
	public Map<String, Set<String>> getInteractionMap() {
		Map<String, Set<String>> interactionMap = new HashMap<String, Set<String>>();
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectInteractions(interactionMap, states, initial);
		return interactionMap;
	}
	
	private void collectInteractions(Map<String, Set<String>> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) return;
		states.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			// lookup transition to get to here
			Transition t = currentState.getTransition(next);
			TransitionCommandSpec command = t.getCommand();
			String path = command.getPath();
			
			Set<String> interactions = result.get(path);
			if (interactions == null)
				interactions = new HashSet<String>();
			interactions.add(command.getMethod());
			
			result.put(path, interactions);
			collectInteractions(result, states, next);
		}
		
	}

	/**
	 * For a given resource state, get the valid interactions.
	 * @param state
	 * @return
	 */
	public Set<String> getInteractions(ResourceState state) {
		Set<String> interactions = null;
		if(state != null) {
			assert(getStates().contains(state));
			Map<String, Set<String>> interactionMap = getInteractionMap();
			interactions = interactionMap.get(state.getPath());
		}
		return interactions;
	}
	
	/**
	 * Return a map of all the paths to the various ResourceState's
	 * @return
	 */
	public Map<String, ResourceState> getStateMap() {
		return getStateMap(initial);
	}

	public Map<String, ResourceState> getStateMap(ResourceState begin) {
		if (begin == null)
			begin = initial;
		Map<String, ResourceState> stateMap = new HashMap<String, ResourceState>();
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectStates(stateMap, states, begin);
		return stateMap;
	}

	private void collectStates(Map<String, ResourceState> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) return;
		states.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.isSelfState()) {
				String path = next.getPath();
				
				if (result.get(path) != null)
					logger.debug("Replacing ResourceState[" + path + "] " + result.get(path));
				
				result.put(path, next);
			}
			collectStates(result, states, next);
		}
		
	}

	/**
	 * For a given path, return the resource state.
	 * @param state
	 * @return
	 */
	public ResourceState getState(String path) {
		if (path == null)
			return initial;
		return getStateMap().get(path);
	}

}
