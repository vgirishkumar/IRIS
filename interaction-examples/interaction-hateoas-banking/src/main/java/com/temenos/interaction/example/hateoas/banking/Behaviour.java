package com.temenos.interaction.example.hateoas.banking;

/*
 * #%L
 * interaction-example-hateoas-banking
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	public ResourceState getInteractionModel() {

		// this will be the service root
		ResourceState initialState = new ResourceState("home", "initial", createActionList(new Action("NoopGET", Action.TYPE.VIEW), null), "/");
		ResourceState preferences = new ResourceState("Preferences", "preferences", createActionList(new Action("GETPreferences", Action.TYPE.VIEW), null), "/preferences");
		
		initialState.addTransition("GET", preferences);
		initialState.addTransition("GET", getFundsTransferInteractionModel());
		initialState.addTransition("GET", getCustomerInteractionModel());
		return initialState;
	}

	public ResourceStateMachine getFundsTransferInteractionModel() {
		CollectionResourceState fundstransfers = new CollectionResourceState("FundsTransfer", "fundstransfers", createActionList(new Action("GETFundTransfers", Action.TYPE.VIEW), null), "/fundtransfers");
		ResourceState newFtState = new ResourceState(fundstransfers, "new", createActionList(new Action("NoopGET", Action.TYPE.VIEW), new Action("NEWFundTransfer", Action.TYPE.ENTRY)), "/new");
		ResourceState fundstransfer = new ResourceState("FundsTransfer", "fundstransfer", createActionList(new Action("GETFundTransfer", Action.TYPE.VIEW), new Action("PUTFundTransfer", Action.TYPE.ENTRY)), "/fundtransfers/{id}", "id", "self".split(" "));
		ResourceState finalState = new ResourceState(fundstransfer, "end", createActionList(new Action("NoopGET", Action.TYPE.VIEW), null));

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		fundstransfers.addTransition("POST", newFtState);		

		uriLinkageMap.clear();
		newFtState.addTransition("PUT", fundstransfer, uriLinkageMap);
		//newFtState.addTransition("GET", exists, uriLinkageMap);
		
		uriLinkageMap.clear();
		fundstransfers.addTransitionForEachItem("GET", fundstransfer, uriLinkageMap);		

		fundstransfer.addTransition("PUT", fundstransfer, uriLinkageMap);		
		fundstransfer.addTransition("DELETE", finalState, uriLinkageMap);
		return new ResourceStateMachine(fundstransfers);
	}

	public ResourceStateMachine getCustomerInteractionModel() {
		CollectionResourceState customers = new CollectionResourceState("Customer", "customers", createActionList(new Action("GETCustomers", Action.TYPE.VIEW), null), "/customers");
		ResourceState customer = new ResourceState("Customer", "customer", createActionList(new Action("GETCustomer", Action.TYPE.VIEW), new Action("PUTCustomer", Action.TYPE.ENTRY)), "/customers/{id}");
		ResourceState deleted = new ResourceState(customer, "deleted", createActionList(null, new Action("NoopDELETE", Action.TYPE.ENTRY)));
		
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "name");
		customers.addTransitionForEachItem("GET", customer, uriLinkageMap);		

		customer.addTransition("PUT", customer, uriLinkageMap);		
		customer.addTransition("DELETE", deleted, uriLinkageMap);
		return new ResourceStateMachine(customers);
	}
	
	private List<Action> createActionList(Action view, Action entry) {
		List<Action> actions = new ArrayList<Action>();
		if (view != null)
			actions.add(view);
		if (entry != null)
			actions.add(entry);
		return actions;
	}
}
