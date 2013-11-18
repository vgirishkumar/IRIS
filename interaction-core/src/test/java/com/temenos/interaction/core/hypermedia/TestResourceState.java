package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

public class TestResourceState {

	@Test
	public void testRels() {
		String ENTITY_NAME = "entity";
		String linkRels = "self geospatial";
		ResourceState initial = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/", linkRels.split(" "));
		assertEquals(2, initial.getRels().length);
		assertEquals("self", initial.getRels()[0]);
		assertEquals("geospatial", initial.getRels()[1]);
	}

	@Test
	public void testRel() {
		String ENTITY_NAME = "entity";
		String linkRels = "self geospatial";
		ResourceState initial = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/", linkRels.split(" "));
		assertEquals("self geospatial", initial.getRel());
	}

	@Test
	public void testDefaultRel() {
		String ENTITY_NAME = "entity";
		ResourceState initial = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/");
//		assertEquals("item self", initial.getRel());
		assertEquals("item", initial.getRel());
	}

	@Test
	public void testId() {
		String ENTITY_NAME = "entity";
		ResourceState initial = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/");
		assertEquals("entity.begin", initial.getId());
	}
	
	@Test
	public void testCollection() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", new ArrayList<Action>(), "/");
		
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(end);

		Set<ResourceState> states2 = new HashSet<ResourceState>();
		states2.add(begin);
		states2.add(exists);
		states2.add(end);
		
