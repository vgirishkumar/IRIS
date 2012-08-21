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
//		ResourceState live = new ResourceState("FundsTransfer", "live", "/fundstransfer/{Id}", "Id", "self".split(" "));
//		ResourceState unauthorised = new ResourceState("FundsTransfer", "unauthorised", "/fundstransfer/unauthorised/{Id}", "Id", "self".split(" "));
//		ResourceState authorised = new ResourceState("FundsTransfer", "authorised", "/fundstransfer/{Id}/authorise", "Id", "self".split(" "));
//		ResourceState held = new ResourceState("FundsTransfer", "held", "/fundstransfer/{Id}/hold", "Id", "self".split(" "));
//		ResourceState reversed = new ResourceState("FundsTransfer", "reversed", "/fundstransfer/{Id}/reverse", "Id", "self".split(" "));
//		ResourceState restored = new ResourceState("FundsTransfer", "restored", "/fundstransfer/{Id}/restore", "Id", "self".split(" "));
//		ResourceState unauthorisedDeleted = new ResourceState("FundsTransfer", "unauthorisedDeleted", "/fundstransfer/{Id}", "Id", "self".split(" "));
//		ResourceState holdDeleted = new ResourceState("FundsTransfer", "holdDeleted", "/fundtransfers/{Id}", "Id", "self".split(" "));
//		ResourceState reverseDeleted = new ResourceState("FundsTransfer", "reverseDeleted", "/fundtransfers/{Id}", "Id", "self".split(" "));
		ResourceState live = new ResourceState("FundsTransfer", "live", "/fundstransfer/{Id}");
		ResourceState unauthorised = new ResourceState("FundsTransfer", "unauthorised", "/fundstransfer/unauthorised/{Id}");
		ResourceState authorised = new ResourceState("FundsTransfer", "authorised", "/fundstransfer/{Id}/authorise");
		ResourceState held = new ResourceState("FundsTransfer", "held", "/fundstransfer/{Id}/hold");
		ResourceState reversed = new ResourceState("FundsTransfer", "reversed", "/fundstransfer/{Id}/reverse");
		ResourceState restored = new ResourceState("FundsTransfer", "restored", "/fundstransfer/{Id}/restore");
		ResourceState unauthorisedDeleted = new ResourceState("FundsTransfer", "unauthorisedDeleted", "/fundstransfer/{Id}");
		ResourceState holdDeleted = new ResourceState("FundsTransfer", "holdDeleted", "/fundtransfers/{Id}");
		ResourceState reverseDeleted = new ResourceState("FundsTransfer", "reverseDeleted", "/fundtransfers/{Id}");
		// ResourceState finalState = new ResourceState(initialState, "end");

		Map<String, String> uriLinkageMap = new HashMap<String, String>();

		// INITIAL State
		uriLinkageMap.clear();
		initialState.addTransitionForEachItem("GET", live, uriLinkageMap);
		initialState.addTransition("POST", newFtState);	

		// NEW State
		uriLinkageMap.clear();
		newFtState.addTransition("PUT", unauthorised, uriLinkageMap);
		// newFtState.addTransition("PUT", held, uriLinkageMap);

		// LIVE State
		uriLinkageMap.clear();
		// live.addTransition("GET", live, uriLinkageMap);
		live.addTransition("PUT", unauthorised, uriLinkageMap);
		live.addTransition("PUT", reversed, uriLinkageMap);

		// UNAUTHORISED State
		uriLinkageMap.clear();
		unauthorised.addTransition("PUT", unauthorised, uriLinkageMap);
		unauthorised.addTransition("PUT", authorised, uriLinkageMap);
		unauthorised.addTransition("DELETE", unauthorisedDeleted, uriLinkageMap);

		// AUTHORISED State
		uriLinkageMap.clear();
		authorised.addTransition("GET", live, uriLinkageMap);

		// HELD State
		uriLinkageMap.clear();
		held.addTransition("PUT", unauthorised, uriLinkageMap);
		held.addTransition("DELETE", holdDeleted, uriLinkageMap);
		
		// REVERSED State
		uriLinkageMap.clear();
		reversed.addTransition("PUT", unauthorised, uriLinkageMap);
		reversed.addTransition("DELETE", reverseDeleted, uriLinkageMap);
		
		// RESTORED State
		uriLinkageMap.clear();
		restored.addTransition("PUT", unauthorised, uriLinkageMap);
		
		// FINAL State
		uriLinkageMap.clear();
		restored.addTransition("GET", initialState, uriLinkageMap);
	
		return new ResourceStateMachine(initialState);
	}
}
