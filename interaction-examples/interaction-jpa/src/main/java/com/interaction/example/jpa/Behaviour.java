package com.interaction.example.jpa;

import com.temenos.interaction.core.link.ResourceState;

public class Behaviour {

	public ResourceState getSimpleODataInteractionModel() {
		// this will be the service root
		ResourceState initialState = new ResourceState("begin", "");
		
		/*
		 * create a resource to the $metadata link, this will also require use to 
		 * create a GET command for the $metadata
		 */
		ResourceState metadata = new ResourceState("metadata", "/$metadata");
		ResourceState flights = new ResourceState("flights", "/Flight");
		ResourceState airport = new ResourceState("airports", "/Airports");
		ResourceState flightsSchedules = new ResourceState("flightschedules", "/FlightSchedule");
		
		initialState.addTransition("GET", metadata);
		initialState.addTransition("GET", flights);
		initialState.addTransition("GET", airport);
		initialState.addTransition("GET", flightsSchedules);
		return initialState;
	}

}
