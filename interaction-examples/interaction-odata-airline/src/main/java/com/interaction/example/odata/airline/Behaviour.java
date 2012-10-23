package com.interaction.example.odata.airline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.temenos.interaction.commands.odata.ODataUriSpecification;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	// entities
	private final static String FLIGHT = "Flight";
	private final static String FLIGHT_SCHEDULE = "FlightSchedule";
	private final static String AIRPORT = "Airport";

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
		
		// all states are navigable from the root
		ResourceStateMachine root = new ResourceStateMachine(initialState);
		addTransitionsBetweenRSMs(airports, flightSchedules);
		addTransitionsBetweenFlightsFlightSchedules(root);
		return initialState;
	}
	
	public void addTransitionsBetweenRSMs(ResourceStateMachine airports, ResourceStateMachine flightSchedules) {
		Map<String, String> uriLinkageMap = new HashMap<String, String>();

		//e.g. FlightSchedule(1)/arrivalAirport should return link to Airport(JDK)/flightSchedules
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "code");
		flightSchedules.getResourceStateByName("arrivalAirport").addTransition("GET", airports.getResourceStateByName("airport"), uriLinkageMap);
		flightSchedules.getResourceStateByName("arrivalAirport").addTransition("GET", airports.getResourceStateByName("departures"), uriLinkageMap);
		flightSchedules.getResourceStateByName("arrivalAirport").addTransition("GET", airports.getResourceStateByName("arrivals"), uriLinkageMap);
		flightSchedules.getResourceStateByName("departureAirport").addTransition("GET", airports.getResourceStateByName("airport"), uriLinkageMap);
		flightSchedules.getResourceStateByName("departureAirport").addTransition("GET", airports.getResourceStateByName("departures"), uriLinkageMap);
		flightSchedules.getResourceStateByName("departureAirport").addTransition("GET", airports.getResourceStateByName("arrivals"), uriLinkageMap);
		
		//e.g. Airport(JFK)/departures should return link to FlightSchedule(1)
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "flightScheduleID");
		((CollectionResourceState) airports.getResourceStateByName("departures")).addTransitionForEachItem("GET", flightSchedules.getResourceStateByName("flightschedule"), uriLinkageMap);
		((CollectionResourceState) airports.getResourceStateByName("arrivals")).addTransitionForEachItem("GET", flightSchedules.getResourceStateByName("flightschedule"), uriLinkageMap);
	}

	public void addTransitionsBetweenFlightsFlightSchedules(ResourceStateMachine root) {
		Map<String, String> uriLinkageMap = new HashMap<String, String>();

		// A flight is one instance of a scheduled flight
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "flightScheduleID");
		root.getResourceStateByName("flight").addTransition("GET", root.getResourceStateByName("flightschedule"), uriLinkageMap);
		((CollectionResourceState)root.getResourceStateByName("flights")).addTransitionForEachItem("GET", root.getResourceStateByName("flightschedule"), uriLinkageMap);
	}


	public ResourceStateMachine getFlightSchedulesSM() {
		CollectionResourceState flightSchedules = new CollectionResourceState(FLIGHT_SCHEDULE, "flightschedules", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), "/FlightSchedule");
		ResourceState pseudo = new ResourceState(flightSchedules, "FlightSchedules_pseudo_created", createActionSet(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState flightSchedule = new ResourceState(FLIGHT_SCHEDULE, "flightschedule", createActionSet(new Action("GETEntity", Action.TYPE.VIEW), null), "/FlightSchedule({id})");
		Properties viewAirportNavProperties = new Properties();
		viewAirportNavProperties.put("entity", FLIGHT_SCHEDULE);
		ResourceState arrivalAirport = new ResourceState(AIRPORT, "arrivalAirport", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, viewAirportNavProperties), null), "/FlightSchedule({id})/arrivalAirport", new ODataUriSpecification().getTemplate("/FlightSchedule", ODataUriSpecification.NAVPROPERTY_URI_TYPE));
		ResourceState departureAirport = new ResourceState(AIRPORT, "departureAirport", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, viewAirportNavProperties), null), "/FlightSchedule({id})/departureAirport", new ODataUriSpecification().getTemplate("/FlightSchedule", ODataUriSpecification.NAVPROPERTY_URI_TYPE));

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "flightScheduleID");
		flightSchedules.addTransitionForEachItem("GET", flightSchedule, uriLinkageMap);
		flightSchedules.addTransition("POST", pseudo);
		uriLinkageMap.put("navproperty", "arrivalAirport");
		flightSchedule.addTransition("GET", arrivalAirport, uriLinkageMap);
		flightSchedules.addTransitionForEachItem("GET", arrivalAirport, uriLinkageMap);
		uriLinkageMap.put("navproperty", "departureAirport");
		flightSchedule.addTransition("GET", departureAirport, uriLinkageMap);
		flightSchedules.addTransitionForEachItem("GET", departureAirport, uriLinkageMap);

		return new ResourceStateMachine(flightSchedules);
	}

	public ResourceStateMachine getAirportsSM() {
		CollectionResourceState airports = new CollectionResourceState(AIRPORT, "airports", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), "/Airport");
		ResourceState airport = new ResourceState(AIRPORT, "airport", createActionSet(new Action("GETEntity", Action.TYPE.VIEW), null), "/Airport({id})");
		Properties viewFlightScheduleNavProperties = new Properties();
		viewFlightScheduleNavProperties.put("entity", AIRPORT);
		CollectionResourceState departures = new CollectionResourceState(FLIGHT_SCHEDULE, "departures", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, viewFlightScheduleNavProperties), null), "/Airport({id})/departures", new ODataUriSpecification().getTemplate("/Airport", ODataUriSpecification.NAVPROPERTY_URI_TYPE));
		CollectionResourceState arrivals = new CollectionResourceState(FLIGHT_SCHEDULE, "arrivals", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, viewFlightScheduleNavProperties), null), "/Airport({id})/arrivals", new ODataUriSpecification().getTemplate("/Airport", ODataUriSpecification.NAVPROPERTY_URI_TYPE));
		
		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "code");
		airports.addTransitionForEachItem("GET", airport, uriLinkageMap);
		airport.addTransition("GET", departures, uriLinkageMap);
		airports.addTransitionForEachItem("GET", departures, uriLinkageMap);
		airport.addTransition("GET", arrivals, uriLinkageMap);
		airports.addTransitionForEachItem("GET", arrivals, uriLinkageMap);

		return new ResourceStateMachine(airports);
	}

	public ResourceStateMachine getFlightsSM() {
		CollectionResourceState flights = new CollectionResourceState(FLIGHT, "flights", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), "/Flight");
		ResourceState flight = new ResourceState(FLIGHT, "flight", createActionSet(new Action("GETEntity", Action.TYPE.VIEW), null), "/Flight({id})");
		
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
