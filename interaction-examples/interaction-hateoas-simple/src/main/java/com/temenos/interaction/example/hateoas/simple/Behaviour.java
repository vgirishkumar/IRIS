package com.temenos.interaction.example.hateoas.simple;

import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;

public class Behaviour {

	public ResourceState getInteractionModel() {
		// this will be the service root
		ResourceState initialState = new ResourceState("", "initial", "");
		
		ResourceState profile = new ResourceState("Profile", "profile", "/profile");
		ResourceState preferences = new ResourceState("Preferences", "preferences", "/preferences");
		
		initialState.addTransition("GET", new ResourceStateMachine(profile));
		initialState.addTransition("GET", new ResourceStateMachine(preferences));
		return initialState;
	}

}
