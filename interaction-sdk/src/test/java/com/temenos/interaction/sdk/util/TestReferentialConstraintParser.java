package com.temenos.interaction.sdk.util;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

public class TestReferentialConstraintParser {
	public final static String EDMX_AIRLINE_FILE = "/airlines.edmx";
		
	@Test
	public void testGetLinkProperty1() {
		InputStream isEdmx = getClass().getResourceAsStream(EDMX_AIRLINE_FILE);
		String linkProperty = ReferentialConstraintParser.getLinkProperty("FlightSchedule_arrivalAirport", isEdmx);
		assertEquals("arrivalAirportCode", linkProperty);
	}

	@Test
	public void testGetLinkProperty2() {
		InputStream isEdmx = getClass().getResourceAsStream(EDMX_AIRLINE_FILE);
		String linkProperty = ReferentialConstraintParser.getLinkProperty("FlightSchedule_departureAirport", isEdmx);
		assertEquals("departureAirportCode", linkProperty);
	}
}
