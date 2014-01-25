package com.temenos.interaction.example.hateoas.banking;

/*
 * #%L
 * T24-ft-interaction-banking
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
		ResourceState initialState = new ResourceState("ServiceDocument", "begin", createActionList(new Action("GETServiceDocument", Action.TYPE.VIEW), null), "/");
		ResourceState preferences = new ResourceState("Preferences", "preferences", createActionList(new Action("GETPreferences", Action.TYPE.VIEW), null), "/preferences");
		
		initialState.addTransition("GET", preferences);
		initialState.addTransition("GET", getFundsTransferInteractionModel());
		return initialState;
	}

	public ResourceStateMachine getFundsTransferInteractionModel() {
		CollectionResourceState initialState = new CollectionResourceState("FundsTransfer", "initial", createActionList(new Action("GETEntities", Action.TYPE.VIEW), null), "/fundstransfer");
		ResourceState newFtState = new ResourceState(initialState, "new", createActionList(new Action("T24Input", Action.TYPE.VIEW), null), "/new");
		ResourceState viewed = new ResourceState("FundsTransfer", "view", createActionList(new Action("GETEntity", Action.TYPE.VIEW), null), "/fundstransfer/{Id}", "Id");
		ResourceState unauthorised = new ResourceState("FundsTransfer", "unauthorised", createActionList(new Action("T24Input", Action.TYPE.VIEW), null), "/fundstransfer/{Id}/unauthorise", "Id");
		ResourceState authorised = new ResourceState("FundsTransfer", "authorised", createActionList(new Action("T24Authorise", Action.TYPE.VIEW), null), "/fundstransfer/{Id}/authorise", "Id");
		ResourceState held = new ResourceState("FundsTransfer", "held", createActionList(new Action("T24Hold", Action.TYPE.VIEW), null), "/fundstransfer/{Id}/hold", "Id");
		ResourceState reversed = new ResourceState("FundsTransfer", "reversed", createActionList(new Action("T24Reverse", Action.TYPE.VIEW), null), "/fundstransfer/{Id}/reverse", "Id");
		ResourceState restored = new ResourceState("FundsTransfer", "restored", createActionList(new Action("T24Restore", Action.TYPE.VIEW), null), "/fundstransfer/{Id}/restore", "Id");
		ResourceState deleted = new ResourceState("FundsTransfer", "deleted", createActionList(new Action("T24Delete", Action.TYPE.VIEW), null), "/fundstransfer/{Id}/delete", "Id");


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
	
	private List<Action> createActionList(Action view, Action entry) {
		List<Action> actions = new ArrayList<Action>();
		if (view != null)
			actions.add(view);
		if (entry != null)
			actions.add(entry);
		return actions;
	}
	
}
