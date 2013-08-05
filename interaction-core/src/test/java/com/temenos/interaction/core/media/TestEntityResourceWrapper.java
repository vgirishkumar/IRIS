package com.temenos.interaction.core.media;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.junit.Test;

import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.hypermedia.TransitionCommandSpec;
import com.temenos.interaction.core.resource.EntityResource;

public class TestEntityResourceWrapper {

	@Test
	public void testFindEntityGetLink() {
		Collection<Link> links = new ArrayList<Link>();
		Link linkToEntityOtherRSM = getLink(
				getMockCollectionResourceState("FlightSchedules", "FlightSchedule"), 
				getMockResourceState("airport", "Airport"), 
				"airport");
		Link linkToEntitySameRSM = getLink(
				getMockCollectionResourceState("FlightSchedules", "FlightSchedule"), 
				getMockResourceState("flightschedule", "FlightSchedule"), 
				"flight");
		Link linkToCollectionSameRSM = getLink(
				getMockCollectionResourceState("FlightSchedules", "FlightSchedule"), 
				getMockCollectionResourceState("FlightSchedules_delayed", "FlightSchedule"), 
				"delayedFlights");
		links.add(linkToEntityOtherRSM);
		links.add(linkToEntitySameRSM);
		links.add(linkToCollectionSameRSM);
		
		EntityResource<Map<String, Object>> er = new EntityResource<Map<String, Object>>();
		er.setLinks(links);
		EntityResourceWrapper erw = new EntityResourceWrapper(er);
		Link l = erw.getEntityGetLink();
		assertEquals(linkToEntitySameRSM.getId(), l.getId());
	}

	@Test
	public void testFindEntityGetLinkWithSelfRelation() {
		Collection<Link> links = new ArrayList<Link>();
		Link linkToEntityOtherRSM = getLink(
				getMockCollectionResourceState("FlightSchedules", "FlightSchedule"), 
				getMockResourceState("airport", "Airport"), 
				"airport");
		Link linkToEntitySameRSM = getLink(
				getMockResourceState("flightschedule", "FlightSchedule"), 
				getMockResourceState("flightschedule", "FlightSchedule"), 
				"self");
		Link linkToCollectionSameRSM = getLink(
				getMockCollectionResourceState("FlightSchedules", "FlightSchedule"), 
				getMockCollectionResourceState("FlightSchedules_delayed", "FlightSchedule"), 
				"delayedFlights");
		links.add(linkToEntityOtherRSM);
		links.add(linkToEntitySameRSM);
		links.add(linkToCollectionSameRSM);
		
		EntityResource<Map<String, Object>> er = new EntityResource<Map<String, Object>>();
		er.setLinks(links);
		EntityResourceWrapper erw = new EntityResourceWrapper(er);
		Link l = erw.getEntityGetLink();
		assertEquals(linkToEntitySameRSM.getId(), l.getId());
	}

	@Test
	public void testFindEntityGetLinkWithoutSelfRelation() {
		Collection<Link> links = new ArrayList<Link>();
		Link linkToEntityOtherRSM = getLink(
				getMockCollectionResourceState("FlightSchedules", "FlightSchedule"), 
				getMockResourceState("airport", "Airport"), 
				"airport");
		Link linkToEntitySameRSM = getLink(
				getMockResourceState("flightschedule", "FlightSchedule"), 
				getMockResourceState("flightschedule", "FlightSchedule"), 
				"flightschedule");
		Link linkToCollectionSameRSM = getLink(
				getMockCollectionResourceState("FlightSchedules", "FlightSchedule"), 
				getMockCollectionResourceState("FlightSchedules_delayed", "FlightSchedule"), 
				"delayedFlights");
		links.add(linkToEntityOtherRSM);
		links.add(linkToEntitySameRSM);
		links.add(linkToCollectionSameRSM);
		
		EntityResource<Map<String, Object>> er = new EntityResource<Map<String, Object>>();
		er.setLinks(links);
		EntityResourceWrapper erw = new EntityResourceWrapper(er);
		Link l = erw.getEntityGetLink();
		assertNull(l);
	}
	
	private Link getLink(ResourceState sourceState, ResourceState targetState, String linkRelation) {
		TransitionCommandSpec cs = mock(TransitionCommandSpec.class);
		when(cs.getMethod()).thenReturn(HttpMethod.GET);

		Transition t = mock(Transition.class);
		when(t.getLabel()).thenReturn(null);
		when(t.getSource()).thenReturn(sourceState);
		when(t.getTarget()).thenReturn(targetState);
		when(t.getCommand()).thenReturn(cs);
		return new Link(t, linkRelation, "http://localhost:8080/example/FlightSchedules(EI123)", "GET");
	}
	
	private ResourceState getMockResourceState(String name, String entityName) {
		ResourceState state = mock(ResourceState.class);
		when(state.getName()).thenReturn(name);
		when(state.getEntityName()).thenReturn(entityName);
		when(state.getRel()).thenReturn("item");
		return state;
	}

	private ResourceState getMockCollectionResourceState(String name, String entityName) {
		CollectionResourceState state = mock(CollectionResourceState.class);
		when(state.getName()).thenReturn(name);
		when(state.getEntityName()).thenReturn(entityName);
		when(state.getRel()).thenReturn("collection");
		return state;
	}
}
