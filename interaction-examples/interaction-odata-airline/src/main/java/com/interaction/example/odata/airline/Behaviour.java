package com.interaction.example.odata.airline;

import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;

public class Behaviour {

	public ResourceState getSimpleODataInteractionModel() {
		// this will be the service root
		ResourceState initialState = new ResourceState("", "begin", "");
		
		/*
		 * create a resource to the $metadata link, this will also require use to 
		 * create a GET command for the $metadata
		 */
		ResourceState metadata = new ResourceState("", "metadata", "/$metadata");
		ResourceState flights = new ResourceState("Flight", "flights", "/Flight");
		ResourceState airport = new ResourceState("Airport", "airports", "/Airport");
		ResourceState flightsSchedules = new ResourceState("FlightSchedule", "flightschedules", "/FlightSchedule");
		
		initialState.addTransition("GET", metadata);
		initialState.addTransition("GET", new ResourceStateMachine("Flight", flights));
		initialState.addTransition("GET", new ResourceStateMachine("Airport", airport));
		initialState.addTransition("GET", new ResourceStateMachine("FlightSchedule", flightsSchedules));
		return initialState;
	}

}
