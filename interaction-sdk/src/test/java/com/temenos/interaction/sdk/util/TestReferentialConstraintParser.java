package com.temenos.interaction.sdk.util;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

public class TestReferentialConstraintParser {
	public final static String EDMX_AIRLINE_FILE = "/airlines.edmx";
		
	@Test
	public void testGetDependentProperty1() {
		InputStream isEdmx = getClass().getResourceAsStream(EDMX_AIRLINE_FILE);
		String linkProperty = ReferentialConstraintParser.getDependent("FlightSchedule_arrivalAirport", isEdmx);
		assertEquals("arrivalAirportCode", linkProperty);
	}

	@Test
	public void testGetDependentProperty2() {
		InputStream isEdmx = getClass().getResourceAsStream(EDMX_AIRLINE_FILE);
		String linkProperty = ReferentialConstraintParser.getDependent("FlightSchedule_departureAirport", isEdmx);
		assertEquals("departureAirportCode", linkProperty);
	}

	@Test
	public void testGetPrincipalProperty1() {
		InputStream isEdmx = getClass().getResourceAsStream(EDMX_AIRLINE_FILE);
		String prop = ReferentialConstraintParser.getPrincipal("FlightSchedule_arrivalAirport", isEdmx);
		assertEquals("code", prop);
	}

	@Test
	public void testGetPrincipalProperty2() {
		InputStream isEdmx = getClass().getResourceAsStream(EDMX_AIRLINE_FILE);
		String prop = ReferentialConstraintParser.getPrincipal("FlightSchedule_departureAirport", isEdmx);
		assertEquals("code", prop);
	}
}
