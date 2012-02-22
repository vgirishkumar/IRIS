package com.temenos.interaction.example.note;

import com.temenos.interaction.core.link.ResourceState;

public class Behaviour {

	public ResourceState getCRUDResourceStateModel() {
		ResourceState initialState = new ResourceState("begin");
		ResourceState exists = new ResourceState("exists");
		ResourceState finalState = new ResourceState("end");
	
		initialState.addTransition("PUT", exists);		
		exists.addTransition("PUT", exists);		
		exists.addTransition("DELETE", finalState);
		return initialState;
	}

}
