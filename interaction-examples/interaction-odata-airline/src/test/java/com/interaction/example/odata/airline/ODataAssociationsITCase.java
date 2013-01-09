package com.interaction.example.odata.airline;

import static org.junit.Assert.*;

import java.util.List;

import org.core4j.Enumerable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

/**
 * This test ensures that this projects OData entities have working 
 * OData associations (links).
 * 
 * @author aphethean
 */
public class ODataAssociationsITCase {

	private final static String FLIGHT_ENTITYSET_NAME = "Flights";
	private final static String FLIGHT_SCHEDULE_ENTITYSET_NAME = "FlightSchedules";
	private final static String AIRPORT_ENTITYSET_NAME = "Airports";
	
	public ODataAssociationsITCase() throws Exception {
		super();
	}
	
	@Before
	public void initTest() {}

	@After
	public void tearDown() {}

	/**
	 * GET collection, check link to self and other entities for each item
	 */
	//@Test
	public void getFlightsLinksToFlight() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpintUri(Configuration.TEST_ENDPOINT_URI)).build();

		Enumerable<OEntity> flights = consumer.getEntities(FLIGHT_ENTITYSET_NAME).execute();
		assertEquals(4, flights.toSet().size());
		for (OEntity flight : flights.toSet()) {
			Long id = (Long) flight.getProperty("flightID").getValue();
			Long flightScheduleId = (Long) flight.getProperty("flightScheduleID").getValue();

			assertEquals(2, flight.getLinks().size());
			// there should be one link to self
			assertTrue(containsLink(flight.getLinks(), FLIGHT_ENTITYSET_NAME + "(" + id + ")"));
			// there should be one link to one flight schedule for this flight
			assertTrue(containsLink(flight.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + flightScheduleId + ")"));
		}
	}

	/**
	 * GET item, check link to another entity
	 */
	@Test
	public void getFlightLinksToFlightSchedule() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpintUri(Configuration.TEST_ENDPOINT_URI)).build();

		OEntity flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 2).execute();
		Long id = (Long) flight.getProperty("flightID").getValue();
		assertEquals(2, id.intValue());

		// there should be one link to one flight schedule for this flight
		assertEquals(1, flight.getLinks().size());
		assertTrue(containsLink(flight.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(2055)"));
	}

	/**
	 * GET collection, check link to self and other entities for each item
	 */
	@Test
	public void getFlightSchedulessLinksToFlightSchedule() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpintUri(Configuration.TEST_ENDPOINT_URI)).build();

		Enumerable<OEntity> flightSchedules = consumer.getEntities(FLIGHT_SCHEDULE_ENTITYSET_NAME).execute();
		// if the test is run multiple times it will be greater than 7
		assertTrue(flightSchedules.toSet().size() >= 7);
		for (OEntity flightSchedule : flightSchedules.toSet()) {
			Long id = (Long) flightSchedule.getProperty("flightScheduleID").getValue();

			assertEquals(3, flightSchedule.getLinks().size());
			// there should be one link to self
			assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")"));
			// there should be one link to one departureAirport for this flight schedule
			assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")/departureAirport"));
			// there should be one link to one departureAirport for this flight schedule
			assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")/arrivalAirport"));
		}
	}

	/**
	 * GET item, check link to another entity
	 */
	@Test
	public void getFlightScheduleLinksToAirports() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpintUri(Configuration.TEST_ENDPOINT_URI)).build();

		OEntity flightSchedule = consumer.getEntity(FLIGHT_SCHEDULE_ENTITYSET_NAME, 2).execute();
		Long id = (Long) flightSchedule.getProperty("flightScheduleID").getValue();
		assertEquals(2, id.intValue());

		assertEquals(2, flightSchedule.getLinks().size());
		// there should be one link to one departureAirport for this flight schedule
		assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")/departureAirport"));
		// there should be one link to one departureAirport for this flight schedule
		assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")/arrivalAirport"));
	}

	/**
	 * GET nav properties for an item
	 */
	@Test
	public void getFlightScheduleNavProperties() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpintUri(Configuration.TEST_ENDPOINT_URI)).build();

		// now follow links to departure airport and arrival airport
		OEntity departureAirport = consumer
				.getEntity(FLIGHT_SCHEDULE_ENTITYSET_NAME, 2055)
				.nav("departureAirport")
				.execute();
		assertEquals("LTN", departureAirport.getProperty("code").getValue());
		OEntity arrivalAirport = consumer
				.getEntity(FLIGHT_SCHEDULE_ENTITYSET_NAME, 2055)
				.nav("arrivalAirport")
				.execute();
		assertEquals("GVA", arrivalAirport.getProperty("code").getValue());
	}
	
	/**
	 * GET collection, check link to self and other entities for each item
	 */
	@Test
	public void getAirportsLinksToAirport() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpintUri(Configuration.TEST_ENDPOINT_URI)).build();

		Enumerable<OEntity> airports = consumer.getEntities(AIRPORT_ENTITYSET_NAME).execute();
		assertEquals(6, airports.toSet().size());
		for (OEntity airport : airports.toSet()) {
			String code = airport.getProperty("code").getValue().toString();

			assertEquals(3, airport.getLinks().size());
			// there should be one link to self
			assertTrue(containsLink(airport.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + code + "')"));
			// there should be one link to departures
			assertTrue(containsLink(airport.getLinks(), "FlightSchedulesFiltered(departureAirportCode%20eq%20'MIA')"));
			// there should be one link to arrivals
			assertTrue(containsLink(airport.getLinks(), "FlightSchedulesFiltered(arrivalAirportCode%20eq%20'MIA')"));
		}
	}

	/**
	 * GET item, check link to another entity
	 */
	//@Test
	public void getAirportLinksToArrivalsDepartures() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpintUri(Configuration.TEST_ENDPOINT_URI)).build();

		OEntity airport = consumer.getEntity(AIRPORT_ENTITYSET_NAME, "LTN").execute();
		String code = airport.getProperty("code").getValue().toString();
		assertEquals("LTN", code);

		assertEquals(2, airport.getLinks().size());
		// there should be one link to one departures
		assertTrue(containsLink(airport.getLinks(), AIRPORT_ENTITYSET_NAME + "(" + code + ")/departures"));
		// there should be one link to one departureAirport for this flight schedule
		assertTrue(containsLink(airport.getLinks(), AIRPORT_ENTITYSET_NAME + "(" + code + ")/arrivals"));
	}

	/**
	 * GET nav properties for an item
	 */
	@Test
	public void getAirportNavProperties() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpintUri(Configuration.TEST_ENDPOINT_URI)).build();

		// now follow links to departure airport and arrival airport
		OEntity departuresFlightSchedule = consumer
				.getEntity(AIRPORT_ENTITYSET_NAME, "LTN")
				.nav("departures")
				.execute();
		assertEquals(2051L, departuresFlightSchedule.getProperty("flightScheduleID").getValue());
		OEntity arrivalsFlightSchedule = consumer
				.getEntity(AIRPORT_ENTITYSET_NAME, "LTN")
				.nav("arrivals")
				.execute();
		assertEquals(2052L, arrivalsFlightSchedule.getProperty("flightScheduleID").getValue());
	}
	
	private boolean containsLink(List<OLink> links, String link) {
		assert(links != null);
		boolean contains = false;
		for (OLink l : links) {
			if (l.getHref().equals(link)) {
				contains = true;
			}
		}
		return contains;
	}
	

}
