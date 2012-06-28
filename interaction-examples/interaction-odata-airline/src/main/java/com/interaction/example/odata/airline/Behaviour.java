package com.interaction.example.odata.airline;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.link.CollectionResourceState;
import com.temenos.interaction.core.link.ResourceState;

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

		//Airport collection and entities
		CollectionResourceState airports = new CollectionResourceState("Airport", "airports", "/Airport");
		ResourceState airport = new ResourceState("Airport", "airport", "/Airport({id})");
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "code");
		airports.addTransitionForEachItem("GET", airport, uriLinkageMap);

		//Transition from Flight schedule to Airport
		ResourceState flightSchedule = new ResourceState("FlightSchedule", "flightschedule", "/FlightSchedule({id})");
		ResourceState departureAirport = new ResourceState("Airport", "departureAirport", "/Airport({id})");
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "departureAirportCode");
		flightSchedule.addTransition("GET", departureAirport, uriLinkageMap);
		ResourceState arrivalAirport = new ResourceState("Airport", "arrivalAirport", "/Airport({id})");
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "arrivalAirportCode");
		flightSchedule.addTransition("GET", arrivalAirport, uriLinkageMap);

		//Flight schedule collection and entities
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "flightschedules", "/FlightSchedule");
		uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "flightScheduleID");
		flightSchedules.addTransitionForEachItem("GET", flightSchedule, uriLinkageMap);
		
		initialState.addTransition("GET", metadata);
		initialState.addTransition("GET", flights);
		initialState.addTransition("GET", airports);
		initialState.addTransition("GET", flightSchedules);
		
		return initialState;
	}

}
