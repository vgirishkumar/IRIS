package com.temenos.interaction.sdk.util;

/*
 * #%L
 * interaction-sdk
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
