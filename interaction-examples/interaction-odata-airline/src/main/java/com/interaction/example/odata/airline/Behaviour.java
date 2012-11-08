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
import com.temenos.interaction.core.hypermedia.validation.HypermediaValidator;

public class Behaviour {

    public static void main(String[] args) {
        ResourceStateMachine hypermediaEngine = new ResourceStateMachine(new Behaviour().getSimpleODataInteractionModel());
        HypermediaValidator validator = HypermediaValidator.createValidator(hypermediaEngine);
        System.out.println(validator.graph());
    }

	public ResourceState getSimpleODataInteractionModel() {
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		Properties actionViewProperties = new Properties();
		ResourceState initial = null;
		// create states
		CollectionResourceState sServiceDocument = new CollectionResourceState("ServiceDocument", "ServiceDocument", createActionSet(new Action("GETServiceDocument", Action.TYPE.VIEW, actionViewProperties), null), "");
		// identify the initial state
		initial = sServiceDocument;
		ResourceState smetadata = new ResourceState("Metadata", "metadata", createActionSet(new Action("GETMetadata", Action.TYPE.VIEW, actionViewProperties), null), "/$metadata");
		CollectionResourceState sflights = new CollectionResourceState("Flight", "flights", createActionSet(new Action("GETEntities", Action.TYPE.VIEW, actionViewProperties), null), "/Flight");
		ResourceState sflight = new ResourceState("Flight", "flight", createActionSet(new Action("GETEntity", Action.TYPE.VIEW, actionViewProperties), null), "/Flight({id})");
		CollectionResourceState sairports = new CollectionResourceState("Airport", "airports", createActionSet(new Action("GETEntities", Action.TYPE.VIEW, actionViewProperties), null), "/Airport");
		ResourceState sairport = new ResourceState("Airport", "airport", createActionSet(new Action("GETEntity", Action.TYPE.VIEW, actionViewProperties), null), "/Airport({id})");
		CollectionResourceState sflightschedules = new CollectionResourceState("FlightSchedule", "flightschedules", createActionSet(new Action("GETEntities", Action.TYPE.VIEW, actionViewProperties), null), "/FlightSchedule");
		ResourceState sflightschedule = new ResourceState("FlightSchedule", "flightschedule", createActionSet(new Action("GETEntity", Action.TYPE.VIEW, actionViewProperties), null), "/FlightSchedule({id})");
		actionViewProperties.put("entity", "FlightSchedule");
		ResourceState sdepartureAirport = new ResourceState("Airport", "departureAirport", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, actionViewProperties), null), "/FlightSchedule({id})/departureAirport", new ODataUriSpecification().getTemplate("/FlightSchedule", "NavProperty"));
		actionViewProperties.put("entity", "FlightSchedule");
		ResourceState sarrivalAirport = new ResourceState("Airport", "arrivalAirport", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, actionViewProperties), null), "/FlightSchedule({id})/arrivalAirport", new ODataUriSpecification().getTemplate("/FlightSchedule", "NavProperty"));

		// create regular transitions
		sServiceDocument.addTransition("GET", smetadata, uriLinkageMap);
		sServiceDocument.addTransition("GET", sflights, uriLinkageMap);
		sServiceDocument.addTransition("GET", sairports, uriLinkageMap);
		sServiceDocument.addTransition("GET", sflightschedules, uriLinkageMap);
		uriLinkageMap.put("id", "flightScheduleID");uriLinkageMap.put("navproperty", "departureAirport");
		sflightschedule.addTransition("GET", sdepartureAirport, uriLinkageMap);
		uriLinkageMap.put("id", "flightScheduleID");uriLinkageMap.put("navproperty", "arrivalAirport");
		sflightschedule.addTransition("GET", sarrivalAirport, uriLinkageMap);
		uriLinkageMap.put("id", "code");
		sdepartureAirport.addTransition("GET", sairport, uriLinkageMap);
		uriLinkageMap.put("id", "code");
		sarrivalAirport.addTransition("GET", sairport, uriLinkageMap);

        // create foreach transitions
                uriLinkageMap.put("id", "flightID");
                sflights.addTransitionForEachItem("GET", sflight, uriLinkageMap);
                uriLinkageMap.put("id", "code");
                sairports.addTransitionForEachItem("GET", sairport, uriLinkageMap);
                uriLinkageMap.put("id", "flightScheduleID");
                sflightschedules.addTransitionForEachItem("GET", sflightschedule, uriLinkageMap);
                uriLinkageMap.put("id", "flightScheduleID");
                sflightschedules.addTransitionForEachItem("GET", sdepartureAirport, uriLinkageMap);
                uriLinkageMap.put("id", "flightScheduleID");
                sflightschedules.addTransitionForEachItem("GET", sarrivalAirport, uriLinkageMap);

        // create AUTO transitions

	    return initial;
	}
	
	public ResourceState BCKPgetSimpleODataInteractionModel() {
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		Properties actionViewProperties = new Properties();
		ResourceState initial = null;
		// create states
		CollectionResourceState sServiceDocument = new CollectionResourceState("ServiceDocument", "ServiceDocument", createActionSet(new Action("GETServiceDocument", Action.TYPE.VIEW, actionViewProperties), null), "");
		// identify the initial state
		initial = sServiceDocument;
		ResourceState smetadata = new ResourceState("Metadata", "metadata", createActionSet(new Action("GETMetadata", Action.TYPE.VIEW, actionViewProperties), null), "/$metadata");
		CollectionResourceState sflights = new CollectionResourceState("Flight", "flights", createActionSet(new Action("GETEntities", Action.TYPE.VIEW, actionViewProperties), null), "/Flight");
		ResourceState sflight = new ResourceState("Flight", "flight", createActionSet(new Action("GETEntity", Action.TYPE.VIEW, actionViewProperties), null), "/Flight({id})");
		CollectionResourceState sairports = new CollectionResourceState("Airport", "airports", createActionSet(new Action("GETEntities", Action.TYPE.VIEW, actionViewProperties), null), "/Airport");
		ResourceState sairport = new ResourceState("Airport", "airport", createActionSet(new Action("GETEntity", Action.TYPE.VIEW, actionViewProperties), null), "/Airport({id})");
		CollectionResourceState sflightschedules = new CollectionResourceState("FlightSchedule", "flightschedules", createActionSet(new Action("GETEntities", Action.TYPE.VIEW, actionViewProperties), null), "/FlightSchedule");
		ResourceState sflightschedule = new ResourceState("FlightSchedule", "flightschedule", createActionSet(new Action("GETEntity", Action.TYPE.VIEW, actionViewProperties), null), "/FlightSchedule({id})");
		actionViewProperties.put("entity", "FlightSchedule");
		ResourceState sdepartureAirport = new ResourceState("Airport", "departureAirport", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, actionViewProperties), null), "/FlightSchedule({id})/departureAirport", new ODataUriSpecification().getTemplate("/FlightSchedule", "NavProperty"));
		actionViewProperties.put("entity", "FlightSchedule");
		ResourceState sarrivalAirport = new ResourceState("Airport", "arrivalAirport", createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, actionViewProperties), null), "/FlightSchedule({id})/arrivalAirport", new ODataUriSpecification().getTemplate("/FlightSchedule", "NavProperty"));

		// create regular transitions
		sServiceDocument.addTransition("GET", smetadata, uriLinkageMap);
		sServiceDocument.addTransition("GET", sflights, uriLinkageMap);
		sServiceDocument.addTransition("GET", sairports, uriLinkageMap);
		sServiceDocument.addTransition("GET", sflightschedules, uriLinkageMap);
		uriLinkageMap.put("id", "flightScheduleID");uriLinkageMap.put("navproperty", "departureAirport");
		sflightschedule.addTransition("GET", sdepartureAirport, uriLinkageMap);
		uriLinkageMap.put("id", "flightScheduleID");uriLinkageMap.put("navproperty", "arrivalAirport");
		sflightschedule.addTransition("GET", sarrivalAirport, uriLinkageMap);
		uriLinkageMap.put("id", "code");
		sdepartureAirport.addTransition("GET", sairport, uriLinkageMap);
		uriLinkageMap.put("id", "code");
		sarrivalAirport.addTransition("GET", sairport, uriLinkageMap);

        // create foreach transitions
                uriLinkageMap.put("id", "flightID");
                sflights.addTransitionForEachItem("GET", sflight, uriLinkageMap);
                uriLinkageMap.put("id", "code");
                sairports.addTransitionForEachItem("GET", sairport, uriLinkageMap);
                uriLinkageMap.put("id", "flightScheduleID");
                sflightschedules.addTransitionForEachItem("GET", sflightschedule, uriLinkageMap);
                uriLinkageMap.put("id", "flightScheduleID");
                sflightschedules.addTransitionForEachItem("GET", sdepartureAirport, uriLinkageMap);
                uriLinkageMap.put("id", "flightScheduleID");
                sflightschedules.addTransitionForEachItem("GET", sarrivalAirport, uriLinkageMap);

        // create AUTO transitions

	    return initial;
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
