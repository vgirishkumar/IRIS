package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceStateMachine {

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
		if (states.contains(currentState)) return;
		states.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
//			if (!next.equals(initial)) {
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
//			}
		}
		
	}

	/**
	 * For a given resource state, get the valid interactions.
	 * @param state
	 * @return
	 */
	public Set<String> getInteractions(ResourceState state) {
		assert(getStates().contains(state));
		Map<String, Set<String>> interactionMap = getInteractionMap();
		Set<String> interactions = interactionMap.get(state.getPath());
		return interactions;
	}
	
	public ResourceState getSimpleResourceStateModel() {
		ResourceState initialState = new ResourceState("begin", "/{id}");
		ResourceState finalState = new ResourceState("end", "/{id}");
	
		initialState.addTransition("PUT", initialState);		
		initialState.addTransition("DELETE", finalState);
		return initialState;
	}

}
