package com.interaction.example.odata.airline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	public ResourceState getSimpleODataInteractionModel() {
		// the service root
		ResourceState initialState = new ResourceState("ServiceDocument", "begin", createActionSet(new Action("GETServiceDocument", Action.TYPE.VIEW), null), "/");
		ResourceState metadata = new ResourceState("", "metadata", createActionSet(new Action("GETMetadata", Action.TYPE.VIEW), null), "/$metadata");

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
		flightSchedules.getResourceStateByName("arrivalAirport").addTransition("GET", airports.getResourceStateByName("flightSchedules"), uriLinkageMap);
		flightSchedules.getResourceStateByName("departureAirport").addTransition("GET", airports.getResourceStateByName("flightSchedules"), uriLinkageMap);
		
		//e.g. Airport(JFK)/flightSchedules should return link to FlightSchedule(1)
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "flightScheduleID");
		((CollectionResourceState) airports.getResourceStateByName("flightSchedules")).addTransitionForEachItem("GET", flightSchedules.getResourceStateByName("flightschedule"), uriLinkageMap);
	}

	public ResourceStateMachine getFlightSchedulesSM() {
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "flightschedules", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), "/FlightSchedule");
		ResourceState pseudo = new ResourceState(flightSchedules, "FlightSchedules_pseudo_created", createActionSet(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState flightSchedule = new ResourceState("FlightSchedule", "flightschedule", createActionSet(new Action("GETEntity", Action.TYPE.VIEW), null), "/FlightSchedule({id})");
		ResourceState arrivalAirport = new ResourceState("Airport", "arrivalAirport", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW), null), "/FlightSchedule({id})/arrivalAirport");
		ResourceState departureAirport = new ResourceState("Airport", "departureAirport", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW), null), "/FlightSchedule({id})/departureAirport");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "flightScheduleID");
		flightSchedules.addTransitionForEachItem("GET", flightSchedule, uriLinkageMap);
		flightSchedules.addTransition("POST", pseudo);
		uriLinkageMap.put("navproperty", "arrivalAirport");
		flightSchedule.addTransition("GET", arrivalAirport, uriLinkageMap);
		uriLinkageMap.put("navproperty", "departureAirport");
		flightSchedule.addTransition("GET", departureAirport, uriLinkageMap);

		return new ResourceStateMachine(flightSchedules);
	}

	public ResourceStateMachine getAirportsSM() {
		CollectionResourceState airports = new CollectionResourceState("Airport", "airports", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), "/Airport");
		ResourceState airport = new ResourceState("Airport", "airport", createActionSet(new Action("GETEntity", Action.TYPE.VIEW), null), "/Airport({id})");
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "flightSchedules", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), "/Airport({id})/flightSchedules");
		
		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "code");
		airports.addTransitionForEachItem("GET", airport, uriLinkageMap);
		airport.addTransition("GET", flightSchedules, uriLinkageMap);

		return new ResourceStateMachine(airports);
	}

	public ResourceStateMachine getFlightsSM() {
		CollectionResourceState flights = new CollectionResourceState("Flight", "flights", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), "/Flight");
		ResourceState flight = new ResourceState("Flight", "flight", createActionSet(new Action("GETEntity", Action.TYPE.VIEW), null), "/Flight({id})");
		
		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "flightID");
		flights.addTransitionForEachItem("GET", flight, uriLinkageMap);

		return new ResourceStateMachine(flights);
	}
	
	private Set<Action> createActionSet(Action view, Action entry) {
		Set<Action> actions = new HashSet<Action>();
		if (view != null)
			actions.add(view);
		if (entry != null)
			actions.add(entry);
		return actions;
	}
	
}
