package com.temenos.interaction.example.hateoas.banking;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.link.CollectionResourceState;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;

public class Behaviour {

	public ResourceState getInteractionModel() {
		// this will be the service root
		ResourceState initialState = new ResourceState("home", "initial", "/");
		ResourceState preferences = new ResourceState("Preferences", "preferences", "/preferences");
		
		initialState.addTransition("GET", preferences);
		initialState.addTransition("GET", getFundsTransferInteractionModel());
		return initialState;
	}

	public ResourceStateMachine getFundsTransferInteractionModel() {
		CollectionResourceState initialState = new CollectionResourceState("FundsTransfer", "initial", "/fundtransfers");
		ResourceState newFtState = new ResourceState("FundsTransfer", "new", "/fundtransfers/new");
		ResourceState exists = new ResourceState("FundsTransfer", "exists", "/fundtransfers/{id}", "id");
		ResourceState finalState = new ResourceState(initialState, "end");

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		initialState.addTransition("POST", newFtState);		

		uriLinkageMap.clear();
		newFtState.addTransition("PUT", exists, uriLinkageMap);
		//newFtState.addTransition("GET", exists, uriLinkageMap);
		
		uriLinkageMap.clear();
		initialState.addTransitionForEachItem("GET", exists, uriLinkageMap);		

		exists.addTransition("PUT", exists, uriLinkageMap);		
		exists.addTransition("DELETE", finalState, uriLinkageMap);
		return new ResourceStateMachine(initialState);
	}
	
}
