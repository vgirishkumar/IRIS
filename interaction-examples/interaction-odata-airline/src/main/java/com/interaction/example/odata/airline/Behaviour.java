package com.interaction.example.odata.airline;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.link.CollectionResourceState;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;

public class Behaviour {

	public ResourceState getSimpleODataInteractionModel() {
		// the service root
		ResourceState initialState = new ResourceState("ServiceDocument", "begin", "/");
		ResourceState metadata = new ResourceState("", "metadata", "/$metadata");

		initialState.addTransition("GET", metadata);
		initialState.addTransition("GET", getFlightSchedulesSM());
		initialState.addTransition("GET", getAirportsSM());
		initialState.addTransition("GET", getFlightsSM());
		
		return initialState;
	}

	public ResourceStateMachine getFlightSchedulesSM() {
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "flightschedules", "/FlightSchedule");
		ResourceState pseudo = new ResourceState(flightSchedules, "FlightSchedules_pseudo_created");
		ResourceState flightSchedule = new ResourceState("FlightSchedule", "flightschedule", "/FlightSchedule({id})");
		ResourceState arrivalAirport = new ResourceState("Airport", "airport", "/FlightSchedule({id})/arrivalAirport");
		ResourceState departureAirport = new ResourceState("Airport", "airport", "/FlightSchedule({id})/departureAirport");

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "flightScheduleID");
		flightSchedules.addTransitionForEachItem("GET", flightSchedule, uriLinkageMap);
		flightSchedules.addTransition("POST", pseudo);
		flightSchedule.addTransition("GET", arrivalAirport, uriLinkageMap);
		flightSchedule.addTransition("GET", departureAirport, uriLinkageMap);

		return new ResourceStateMachine(flightSchedules);
	}

	public ResourceStateMachine getAirportsSM() {
		CollectionResourceState airports = new CollectionResourceState("Airport", "airports", "/Airport");
		ResourceState airport = new ResourceState("Airport", "airport", "/Airport({id})");
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "flightschedules", "/Airport({id})/flightSchedules");
		
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "code");
		airports.addTransitionForEachItem("GET", airport, uriLinkageMap);
		airport.addTransition("GET", flightSchedules, uriLinkageMap);

		return new ResourceStateMachine(airports);
	}

	public ResourceStateMachine getFlightsSM() {
		CollectionResourceState flights = new CollectionResourceState("Flight", "flights", "/Flight");
		ResourceState flight = new ResourceState("Flight", "flight", "/Flight({id})");
		
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "flightID");
		flights.addTransitionForEachItem("GET", flight, uriLinkageMap);

		return new ResourceStateMachine(flights);
	}
}
