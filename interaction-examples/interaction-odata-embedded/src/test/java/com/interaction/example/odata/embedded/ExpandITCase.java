package com.interaction.example.odata.embedded;

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

import org.core4j.Enumerable;
import org.junit.Ignore;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.ORelatedEntityLinkInline;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

public class ExpandITCase {

	private final static String FLIGHT_ENTITYSET_NAME = "Flights";
	private final static String PASSENGER_ENTITYSET_NAME = "Passengers";

	protected ODataConsumer getODataConsumer() {
		return ODataJerseyConsumer.newBuilder(
				ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).
				build();
	}
	
	/**
	 * GET collection, check link to self and other entities for each item
	 */
	@Test
	// *+-> syntax in RIM not supported yet
	@Ignore
	public void getFlightsLinksToFlightSchedule() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		Enumerable<OEntity> flights = consumer.getEntities(FLIGHT_ENTITYSET_NAME).execute();
		assertEquals(4, flights.toSet().size());
		for (OEntity flight : flights.toSet()) {
			Long flightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();

			assertEquals(1, flight.getLinks().size());
			// there should be one inline link to one flight schedule for this flight
			OEntity inlineEntity = flight.getLink("FlightSchedule", ORelatedEntityLinkInline.class).getRelatedEntity();
		    assertEquals("The flight schedule id", flightScheduleNum, (Long) inlineEntity.getProperty("flightScheduleID").getValue());
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

		// there should be one inline link to one flight schedule for this flight
		OEntity inlineEntity = flight.getLink("FlightSchedule", ORelatedEntityLinkInline.class).getRelatedEntity();
	    assertEquals("The flight schedule id", flightScheduleNum, (Long) inlineEntity.getProperty("flightScheduleID").getValue());
	}
	
	/**
	 * GET Passenger, check inline link to Flight (embedded in the command as opposed to using the RIM '+->' transition)
	 */
	@Test
	public void getPassengerLinkToFlightEmbedded() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();

		// id doesn't matter, it's totally mocked
		OEntity passenger = consumer.getEntity(PASSENGER_ENTITYSET_NAME, 69).execute();
		String name = (String) passenger.getProperty("name").getValue();
		assertEquals("Big Ron", name);

		// there should be one inline link to one flight schedule for this flight
		OEntity inlineEntity = passenger.getLink("Flight", ORelatedEntityLinkInline.class).getRelatedEntity();
	    assertEquals((Long)2629L, (Long) inlineEntity.getProperty("flightID").getValue());
	}

}
