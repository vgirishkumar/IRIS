package com.temenos.interaction.example.hateoas.banking;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

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
		CollectionResourceState initialState = new CollectionResourceState("FundsTransfer", "initial", "/fundstransfer");
		ResourceState newFtState = new ResourceState(initialState, "new", "/new");
		ResourceState viewed = new ResourceState("FundsTransfer", "view", "/fundstransfer/{Id}", "Id");
		ResourceState unauthorised = new ResourceState("FundsTransfer", "unauthorised", "/fundstransfer/{Id}/unauthorise", "Id");
		ResourceState authorised = new ResourceState("FundsTransfer", "authorised", "/fundstransfer/{Id}/authorise", "Id");
		ResourceState held = new ResourceState("FundsTransfer", "held", "/fundstransfer/{Id}/hold", "Id");
		ResourceState reversed = new ResourceState("FundsTransfer", "reversed", "/fundstransfer/{Id}/reverse", "Id");
		ResourceState restored = new ResourceState("FundsTransfer", "restored", "/fundstransfer/{Id}/restore", "Id");
		ResourceState deleted = new ResourceState("FundsTransfer", "deleted", "/fundstransfer/{Id}/delete", "Id");


		Map<String, String> uriLinkageMap = new HashMap<String, String>();

		// INITIAL State
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "Id");
		initialState.addTransitionForEachItem("GET", viewed, uriLinkageMap);
		initialState.addTransition("POST", newFtState);	

		// NEW State
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "Id");
		newFtState.addTransition("PUT", unauthorised, uriLinkageMap);
		// newFtState.addTransition("PUT", held, uriLinkageMap);

		// VIEWED State
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "Id");
		viewed.addTransition("GET", viewed, uriLinkageMap);
		viewed.addTransition("PUT", unauthorised, uriLinkageMap);
		viewed.addTransition("PUT", authorised, uriLinkageMap);
		viewed.addTransition("PUT", unauthorised, uriLinkageMap);
		viewed.addTransition("PUT", held, uriLinkageMap);
		viewed.addTransition("PUT", reversed, uriLinkageMap);
		viewed.addTransition("PUT", restored, uriLinkageMap);
		viewed.addTransition("DELETE", deleted, uriLinkageMap);
		
		return new ResourceStateMachine(initialState);
	}
}