		states.removeAll(states2);
		assertEquals(0, states.size());
	}

	/**
	 * Each resource state must have a unique path.  Test the constructor
	 * that uses the state name as the path.
	 */
	@Test
	public void testSelfStatePath() {
		String ENTITY_NAME = "entity";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/test");
		ResourceState exists = new ResourceState(initial, "exists", new ArrayList<Action>(), "/exists");
		ResourceState root = new ResourceState(ENTITY_NAME, "root", new ArrayList<Action>(), "/");
		ResourceState archived = new ResourceState(ENTITY_NAME, "archived", new ArrayList<Action>(), "/archived");
		assertEquals("/test", initial.getPath());
		assertEquals("/test/exists", exists.getPath());
		assertEquals("/", root.getPath());
		assertEquals("/archived", archived.getPath());
	}

	@Test
	public void testGetCommand() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "{id}");
		begin.addTransition("PUT", exists);
		assertEquals("PUT", begin.getTransition(exists).getCommand().getMethod());
		assertEquals("{id}", begin.getTransition(exists).getCommand().getPath());
	}

	@Test
	public void testAutoTransition() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "{id}");
		begin.addTransition(null, exists, Transition.AUTO);
		assertTrue(begin.getTransition(exists).getCommand().isAutoTransition());
	}	

	@Test (expected = IllegalArgumentException.class)
	public void testInvalidAutoTransition() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "{id}");
		begin.addTransition("PUT", exists, Transition.AUTO);
	}	
	
	@Test
	public void testAddTransitionLinkageMap() {
		// define a linkage map (target URI element, source entity element)
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		// uri defines a template with {id}, our entity needs to supply {NoteId} as the id
		uriLinkageMap.put("id", "NoteId");

		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState("SomeEntity", "initial", new ArrayList<Action>(), "/tests");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "/test/{id}");
		begin.addTransition("PUT", exists, uriLinkageMap);
		assertEquals("/test/{id}", begin.getTransition(exists).getCommand().getPath());
		assertTrue(begin.getTransition(exists).getCommand().getUriParameters().containsKey("id"));
	}
	
	@Test
	public void testAddMultiplTransitionsNoPath() {
		ResourceState initial = new ResourceState("ENTITY", "initial", new ArrayList<Action>(), "/");
		ResourceState A = new ResourceState("ENTITY", "initial", new ArrayList<Action>(), "/A");
		ResourceState B = new ResourceState("ENTITY", "initial", new ArrayList<Action>(), "/B");
		
		initial.addTransition(A);
		initial.addTransition(B);
		
		assertEquals(2, initial.getAllTargets().size());
	}
	
	@Test(expected = AssertionError.class)
	public void testResourceStateNoPath() {
		new ResourceState("ENTITY", "initial", new ArrayList<Action>(), "");
	}

	@Test
	public void testTransitionToStateMachine() {
		String ENTITY_NAME1 = "entity1";
		ResourceState initial = new ResourceState(ENTITY_NAME1, "initial", new ArrayList<Action>(), "/test/{id}");
		ResourceState exists = new ResourceState(initial, "exists", new ArrayList<Action>());
		ResourceState deleted = new ResourceState(initial, "deleted", new ArrayList<Action>());
		initial.addTransition("PUT", exists);
		exists.addTransition("DELETE", deleted);
		
		String ENTITY_NAME2 = "entity2";
		ResourceState initial2 = new ResourceState(ENTITY_NAME2, "initial", new ArrayList<Action>(), "/entity/2");
		ResourceState exists2 = new ResourceState(initial2, "exists", new ArrayList<Action>());
		ResourceState deleted2 = new ResourceState(initial2, "deleted", new ArrayList<Action>());
		initial2.addTransition("PUT", exists2);
		exists2.addTransition("DELETE", deleted2);
		
		ResourceStateMachine rsm1 = new ResourceStateMachine(initial);
		ResourceStateMachine rsm2 = new ResourceStateMachine(initial2);
		exists.addTransition("GET", rsm2);
		exists2.addTransition("GET", rsm1);
		
		assertEquals("GET", exists.getTransition(initial2).getCommand().getMethod());
		assertEquals("/entity/2", exists.getTransition(initial2).getCommand().getPath());
	}
	
	@Test
	public void testEquality() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/");
		ResourceState begin2 = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/");
		assertEquals(begin, begin2);
		assertEquals(begin.hashCode(), begin2.hashCode());
	}

	@Test
	public void testInequality() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", new ArrayList<Action>(), "/");
		assertFalse(begin.equals(end));
		assertFalse(begin.hashCode() == end.hashCode());
	}

	@Test
	public void testEqualityEntity() {
		String STATE_NAME = "pseudo";
		ResourceState one = new ResourceState("entity1", STATE_NAME, new ArrayList<Action>(), "/");
		ResourceState two = new ResourceState("entity1", STATE_NAME, new ArrayList<Action>(), "/");
		assertEquals(one, two);
		assertEquals(one.hashCode(), two.hashCode());
	}

	@Test
	public void testInequalityEntity() {
		String STATE_NAME = "pseudo";
		ResourceState one = new ResourceState("entity1", STATE_NAME, new ArrayList<Action>(), "/");
		ResourceState two = new ResourceState("entity2", STATE_NAME, new ArrayList<Action>(), "/");
		assertFalse(one.equals(two));
		assertFalse(one.hashCode() == two.hashCode());
	}

	@Test
	public void testEndState() {
		String ENTITY_NAME = "entity";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "/");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", new ArrayList<Action>(), "/");
		begin.addTransition("DELETE", end);
		assertFalse(begin.isFinalState());
		assertTrue(end.isFinalState());
	}

	/**
	 * A pseudo state is a resource state without a resource to represent its state.
	 * e.g. a deleted state is a state of an entity resource.
	 */
	@Test
	public void testPseudoState() {
		ResourceState exists = new ResourceState("entity", "exists", new ArrayList<Action>(), "/exists");
		ResourceState deleted = new ResourceState(exists, "deleted", new ArrayList<Action>());
		assertTrue(deleted.isPseudoState());
	}

	/**
	 * A transient state is a resource state with a single AUTO transition.
	 */
	@Test
	public void testTransientState() {
		ResourceState home = new ResourceState("root", "root", new ArrayList<Action>(), "/");
		ResourceState reboot = new ResourceState("entity", "reboot", new ArrayList<Action>(), "/reboot");
		home.addTransition("POST", reboot);
		reboot.addTransition(home);
		assertTrue(reboot.isTransientState());
	}

	/**
	 * A transient state is a resource state with a single AUTO transition, get the
	 * target state.
	 */
	@Test
	public void testTransientTarget() {
		ResourceState home = new ResourceState("root", "root", new ArrayList<Action>(), "/");
		ResourceState reboot = new ResourceState("entity", "reboot", new ArrayList<Action>(), "/reboot");
		home.addTransition("POST", reboot);
		reboot.addTransition(home);
		assertEquals(home, reboot.getAutoTransition().getTarget());
	}

	@Test
	public void testAddMultipleTransitionsToSameState() {
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports('{id}')", null, new UriSpecification("airport", "/Airports('{id}')"));
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", new ArrayList<Action>(), "/Flights()", null, null);

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("filter", "arrivalAirportCode eq '{code}'");
		airport.addTransition("GET", flights, uriLinkageMap);
		uriLinkageMap.put("filter", "departureAirportCode eq '{code}'");
		airport.addTransition("GET", flights, uriLinkageMap);
		airport.addTransition("PUT", flights, null, null);
		
		assertEquals(3, airport.getTransitions(flights).size());
		List<Transition> transitions = airport.getTransitions(flights);
		assertTrue(containsTransitionWithLinkParameter(transitions, "Airport.airport>GET>Flight.Flights", "filter", "departureAirportCode eq '{code}'"));
		assertTrue(containsTransitionWithLinkParameter(transitions, "Airport.airport>GET>Flight.Flights", "filter", "arrivalAirportCode eq '{code}'"));
		assertTrue(containsTransition(transitions, "Airport.airport>PUT>Flight.Flights", null));
	}

	@Test
	public void testLinkProperty() {
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports('{id}')", null, new UriSpecification("airport", "/Airports('{id}')"));
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", new ArrayList<Action>(), "/Flights()", null, null);

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("filter", "arrivalAirportCode eq '{code}'");
		airport.addTransition("GET", flights, uriLinkageMap);
		uriLinkageMap.put("filter", "departureAirportCode eq '{code}'");
		airport.addTransition("GET", flights, uriLinkageMap);
		
		assertEquals(2, airport.getTransitions(flights).size());
		List<Transition> transitions = airport.getTransitions(flights);
		assertTrue(containsTransitionWithLinkParameter(transitions, "Airport.airport>GET>Flight.Flights", "filter", "departureAirportCode eq '{code}'"));
		assertTrue(containsTransitionWithLinkParameter(transitions, "Airport.airport>GET>Flight.Flights", "filter", "arrivalAirportCode eq '{code}'"));
	}

	@Test
	public void testReplaceLinkPropertyTemplates() {
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports('{id}')", null, new UriSpecification("airport", "/Airports('{id}')"));
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", new ArrayList<Action>(), "/Airports('{id}')/Flights", null, null);

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("filter", "arrivalAirportCode eq '{code}'");
		uriLinkageMap.put("id", "{code}");
		airport.addTransition("GET", flights, uriLinkageMap);
		
		assertEquals(1, airport.getTransitions(flights).size());
		List<Transition> transitions = airport.getTransitions(flights);
		assertTrue(containsTransitionWithLinkParameter(transitions, "Airport.airport>GET>Flight.Flights", "filter", "arrivalAirportCode eq '{code}'"));
	}

	@Test
	public void testReplaceMultipleLinkPropertyTemplates() {
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports('{id}')", null, new UriSpecification("airport", "/Airports('{id}')"));
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", new ArrayList<Action>(), "/Flights()", null, null);

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("filter", "arrivalAirportCode eq '{code}'");
		uriLinkageMap.put("id", "{code}");
		airport.addTransition("GET", flights, uriLinkageMap);
		uriLinkageMap.put("filter", "departureAirportCode eq '{code}'");
		uriLinkageMap.put("id", "{code}");
		airport.addTransition("GET", flights, uriLinkageMap);
		
		assertEquals(2, airport.getTransitions(flights).size());
		List<Transition> transitions = airport.getTransitions(flights);
		assertTrue(containsTransitionWithLinkParameter(transitions, "Airport.airport>GET>Flight.Flights", "filter", "departureAirportCode eq '{code}'"));
		assertTrue(containsTransitionWithLinkParameter(transitions, "Airport.airport>GET>Flight.Flights", "filter", "arrivalAirportCode eq '{code}'"));
	}
	
	@Test
	public void testActionParameters() {
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports('{id}')", null, new UriSpecification("airport", "/Airports('{id}')"));
		List<Action> flightsActions = new ArrayList<Action>();
		Properties actionViewProperties = new Properties();
		actionViewProperties.put("entity", "Customer");
		actionViewProperties.put("filter", "myfilter");
		flightsActions.add(new Action("GetMyEntities", Action.TYPE.VIEW, actionViewProperties));
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", flightsActions, "/Flights()", null, null);

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("myfilter", "arrivalAirportCode eq '{code}'");
		uriLinkageMap.put("id", "{code}");
		airport.addTransition("GET", flights, uriLinkageMap);
		
		List<Action> actions = flights.getActions();
		assertEquals(1, actions.size());
		assertEquals("GetMyEntities", actions.get(0).getName());
		Properties props = actions.get(0).getProperties();
		assertEquals(2, props.size());
		assertEquals("Customer", props.get("entity"));
		assertTrue(props.get("filter") instanceof ActionPropertyReference);
		assertEquals("myfilter", ((ActionPropertyReference) props.get("filter")).getKey());
		assertEquals("arrivalAirportCode eq '{code}'", ((ActionPropertyReference) props.get("filter")).getProperty("__code"));
	}

	@Test
	public void testActionPropertiesReferecingMultipleParameters() {
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports('{id}')", null, new UriSpecification("airport", "/Airports('{id}')"));
		List<Action> flightsActions = new ArrayList<Action>();
		Properties actionViewProperties = new Properties();
		actionViewProperties.put("entity", "Customer");
		actionViewProperties.put("filter", "myfilter");
		flightsActions.add(new Action("GetMyEntities", Action.TYPE.VIEW, actionViewProperties));
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", flightsActions, "/Flights()", null, null);

		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("myfilter", "arrivalAirportCode eq '{arrivalAirportCode}'");
		airport.addTransition("GET", flights, uriLinkageMap);
		uriLinkageMap.put("myfilter", "departureAirportCode eq '{departureAirportCode}'");
		airport.addTransition("GET", flights, uriLinkageMap);
		
		List<Action> actions = flights.getActions();
		assertEquals(1, actions.size());
		assertEquals("GetMyEntities", actions.get(0).getName());
		Properties props = actions.get(0).getProperties();
		assertEquals(2, props.size());
		assertEquals("Customer", props.get("entity"));
		assertTrue(props.get("filter") instanceof ActionPropertyReference);
		assertEquals("myfilter", ((ActionPropertyReference) props.get("filter")).getKey());
		assertEquals("arrivalAirportCode eq '{arrivalAirportCode}'", ((ActionPropertyReference) props.get("filter")).getProperty("__arrivalAirportCode"));
		assertEquals("departureAirportCode eq '{departureAirportCode}'", ((ActionPropertyReference) props.get("filter")).getProperty("__departureAirportCode"));
	}
	
	private boolean containsTransition(List<Transition> transitions, String id, String label) {
		for(Transition t : transitions) {
			if(t.getId().equals(id) && (t.getLabel() == null && label == null || t.getLabel().equals(label))) {
				return true;
			}
		}
		System.out.println("NOT FOUND id: ["+id+"], label: ["+label+"]");
		for(Transition t : transitions) {
			System.out.println("id: ["+t.getId()+"], label: ["+t.getLabel()+"]");
		}
		return false;
	}

	private boolean containsTransitionWithLinkParameter(List<Transition> transitions, String id, String linkParam, String expectedLinkParamValue) {
		for(Transition t : transitions) {
			Map<String, String> linkParameters = t.getCommand().getUriParameters();
			if(t.getId().equals(id) && linkParameters.get(linkParam).equals(expectedLinkParamValue)) {
				return true;
			}
		}
		System.out.println("NOT FOUND id: ["+id+"], linkParam: ["+linkParam+"], expectedLinkParamValue: ["+expectedLinkParamValue+"]");
		for(Transition t : transitions) {
			Map<String, String> linkParameters = t.getCommand().getUriParameters();
			System.out.println("id: ["+t.getId()+"], linkParam: ["+linkParam+"], expectedLinkParamValue: ["+linkParameters.get(linkParam)+"]");
		}
		return false;
	}
}
