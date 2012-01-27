package com.temenos.interaction.core.link;

import java.util.HashSet;
import java.util.Set;

import com.temenos.interaction.core.state.ResourceInteractionModel;

public class ResourceStateMachine {

	public Set<ResourceState> states = new HashSet<ResourceState>();
	
	public ResourceStateMachine(Set<ResourceState> states) {
		this.states = states;
	}
	
}
