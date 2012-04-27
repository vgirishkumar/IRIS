package com.interaction.example.odata.airline;

import java.util.HashMap;
import java.util.Map;

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
		
		ResourceState flightSchedules = new ResourceState("FlightSchedule", "flightschedules", "/FlightSchedule");
		ResourceState flightSchedule = new ResourceState("FlightSchedule", "flightschedule", "/FlightSchedule({id})");
		// add collection transition to individual
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "flightScheduleID");
		flightSchedules.addTransition("GET", flightSchedule, uriLinkageMap);
		
		initialState.addTransition("GET", metadata);
		initialState.addTransition("GET", new ResourceStateMachine(flights));
		initialState.addTransition("GET", new ResourceStateMachine(airport));
		initialState.addTransition("GET", new ResourceStateMachine(flightSchedules));
		return initialState;
	}

}
