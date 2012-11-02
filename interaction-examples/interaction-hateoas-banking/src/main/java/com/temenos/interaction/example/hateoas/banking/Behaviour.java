package com.temenos.interaction.example.hateoas.banking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	public ResourceState getInteractionModel() {

		// this will be the service root
		ResourceState initialState = new ResourceState("home", "initial", createActionSet(new Action("NoopGET", Action.TYPE.VIEW), null), "/");
		ResourceState preferences = new ResourceState("Preferences", "preferences", createActionSet(new Action("GETPreferences", Action.TYPE.VIEW), null), "/preferences");
		
		initialState.addTransition("GET", preferences);
		initialState.addTransition("GET", getFundsTransferInteractionModel());
		initialState.addTransition("GET", getCustomerInteractionModel());
		return initialState;
	}

	public ResourceStateMachine getFundsTransferInteractionModel() {
		CollectionResourceState initialState = new CollectionResourceState("FundsTransfer", "initial", createActionSet(new Action("GETFundTransfers", Action.TYPE.VIEW), null), "/fundtransfers");
		ResourceState newFtState = new ResourceState(initialState, "new", createActionSet(new Action("NoopGET", Action.TYPE.VIEW), new Action("NEWFundTransfer", Action.TYPE.ENTRY)), "/new");
		ResourceState exists = new ResourceState("FundsTransfer", "exists", createActionSet(new Action("GETFundTransfer", Action.TYPE.VIEW), new Action("PUTFundTransfer", Action.TYPE.ENTRY)), "/fundtransfers/{id}", "id", "self".split(" "));
		ResourceState finalState = new ResourceState(exists, "end", createActionSet(new Action("NoopGET", Action.TYPE.VIEW), null));

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

	public ResourceStateMachine getCustomerInteractionModel() {
		CollectionResourceState initialState = new CollectionResourceState("Customer", "initial", createActionSet(new Action("GETCustomers", Action.TYPE.VIEW), null), "/customers");
		ResourceState exists = new ResourceState("Customer", "exists", createActionSet(new Action("GETCustomer", Action.TYPE.VIEW), new Action("PUTCustomer", Action.TYPE.ENTRY)), "/customers/{id}", "id", "self".split(" "));
		ResourceState finalState = new ResourceState(exists, "end", createActionSet(new Action("NoopGET", Action.TYPE.VIEW), null));
		
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "name");
		initialState.addTransitionForEachItem("GET", exists, uriLinkageMap);		

		exists.addTransition("PUT", exists, uriLinkageMap);		
		exists.addTransition("DELETE", finalState, uriLinkageMap);
		return new ResourceStateMachine(initialState);
	}
	
	private Set<Action> createActionSet(Action view, Action entry) {
		Set<Action> actions = new HashSet<Action>();
		if (view != null)
			actions.add(view);
		if (entry != null)
			actions.add(entry);
		return actions;
	}
}
