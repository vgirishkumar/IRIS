package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class TestLink {

	@Test
	public void testGetHrefTransition() throws Exception {
		Link link = new Link(null, "NoteToPersonLink", "Person", 
				"http://localhost:8080/example/interaction-odata-notes.svc/Notes(1)/Person",
				null, null, "GET", null);
		String hrefTransition = link.getHrefTransition("example/interaction-odata-notes.svc");
		assertEquals("Notes(1)/Person", hrefTransition);
	}

	@Test
	public void testTitleWithoutLabel() throws Exception {
		Transition t = mock(Transition.class);
		when(t.getLabel()).thenReturn(null);
		ResourceState state = mock(ResourceState.class);
		when(state.getName()).thenReturn("FlightSchedules");
		when(t.getTarget()).thenReturn(state);
		Link link = new Link(t, "arrivals", "http://localhost:8080/example/Airport/arrivals", "GET");

		assertEquals("FlightSchedules", link.getTitle());
		assertEquals("arrivals", link.getRel());
	}

	@Test
	public void testTitleWithEmptyLabel() throws Exception {
		Transition t = mock(Transition.class);
		when(t.getLabel()).thenReturn("");
		ResourceState state = mock(ResourceState.class);
		when(state.getName()).thenReturn("FlightSchedules");
		when(t.getTarget()).thenReturn(state);
		Link link = new Link(t, "arrivals", "http://localhost:8080/example/Airport/arrivals", "GET");

		assertEquals("FlightSchedules", link.getTitle());
		assertEquals("arrivals", link.getRel());
	}
	
	@Test
	public void testTitleWithLabel() throws Exception {
		Transition t = mock(Transition.class);
		when(t.getLabel()).thenReturn("arrivals");
		ResourceState state = mock(ResourceState.class);
		when(state.getName()).thenReturn("FlightSchedules");
		when(t.getTarget()).thenReturn(state);
		Link link = new Link(t, "arrivals", "http://localhost:8080/example/Airport/arrivals", "GET");

		assertEquals("arrivals", link.getTitle());
		assertEquals("arrivals", link.getRel());
	}
}
