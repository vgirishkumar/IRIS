package com.temenos.interaction.example.note;

import com.temenos.interaction.core.link.ResourceState;

public class Behaviour {

	private final static String ENTITY_NAME = "NOTE";
	
	public ResourceState getCRUDResourceStateModel() {
		
		ResourceState initialState = new ResourceState(ENTITY_NAME, "begin");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists");
		ResourceState finalState = new ResourceState(ENTITY_NAME, "end");
	
		initialState.addTransition("PUT", exists);		
		exists.addTransition("PUT", exists);		
		exists.addTransition("DELETE", finalState);
		return initialState;
	}

}
