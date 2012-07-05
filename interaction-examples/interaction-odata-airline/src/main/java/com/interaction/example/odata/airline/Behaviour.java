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
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "flightschedules", "/FlightSchedule");
		ResourceState pseudo = new ResourceState(flightSchedules, "FlightSchedules.pseudo.created");
		ResourceState flightSchedule = new ResourceState("FlightSchedule", "flightschedule", "/FlightSchedule({id})");
		ResourceState flightScheduleArrivalAirport = new ResourceState("Airport", "flightScheduleArrivalAirport", "/FlightSchedule({id})/arrivalAirport");
		ResourceState flightScheduleDepartureAirport = new ResourceState("Airport", "flightScheduleDepartureAirport", "/FlightSchedule({id})/departureAirport");

		uriLinkageMap.clear();
		uriLinkageMap.put("id", "flightScheduleID");
		flightSchedules.addTransitionForEachItem("GET", flightSchedule, uriLinkageMap);
		flightSchedules.addTransition("POST", pseudo);
		flightSchedule.addTransition("GET", flightScheduleArrivalAirport, uriLinkageMap);
		flightSchedule.addTransition("GET", flightScheduleDepartureAirport, uriLinkageMap);

		return new ResourceStateMachine(flightSchedules);
	}

	public ResourceStateMachine getAirportsSM() {
		CollectionResourceState airports = new CollectionResourceState("Airport", "airports", "/Airport");
		ResourceState airport = new ResourceState("Airport", "airport", "/Airport({id})");
		
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "code");
		airports.addTransitionForEachItem("GET", airport, uriLinkageMap);

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
