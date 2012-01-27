package com.temenos.interaction.core.link;

import java.util.HashSet;
import java.util.Set;

import com.temenos.interaction.core.state.ResourceInteractionModel;

public class ResourceState {

	public ResourceInteractionModel resource;
	public Set<Transition> transitions = new HashSet<Transition>();
	
	public ResourceState(ResourceInteractionModel resource) {
		this.resource = resource;
	}
	
	public void addTransition(Transition t) {
		transitions.add(t);
	}
	
	
}
