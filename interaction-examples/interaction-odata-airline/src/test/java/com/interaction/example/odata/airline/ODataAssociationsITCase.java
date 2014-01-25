package com.interaction.example.odata.airline;

/*
 * #%L
 * interaction-example-odata-airline
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
import static org.junit.Assert.assertTrue;

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
	
	private final static String NON_STRICT_ODATA_COMPLIANCE_URI_SUFFIX = "NonStrictOData";
	
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
	@Test
	public void getFlightsLinksToFlightSchedule() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		Enumerable<OEntity> flights = consumer.getEntities(FLIGHT_ENTITYSET_NAME).execute();
		assertEquals(4, flights.toSet().size());
		for (OEntity flight : flights.toSet()) {
			Long flightID = (Long) flight.getProperty("flightID").getValue();
			Long flightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();

			assertEquals(1, flight.getLinks().size());
			// there should be one link to one flight schedule for this flight
			assertTrue(containsLink(flight.getLinks(), FLIGHT_ENTITYSET_NAME + "(" + flightID + ")/flightSchedule") ||
					containsLink(flight.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + flightScheduleNum + ")"));
		}
	}

	/**
	 * GET item, check link to another entity
	 */
	@Test
	public void getFlightLinksToFlightSchedule() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		OEntity flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 2).execute();
		Long flightID = (Long) flight.getProperty("flightID").getValue();
		assertEquals(2, flightID.intValue());
		Long flightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();

		// there should be one link to one flight schedule for this flight
		assertEquals(1, flight.getLinks().size());
		assertTrue(containsLink(flight.getLinks(), FLIGHT_ENTITYSET_NAME + "(" + flightID + ")/flightSchedule") ||
				containsLink(flight.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + flightScheduleNum + ")"));
	}

	/**
	 * GET collection, check link to self and other entities for each item
	 */
	@Test
	public void getFlightSchedulesLinksToFlightSchedule() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		Enumerable<OEntity> flightSchedules = consumer.getEntities(FLIGHT_SCHEDULE_ENTITYSET_NAME).execute();
		// if the test is run multiple times it will be greater than 7
		assertTrue(flightSchedules.toSet().size() >= 7);
		for (OEntity flightSchedule : flightSchedules.toSet()) {
			Long id = (Long) flightSchedule.getProperty("flightScheduleID").getValue();
			String departureAirportCode = (String) flightSchedule.getProperty("departureAirportCode").getValue();
			String arrivalAirportCode = (String) flightSchedule.getProperty("arrivalAirportCode").getValue();

			// there should be one link to self
			// there should be one link to one departureAirport for this flight schedule
			// there should be one link to one departureAirport for this flight schedule
			if(consumer.getServiceRootUri().contains(NON_STRICT_ODATA_COMPLIANCE_URI_SUFFIX)) {
				assertTrue(containsLink(flightSchedule.getLinks(), "Flights()?filter=flightScheduleNum+eq+'" + id + "'", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Flights"));
				assertTrue(containsLink(flightSchedule.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + departureAirportCode + "')", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Airport"));
				assertTrue(containsLink(flightSchedule.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + arrivalAirportCode + "')", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Airport"));
			} else {
				assertTrue(containsLink(flightSchedule.getLinks(), "FlightSchedules(" + id + ")/flights", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Flights"));
				assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")/departureAirport", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Airport"));
				assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")/arrivalAirport", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Airport"));
			}
			assertEquals(3, flightSchedule.getLinks().size());
		}
	}

	/**
	 * GET item, check link to another entity
	 */
	@Test
	public void getFlightScheduleLinksToAirports() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		OEntity flightSchedule = consumer.getEntity(FLIGHT_SCHEDULE_ENTITYSET_NAME, 2).execute();
		Long id = (Long) flightSchedule.getProperty("flightScheduleID").getValue();
		String departureAirportCode = (String) flightSchedule.getProperty("departureAirportCode").getValue();
		String arrivalAirportCode = (String) flightSchedule.getProperty("arrivalAirportCode").getValue();
		assertEquals(2, id.intValue());

		// there should be one link to one departureAirport for this flight schedule
		// there should be one link to one arrivalAirport for this flight schedule
		if(consumer.getServiceRootUri().contains(NON_STRICT_ODATA_COMPLIANCE_URI_SUFFIX)) {
			assertTrue(containsLink(flightSchedule.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + departureAirportCode + "')", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Airport"));
			assertTrue(containsLink(flightSchedule.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + arrivalAirportCode + "')", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Airport"));
		} else {
			assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")/departureAirport", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Airport"));
			assertTrue(containsLink(flightSchedule.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "(" + id + ")/arrivalAirport", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Airport"));
		}
		assertEquals(3, flightSchedule.getLinks().size());
	}

	/**
	 * GET nav properties for an item
	 */
	@Test
	public void getFlightScheduleNavProperties() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();
		// now follow links to departure airport and arrival airport
		if(!consumer.getServiceRootUri().contains(NON_STRICT_ODATA_COMPLIANCE_URI_SUFFIX)) {
			//do this only for strict odata
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
	}
	
	/**
	 * GET collection, check link to self and other entities for each item
	 */
	@Test
	public void getAirportsLinksToAirport() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		Enumerable<OEntity> airports = consumer.getEntities(AIRPORT_ENTITYSET_NAME).execute();
		assertEquals(6, airports.toSet().size());
		for (OEntity airport : airports.toSet()) {
			String code = airport.getProperty("code").getValue().toString();

			assertEquals(2, airport.getLinks().size());
			// there should be one link to departures
			if(consumer.getServiceRootUri().contains(NON_STRICT_ODATA_COMPLIANCE_URI_SUFFIX)) {
				assertTrue(containsLink(airport.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "()?filter=departureAirportCode+eq+'" + code + "'"));
				assertTrue(containsLink(airport.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "()?filter=arrivalAirportCode+eq+'" + code + "'"));
			}
			else {
				assertTrue(containsLink(airport.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + code + "')/departures"));
				assertTrue(containsLink(airport.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + code + "')/arrivals"));
			}
		}
	}

	/**
	 * GET item, check link to another entity
	 */
	@Test
	public void getAirportLinksToArrivalsDepartures() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		OEntity airport = consumer.getEntity(AIRPORT_ENTITYSET_NAME, "LTN").execute();
		String code = airport.getProperty("code").getValue().toString();
		assertEquals("LTN", code);

		assertEquals(2, airport.getLinks().size());
		if(consumer.getServiceRootUri().contains(NON_STRICT_ODATA_COMPLIANCE_URI_SUFFIX)) {
			assertTrue(containsLink(airport.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "()?filter=arrivalAirportCode+eq+'" + code + "'", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/" + FLIGHT_SCHEDULE_ENTITYSET_NAME));
			assertTrue(containsLink(airport.getLinks(), FLIGHT_SCHEDULE_ENTITYSET_NAME + "()?filter=departureAirportCode+eq+'" + code + "'", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/" + FLIGHT_SCHEDULE_ENTITYSET_NAME));
		}
		else {
			assertTrue(containsLink(airport.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + code + "')/departures"));
			assertTrue(containsLink(airport.getLinks(), AIRPORT_ENTITYSET_NAME + "('" + code + "')/arrivals"));
		}
	}

	/**
	 * GET nav properties for an item
	 */
	@Test
	public void getAirportNavProperties() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		// now follow links to departure airport and arrival airport
		if(!consumer.getServiceRootUri().contains(NON_STRICT_ODATA_COMPLIANCE_URI_SUFFIX)) {
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
	}
	
	private boolean containsLink(List<OLink> links, String link) {
		return containsLink(links, link, null);
	}
	
	private boolean containsLink(List<OLink> links, String link, String relation) {
		assert(links != null);
		for (OLink l : links) {
			if (l.getHref().equals(link) && (relation == null || l.getRelation().equals(relation))) {
				return true;
			}
		}
		//Link not found => print debug info
		System.out.println("Links with rel [" + relation + "] and href [" + link + "] does not exist:");
		for(OLink l : links) {
			System.out.println("   Link: rel [" + l.getRelation() + "], href [" + l.getHref() + "]");
		}
		return false;
	}
}
