package com.temenos.interaction.example.mashup.twitter;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	// the entity that stores users
	private final static String USER_ENTITY_NAME = "user";

	public ResourceState getInteractionModel() {
		// this will be the service root
		ResourceState initialState = new ResourceState("home", "initial", "/");
		
		initialState.addTransition("GET", getUsersInteractionModel());
		return initialState;
	}

	public ResourceStateMachine getUsersInteractionModel() {
		
		CollectionResourceState allUsers = new CollectionResourceState(USER_ENTITY_NAME, "allUsers", "/users");
		ResourceState userProfile = new ResourceState(USER_ENTITY_NAME, "exists", "/users/{userID}", "userID", "self".split(" "));

		// a linkage map (target URI element, source entity element)
		Map<String, String> uriLinkageMap = new HashMap<String, String>();

		/* 
		 * a link on each user in the collection to get view the user
		 * no linkage map as target URI element (self) must exist in source entity element (also self)
		 */
		uriLinkageMap.clear();
		allUsers.addTransitionForEachItem("GET", userProfile, uriLinkageMap);
		
		return new ResourceStateMachine(allUsers);
	}

}
