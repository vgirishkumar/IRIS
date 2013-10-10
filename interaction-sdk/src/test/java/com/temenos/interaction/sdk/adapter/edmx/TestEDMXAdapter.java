package com.temenos.interaction.sdk.adapter.edmx;

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


import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationEnd;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;

import com.temenos.interaction.sdk.EntityInfo;
import com.temenos.interaction.sdk.FieldInfo;
import com.temenos.interaction.sdk.JoinInfo;

public class TestEDMXAdapter {

	@Test
	public void testCreateJPAEntitySupportedType() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		
		EdmType t = mock(EdmType.class);
		assertNull(adapter.createEntityInfoFromEdmEntityType(t, new HashMap<String, String>()));
	}
	
	@Test
	public void testEntityInfoEdmx() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		
		List<String> keys = new ArrayList<String>();
		keys.add("ID");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		properties.add(EdmProperty.newBuilder("ID").setType(EdmSimpleType.INT64));

		EdmEntityType.Builder entityTypeBuilder = EdmEntityType.newBuilder();
		entityTypeBuilder.setNamespace("AirlineModel");
		entityTypeBuilder.setName("Flight");
		entityTypeBuilder.addKeys(keys);
		entityTypeBuilder.addProperties(properties);
		EdmEntityType t = entityTypeBuilder.build();
		EntityInfo p = adapter.createEntityInfoFromEdmEntityType(t, new HashMap<String, String>());
		
		assertEquals("Flight", p.getClazz());
		assertEquals("AirlineModel", p.getPackage());
		assertEquals("AirlineModel.Flight", p.getFQTypeName());
	}

	@Test
	public void testJPAEntityKeyInfoEdmx() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		
		List<String> keys = new ArrayList<String>();
		keys.add("flightID");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		properties.add(EdmProperty.newBuilder("flightID").setType(EdmSimpleType.INT64));

		EdmEntityType.Builder entityTypeBuilder = EdmEntityType.newBuilder();
		entityTypeBuilder.setNamespace("AirlineModel");
		entityTypeBuilder.setName("Flight");
		entityTypeBuilder.addKeys(keys);
		entityTypeBuilder.addProperties(properties);
		EdmEntityType t = entityTypeBuilder.build();
		EntityInfo p = adapter.createEntityInfoFromEdmEntityType(t, new HashMap<String, String>());
		
		assertEquals("flightID", p.getKeyInfo().getName());
		assertEquals("Long", p.getKeyInfo().getType());
		assertEquals(0, p.getFieldInfos().size());
	}

	@Test
	public void testJPAEntityFieldInfoEdmx() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		
		List<String> keys = new ArrayList<String>();
		keys.add("ID");
		
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		properties.add(EdmProperty.newBuilder("ID").setType(EdmSimpleType.INT64));
		properties.add(EdmProperty.newBuilder("flightID").setType(EdmSimpleType.INT64));
		properties.add(EdmProperty.newBuilder("number").setType(EdmSimpleType.STRING));
		properties.add(EdmProperty.newBuilder("fitHostiesName").setType(EdmSimpleType.STRING).setNullable(true));
		properties.add(EdmProperty.newBuilder("runway").setType(EdmSimpleType.STRING));
		properties.add(EdmProperty.newBuilder("passengers").setType(EdmSimpleType.INT32));
		properties.add(EdmProperty.newBuilder("departureDT").setType(EdmSimpleType.DATETIME));
		properties.add(EdmProperty.newBuilder("dinnerServed").setType(EdmSimpleType.TIME));
		
		EdmEntityType.Builder entityTypeBuilder = EdmEntityType.newBuilder();
		entityTypeBuilder.setNamespace("AirlineModel");
		entityTypeBuilder.setName("Flight");
		entityTypeBuilder.addKeys(keys);
		entityTypeBuilder.addProperties(properties);
		EdmEntityType t = entityTypeBuilder.build();
		EntityInfo p = adapter.createEntityInfoFromEdmEntityType(t, new HashMap<String, String>());
		
		assertEquals(7, p.getFieldInfos().size());
		assertTrue(p.getFieldInfos().contains(new FieldInfo("flightID", "Long", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("number", "String", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("fitHostiesName", "String", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("runway", "String", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("passengers", "Integer", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("departureDT", "java.util.Date", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("dinnerServed", "java.util.Date", null)));
	}

	@Test
	public void testJPAEntityOneToManyJoinInfoEdmx() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		String namespace = "AirlineModel";

		// Airport entity
		EdmEntityType.Builder bAirportEntityType = createAirportEntity(namespace);

		// Flight entity
		EdmEntityType.Builder bFlightEntityType = createFlightEntity(namespace);
		
		// add the flights relationship from Airport to Flight
		String afRelationName = "Airport_Flight";
		EdmAssociationEnd.Builder afSourceRole = EdmAssociationEnd.newBuilder()
				.setRole(afRelationName + "_Source")
				.setType(bAirportEntityType)
				.setMultiplicity(EdmMultiplicity.ONE);
		EdmAssociationEnd.Builder afTargetRole = EdmAssociationEnd.newBuilder()
				.setRole(afRelationName + "_Target")
				.setType(bFlightEntityType)
				.setMultiplicity(EdmMultiplicity.MANY);
		EdmAssociation.Builder bAssociation = EdmAssociation.newBuilder()
				.setNamespace(namespace)
				.setName(afRelationName)
				.setEnds(afSourceRole, afTargetRole);

		List<EdmNavigationProperty.Builder> airportNavProperties = new ArrayList<EdmNavigationProperty.Builder>();
		airportNavProperties.add(EdmNavigationProperty
				.newBuilder("flights")
				.setFromTo(afSourceRole, afTargetRole)
				.setRelationship(bAssociation));
		bAirportEntityType.addNavigationProperties(airportNavProperties);

		EntityInfo p = adapter.createEntityInfoFromEdmEntityType(bAirportEntityType.build(), new HashMap<String, String>());
		
		assertEquals(1, p.getFieldInfos().size());
		assertTrue(p.getFieldInfos().contains(new FieldInfo("name", "String", null)));
		assertEquals(1, p.getJoinInfos().size());
		JoinInfo join = p.getJoinInfos().get(0);
		assertEquals(join, new JoinInfo("flights", "Flight", null));
		assertEquals("@OneToMany", join.getAnnotations().get(0));
	}

	@Test
	public void testJPAEntityOneToManyBidirectionalJoinInfoEdmx() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		String namespace = "AirlineModel";

		// Airport entity
		EdmEntityType.Builder bAirportEntityType = createAirportEntity(namespace);

		// Flight entity
		EdmEntityType.Builder bFlightEntityType = createFlightEntity(namespace);
		
		// add the flights relationship from Airport to Flight
		String afRelationName = "Airport_Flight";
		EdmAssociationEnd.Builder afSourceRole = EdmAssociationEnd.newBuilder()
				.setRole(afRelationName + "_Source")
				.setType(bAirportEntityType)
				.setMultiplicity(EdmMultiplicity.ONE);
		EdmAssociationEnd.Builder afTargetRole = EdmAssociationEnd.newBuilder()
				.setRole(afRelationName + "_Target")
				.setType(bFlightEntityType)
				.setMultiplicity(EdmMultiplicity.MANY);
		EdmAssociation.Builder bAssociation = EdmAssociation.newBuilder()
				.setNamespace(namespace)
				.setName(afRelationName)
				.setEnds(afSourceRole, afTargetRole);

		// join to the many flights that have left this airport
		List<EdmNavigationProperty.Builder> airportNavProperties = new ArrayList<EdmNavigationProperty.Builder>();
		airportNavProperties.add(EdmNavigationProperty
				.newBuilder("flights")
				.setFromTo(afSourceRole, afTargetRole)
				.setRelationship(bAssociation));
		bAirportEntityType.addNavigationProperties(airportNavProperties);

		// join back to the airport that this flight departed from
		List<EdmNavigationProperty.Builder> flightNavProperties = new ArrayList<EdmNavigationProperty.Builder>();
		flightNavProperties.add(EdmNavigationProperty
				.newBuilder("airport")
				.setFromTo(afTargetRole, afSourceRole)
				.setRelationship(bAssociation));
		bFlightEntityType.addNavigationProperties(flightNavProperties);
		
		Map<String, String> linkPropertyMap = new HashMap<String, String>();
		linkPropertyMap.put("Airport_Flight", "airport");
		EntityInfo p = adapter.createEntityInfoFromEdmEntityType(bAirportEntityType.build(), linkPropertyMap);
		
		assertEquals(1, p.getFieldInfos().size());
		assertTrue(p.getFieldInfos().contains(new FieldInfo("name", "String", null)));
		assertEquals(1, p.getJoinInfos().size());
		JoinInfo join = p.getJoinInfos().get(0);
		assertEquals(join, new JoinInfo("flights", "Flight", null));
		assertEquals("@OneToMany(mappedBy=\"airport\")", join.getAnnotations().get(0));
	}

	@Test
	public void testJPAEntityManyToManyJoinInfoEdmx() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		String namespace = "AirlineModel";

		// Flight entity
		EdmEntityType.Builder bFlightEntityType = createFlightEntity(namespace);
		
		// Person entity
		EdmEntityType.Builder bPersonEntityType = createPersonEntity(namespace);

		// add the passengers relationship from Flight to Person
		String relationName = "Flight_Person";
		EdmAssociationEnd.Builder fpSourceRole = EdmAssociationEnd.newBuilder()
				.setRole(relationName + "_Source")
				.setType(bFlightEntityType)
				.setMultiplicity(EdmMultiplicity.MANY);
		EdmAssociationEnd.Builder fpTargetRole = EdmAssociationEnd.newBuilder()
				.setRole(relationName + "_Target")
				.setType(bPersonEntityType)
				.setMultiplicity(EdmMultiplicity.MANY);
		EdmAssociation.Builder bAssociation = EdmAssociation.newBuilder()
				.setNamespace(namespace)
				.setName(relationName)
				.setEnds(fpSourceRole, fpTargetRole);
		
		List<EdmNavigationProperty.Builder> flightNavProperties = new ArrayList<EdmNavigationProperty.Builder>();
		flightNavProperties.add(EdmNavigationProperty
				.newBuilder("passengers")
				.setFromTo(fpSourceRole, fpTargetRole)
				.setRelationship(bAssociation));
		bFlightEntityType.addNavigationProperties(flightNavProperties);

		EntityInfo p = adapter.createEntityInfoFromEdmEntityType(bFlightEntityType.build(), new HashMap<String, String>());
		
		assertEquals(1, p.getFieldInfos().size());
		assertTrue(p.getFieldInfos().contains(new FieldInfo("departureDT", "java.util.Date", null)));
		assertEquals(1, p.getJoinInfos().size());
		JoinInfo join = p.getJoinInfos().get(0);
		assertEquals(join, new JoinInfo("passengers", "Person", null));
		assertEquals("@ManyToMany", join.getAnnotations().get(0));
	}
	
	@Test
	public void testJPAEntityManyToManyBidirectionalJoinInfoEdmx() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		String namespace = "AirlineModel";

		// Flight entity
		EdmEntityType.Builder bFlightEntityType = createFlightEntity(namespace);
		
		// Person entity
		EdmEntityType.Builder bPersonEntityType = createPersonEntity(namespace);

		// add the passengers relationship from Flight to Person
		String relationName = "Flight_Person";
		EdmAssociationEnd.Builder fpSourceRole = EdmAssociationEnd.newBuilder()
				.setRole(relationName + "_Source")
				.setType(bFlightEntityType)
				.setMultiplicity(EdmMultiplicity.MANY);
		EdmAssociationEnd.Builder fpTargetRole = EdmAssociationEnd.newBuilder()
				.setRole(relationName + "_Target")
				.setType(bPersonEntityType)
				.setMultiplicity(EdmMultiplicity.MANY);
		EdmAssociation.Builder bAssociation = EdmAssociation.newBuilder()
				.setNamespace(namespace)
				.setName(relationName)
				.setEnds(fpSourceRole, fpTargetRole);

		List<EdmNavigationProperty.Builder> flightNavProperties = new ArrayList<EdmNavigationProperty.Builder>();
		flightNavProperties.add(EdmNavigationProperty
				.newBuilder("passengers")
				.setFromTo(fpSourceRole, fpTargetRole)
				.setRelationship(bAssociation));
		bFlightEntityType.addNavigationProperties(flightNavProperties);

		List<EdmNavigationProperty.Builder> personNavProperties = new ArrayList<EdmNavigationProperty.Builder>();
		personNavProperties.add(EdmNavigationProperty
				.newBuilder("flights")
				.setFromTo(fpTargetRole, fpSourceRole)
				.setRelationship(bAssociation));
		bPersonEntityType.addNavigationProperties(personNavProperties);

		Map<String, String> linkPropertyMap = new HashMap<String, String>();
		linkPropertyMap.put("Flight_Person", "flight");
		EntityInfo p = adapter.createEntityInfoFromEdmEntityType(bFlightEntityType.build(), linkPropertyMap);
		
		assertEquals(1, p.getFieldInfos().size());
		assertTrue(p.getFieldInfos().contains(new FieldInfo("departureDT", "java.util.Date", null)));
		assertEquals(1, p.getJoinInfos().size());
		JoinInfo join = p.getJoinInfos().get(0);
		assertEquals(join, new JoinInfo("passengers", "Person", null));
		assertEquals("@ManyToMany(mappedBy=\"flight\")", join.getAnnotations().get(0));
	}
	
	private EdmEntityType.Builder createAirportEntity(String namespace) {
		EdmEntityType.Builder bAirportEntityType = EdmEntityType.newBuilder();
		bAirportEntityType.setNamespace(namespace);
		bAirportEntityType.setName("Airport");
		
		List<String> aKeys = new ArrayList<String>();
		aKeys.add("airportID");
		bAirportEntityType.addKeys(aKeys);
		
		List<EdmProperty.Builder> aProperties = new ArrayList<EdmProperty.Builder>();
		aProperties.add(EdmProperty.newBuilder("airportID").setType(EdmSimpleType.INT64));
		aProperties.add(EdmProperty.newBuilder("name").setType(EdmSimpleType.STRING));
		bAirportEntityType.addProperties(aProperties);
		return bAirportEntityType;
	}
	
	private EdmEntityType.Builder createFlightEntity(String namespace) {
		EdmEntityType.Builder bFlightEntityType = EdmEntityType.newBuilder();
		bFlightEntityType.setNamespace(namespace);
		bFlightEntityType.setName("Flight");
		
		List<String> keys = new ArrayList<String>();
		keys.add("flightID");
		bFlightEntityType.addKeys(keys);
		
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		properties.add(EdmProperty.newBuilder("flightID").setType(EdmSimpleType.INT64));
		properties.add(EdmProperty.newBuilder("departureDT").setType(EdmSimpleType.DATETIME));
		bFlightEntityType.addProperties(properties);
		return bFlightEntityType;
	}
	
	private EdmEntityType.Builder createPersonEntity(String namespace) {
		EdmEntityType.Builder bPersonEntityType = EdmEntityType.newBuilder();
		bPersonEntityType.setNamespace(namespace);
		bPersonEntityType.setName("Person");
		
		List<String> pKeys = new ArrayList<String>();
		pKeys.add("personId");
		bPersonEntityType.addKeys(pKeys);
		
		List<EdmProperty.Builder> pProperties = new ArrayList<EdmProperty.Builder>();
		pProperties.add(EdmProperty.newBuilder("personId").setType(EdmSimpleType.INT64));
		pProperties.add(EdmProperty.newBuilder("name").setType(EdmSimpleType.STRING));
		bPersonEntityType.addProperties(pProperties);
		return bPersonEntityType;
	}

	@Test
	public void testJPAEntityFieldInfoAnnotationsEdmx() throws IOException {
		EDMXAdapter adapter = new EDMXAdapter(mock(InputStream.class));
		
		List<String> keys = new ArrayList<String>();
		keys.add("ID");

		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		properties.add(EdmProperty.newBuilder("ID").setType(EdmSimpleType.INT64));
		properties.add(EdmProperty.newBuilder("departureDT").setType(EdmSimpleType.DATETIME));
		properties.add(EdmProperty.newBuilder("departureTime").setType(EdmSimpleType.TIME));

		EdmEntityType.Builder entityTypeBuilder = EdmEntityType.newBuilder();
		entityTypeBuilder.setNamespace("AirlineModel");
		entityTypeBuilder.setName("Flight");
		entityTypeBuilder.addKeys(keys);
		entityTypeBuilder.addProperties(properties);
		EdmEntityType t = entityTypeBuilder.build();
		EntityInfo p = adapter.createEntityInfoFromEdmEntityType(t, new HashMap<String, String>());

		// Annotations
		FieldInfo dateFI = p.getFieldInfos().get(0);
		assertEquals(1, dateFI.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIMESTAMP)", dateFI.getAnnotations().get(0));

		FieldInfo timeFI = p.getFieldInfos().get(1);
		assertEquals(1, timeFI.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIME)", timeFI.getAnnotations().get(0));
	}
	

}
