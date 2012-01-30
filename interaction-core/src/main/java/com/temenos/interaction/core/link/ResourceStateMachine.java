package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceStateMachine {

	public final ResourceState initial;
	
	public ResourceStateMachine(ResourceState initialState) {
		this.initial = initialState;
	}
	
	public ResourceState getInitial() {
		return initial;
	}
	
	public Collection<ResourceState> getStates() {
		List<ResourceState> result = new ArrayList<ResourceState>();
		collectStates(result, initial);
		return result;
	}
	
	private void collectStates(Collection<ResourceState> result, ResourceState s) {
		if (result.contains(s)) return;
		result.add(s);
		for (ResourceState next : s.getAllTargets())
			collectStates(result, next);
	}
	
}
