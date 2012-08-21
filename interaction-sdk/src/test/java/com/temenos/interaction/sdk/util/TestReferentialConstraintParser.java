package com.temenos.interaction.sdk.util;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

public class TestReferentialConstraintParser {
		
	@Test
	public void testGetLinkProperty1() {
		InputStream isEdmx = getClass().getResourceAsStream("/service.edmx");
		String linkProperty = ReferentialConstraintParser.getLinkProperty("FlightSchedule_arrivalAirport", isEdmx);
		assertEquals("arrivalAirportCode", linkProperty);
	}

	@Test
	public void testGetLinkProperty2() {
		InputStream isEdmx = getClass().getResourceAsStream("/service.edmx");
		String linkProperty = ReferentialConstraintParser.getLinkProperty("FlightSchedule_departureAirport", isEdmx);
		assertEquals("departureAirportCode", linkProperty);
	}
}
