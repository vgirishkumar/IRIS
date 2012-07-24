package com.interaction.example.odata.airline;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	public ResourceState getSimpleODataInteractionModel() {
		// the service root
		ResourceState initialState = new ResourceState("ServiceDocument", "begin", "/");
		ResourceState metadata = new ResourceState("", "metadata", "/$metadata");

		ResourceStateMachine airports = getAirportsSM();
		ResourceStateMachine flights = getFlightsSM();
		ResourceStateMachine flightSchedules = getFlightSchedulesSM();

		//Add transitions between RSMs
		initialState.addTransition("GET", metadata);
		initialState.addTransition("GET", airports);
		initialState.addTransition("GET", flights);
		initialState.addTransition("GET", flightSchedules);
		addTransitionsBetweenRSMs(airports, flightSchedules);
		
		return initialState;
	}
	
	public void addTransitionsBetweenRSMs(ResourceStateMachine airports, ResourceStateMachine flightSchedules) {
		Map<String, String> uriLinkageMap = new HashMap<String, String>();

		//e.g. FlightSchedule(1)/arrivalAirport should return link to Airport(JDK)/flightSchedules
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "code");
		flightSchedules.getState("/FlightSchedule({id})/arrivalAirport").addTransition("GET", airports.getState("/Airport({id})/flightSchedules"), uriLinkageMap);
		flightSchedules.getState("/FlightSchedule({id})/departureAirport").addTransition("GET", airports.getState("/Airport({id})/flightSchedules"), uriLinkageMap);
		
		//e.g. Airport(JFK)/flightSchedules should return link to FlightSchedule(1)
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "flightScheduleID");
		((CollectionResourceState) airports.getState("/Airport({id})/flightSchedules")).addTransitionForEachItem("GET", flightSchedules.getState("/FlightSchedule({id})"), uriLinkageMap);
	}

	public ResourceStateMachine getFlightSchedulesSM() {
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "flightschedules", "/FlightSchedule");
		ResourceState pseudo = new ResourceState(flightSchedules, "FlightSchedules_pseudo_created");
		ResourceState flightSchedule = new ResourceState("FlightSchedule", "flightschedule", "/FlightSchedule({id})");
		ResourceState arrivalAirport = new ResourceState("Airport", "arrivalAirport", "/FlightSchedule({id})/arrivalAirport");
		ResourceState departureAirport = new ResourceState("Airport", "departureAirport", "/FlightSchedule({id})/departureAirport");

		//Add state transitions
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
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "flightSchedules", "/Airport({id})/flightSchedules");
		
		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "code");
		airports.addTransitionForEachItem("GET", airport, uriLinkageMap);
		airport.addTransition("GET", flightSchedules, uriLinkageMap);

		return new ResourceStateMachine(airports);
	}

	public ResourceStateMachine getFlightsSM() {
		CollectionResourceState flights = new CollectionResourceState("Flight", "flights", "/Flight");
		ResourceState flight = new ResourceState("Flight", "flight", "/Flight({id})");
		
		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "flightID");
		flights.addTransitionForEachItem("GET", flight, uriLinkageMap);

		return new ResourceStateMachine(flights);
	}
}
