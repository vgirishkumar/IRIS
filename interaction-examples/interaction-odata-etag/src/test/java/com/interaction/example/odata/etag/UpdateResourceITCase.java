package com.interaction.example.odata.etag;

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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

public class UpdateResourceITCase {

	private final static String FLIGHT_ENTITYSET_NAME = "Flights";

	protected ODataConsumer getODataConsumer(OClientBehavior clientBehaviour) {
		return ODataJerseyConsumer.newBuilder(
				ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).
				setClientBehaviors(clientBehaviour).
				build();
	}
	
	@Test
	public void getFlightWithEtag() {
		ExtendedOClientBehaviour behaviour = new ExtendedOClientBehaviour();
		ODataConsumer consumer = getODataConsumer(behaviour);
		
		OEntity flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 1).execute();
		assertEquals("Flight[flightID=1, flightScheduleNum=1, takeoffTime=1996-08-20T00:00:00.000]", behaviour.getEtag());		//The odata4j jersey consumer does not appear to support etags 
		Long flightID = (Long) flight.getProperty("flightID").getValue();
		assertEquals(1, flightID.intValue());

		flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 2).execute();
		assertEquals("Flight[flightID=2, flightScheduleNum=2055, takeoffTime=2012-08-12T13:21:05.000]", behaviour.getEtag());		//The odata4j jersey consumer does not appear to support etags 
		flightID = (Long) flight.getProperty("flightID").getValue();
		assertEquals(2, flightID.intValue());
	}

	@Test
	public void getFlightWithEtagNotModified() {
		ExtendedOClientBehaviour behaviour = new ExtendedOClientBehaviour();
		ODataConsumer consumer = getODataConsumer(behaviour);
		
		OEntity flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 1).execute();
		String etagFlight1 = behaviour.getEtag();
		assertEquals("Flight[flightID=1, flightScheduleNum=1, takeoffTime=1996-08-20T00:00:00.000]", etagFlight1);		//The odata4j jersey consumer does not appear to support etags 
		Long flightID = (Long) flight.getProperty("flightID").getValue();
		assertEquals(1, flightID.intValue());

		behaviour.setIfNoneMatch(etagFlight1);
		try {
			consumer.getEntity(FLIGHT_ENTITYSET_NAME, 1).execute();
			fail("Should have thrown a 304 Not modified exception.");
		}
		catch(RuntimeException re) {
			assertTrue(re.getMessage().contains(Status.NOT_MODIFIED.getReasonPhrase()));
		}
	}

	@Test
	public void updateFlight() {
		ExtendedOClientBehaviour behaviour = new ExtendedOClientBehaviour();
		ODataConsumer consumer = getODataConsumer(behaviour);
		
		//Get flight
		OEntity flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 1).execute();
		Long originalFlightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();
		assertEquals("Flight[flightID=1, flightScheduleNum=1, takeoffTime=1996-08-20T00:00:00.000]", behaviour.getEtag());		//The odata4j jersey consumer does not appear to support etags 
		Long flightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();
		assertEquals(1, flightScheduleNum.intValue());

		//Modify flight
		List<OProperty<?>> props = new ArrayList<OProperty<?>>();
		props.add(flight.getProperty("flightID"));
		props.add(OProperties.simple("flightScheduleNum", new Long(2)));
		props.add(flight.getProperty("takeoffTime"));
    	OEntity entity = OEntities.create(flight.getEntitySet(), flight.getEntityKey(), props, null);
		consumer.updateEntity(entity).execute();

		//Check flight has been modified
		flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 1).execute();
		assertEquals("Flight[flightID=1, flightScheduleNum=2, takeoffTime=1996-08-20T00:00:00.000]", behaviour.getEtag());		//The odata4j jersey consumer does not appear to support etags 
		flightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();
		assertEquals(2, flightScheduleNum.intValue());

		//Undo changes
		props = new ArrayList<OProperty<?>>();
		props.add(flight.getProperty("flightID"));
		props.add(OProperties.simple("flightScheduleNum", new Long(originalFlightScheduleNum)));
		props.add(flight.getProperty("takeoffTime"));
    	entity = OEntities.create(flight.getEntitySet(), flight.getEntityKey(), props, null);
		consumer.updateEntity(entity).execute();
	}

	@Test
	public void updateCachedFlightSuccess() {
		ExtendedOClientBehaviour behaviour = new ExtendedOClientBehaviour();
		ODataConsumer consumer = getODataConsumer(behaviour);
		
		//Get flight
		OEntity flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 1).execute();
		Long originalFlightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();
		String etagFlight1 = behaviour.getEtag();
		assertEquals("Flight[flightID=1, flightScheduleNum=1, takeoffTime=1996-08-20T00:00:00.000]", etagFlight1);		//The odata4j jersey consumer does not appear to support etags 
		Long flightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();
		assertEquals(1, flightScheduleNum.intValue());

		//Modify flight
		List<OProperty<?>> props = new ArrayList<OProperty<?>>();
		props.add(flight.getProperty("flightID"));
		props.add(OProperties.simple("flightScheduleNum", new Long(2)));
		props.add(flight.getProperty("takeoffTime"));
    	OEntity entity = OEntities.create(flight.getEntitySet(), flight.getEntityKey(), props, null);
		behaviour.setIfMatch(etagFlight1);				//Set If-Match header
		consumer.updateEntity(entity).execute();

		//Check flight has been modified
		flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 1).execute();
		assertEquals("Flight[flightID=1, flightScheduleNum=2, takeoffTime=1996-08-20T00:00:00.000]", behaviour.getEtag());		//The odata4j jersey consumer does not appear to support etags 
		flightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();
		assertEquals(2, flightScheduleNum.intValue());

		//Undo changes
		props = new ArrayList<OProperty<?>>();
		props.add(flight.getProperty("flightID"));
		props.add(OProperties.simple("flightScheduleNum", new Long(originalFlightScheduleNum)));
		props.add(flight.getProperty("takeoffTime"));
    	entity = OEntities.create(flight.getEntitySet(), flight.getEntityKey(), props, null);
		behaviour.setIfMatch(null);
		consumer.updateEntity(entity).execute();
	}

	@Test
	public void updateCachedFlightConflict() {
		ExtendedOClientBehaviour behaviour = new ExtendedOClientBehaviour();
		ODataConsumer consumer = getODataConsumer(behaviour);
		
		//Get flight
		OEntity flight = consumer.getEntity(FLIGHT_ENTITYSET_NAME, 1).execute();
		Long originalFlightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();
		String etagFlight1 = behaviour.getEtag();
		assertEquals("Flight[flightID=1, flightScheduleNum=1, takeoffTime=1996-08-20T00:00:00.000]", etagFlight1);		//The odata4j jersey consumer does not appear to support etags 
		Long flightScheduleNum = (Long) flight.getProperty("flightScheduleNum").getValue();
		assertEquals(1, flightScheduleNum.intValue());

		//Simulate someone else modifying this flight
		List<OProperty<?>> props = new ArrayList<OProperty<?>>();
		props.add(flight.getProperty("flightID"));
		props.add(OProperties.simple("flightScheduleNum", new Long(2062)));
		props.add(flight.getProperty("takeoffTime"));
    	OEntity entity = OEntities.create(flight.getEntitySet(), flight.getEntityKey(), props, null);
		consumer.updateEntity(entity).execute();

		//Modify flight
		props = new ArrayList<OProperty<?>>();
		props.add(flight.getProperty("flightID"));
		props.add(OProperties.simple("flightScheduleNum", new Long(2)));
		props.add(flight.getProperty("takeoffTime"));
    	entity = OEntities.create(flight.getEntitySet(), flight.getEntityType(), flight.getEntityKey(), etagFlight1, props, null);
		try {
			behaviour.setIfMatch(etagFlight1);				//The odata4j jersey consumer does not appear to support etags
			consumer.updateEntity(entity).ifMatch(etagFlight1).execute();
			fail("Should have thrown a 412 Conflict exception.");
		}
		catch(RuntimeException re) {
			assertTrue(re.getMessage().contains(Status.PRECONDITION_FAILED.getReasonPhrase()));
		}
		finally {
			//Undo changes
			props = new ArrayList<OProperty<?>>();
			props.add(flight.getProperty("flightID"));
			props.add(OProperties.simple("flightScheduleNum", new Long(originalFlightScheduleNum)));
			props.add(flight.getProperty("takeoffTime"));
	    	entity = OEntities.create(flight.getEntitySet(), flight.getEntityKey(), props, null);
			behaviour.setIfMatch(null);
			consumer.updateEntity(entity).execute();
		}
	}
}
