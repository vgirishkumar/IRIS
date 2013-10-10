package com.temenos.interaction.odataext.entity;

/*
 * #%L
 * interaction-odata4j-ext
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmType;
import org.odata4j.edm.EdmProperty.CollectionKind;

import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataParser;
import com.temenos.interaction.core.entity.vocabulary.Term;
import com.temenos.interaction.core.entity.vocabulary.TermFactory;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.UriSpecification;
import com.temenos.interaction.odataext.entity.MetadataOData4j;

public class TestMetadataOData4j {
	public final static String METADATA_XML_FILE = "TestMetadataParser.xml";
	public final static String METADATA_AIRLINE_XML_FILE = "AirlinesMetadata.xml";
	public final static String METADATA_CUSTOMER_NON_EXP_XML_FILE = "CustomerNonExpandedMetadata.xml";
	
	private static String AIRLINE_NAMESPACE = "FlightResponderModel";
	private static Metadata metadataAirline;
	private static MetadataOData4j metadataOdata4j;
	private static MetadataOData4j metadataAirlineOdata4j;
	private static MetadataOData4j metadataCustomerNonExpandableModelOdata4j;
	
	@BeforeClass
	public static void setup()
	{
		//Read the metadata file
		TermFactory termFactory = new TermFactory() {
			public Term createTerm(String name, String value) throws Exception {
				if(name.equals("TEST_ENTITY_ALIAS")) {
					Term mockTerm = mock(Term.class);
					when(mockTerm.getValue()).thenReturn(value);
					when(mockTerm.getName()).thenReturn(name);
					return mockTerm;
				}
				else {
					return super.createTerm(name, value);
				}
			}			
		};
		MetadataParser parser = new MetadataParser(termFactory);
		InputStream is = parser.getClass().getClassLoader().getResourceAsStream(METADATA_XML_FILE);
		Metadata metadata = parser.parse(is);
		Assert.assertNotNull(metadata);
		
		//Convert metadata to odata4j metadata
		metadataOdata4j = new MetadataOData4j(metadata, new ResourceStateMachine(new ResourceState("SD", "initial", new ArrayList<Action>(), "/")));

		// Create mock state machine with entity sets
		ResourceState serviceRoot = new ResourceState("SD", "initial", new ArrayList<Action>(), "/");
		serviceRoot.addTransition(new CollectionResourceState("FlightSchedule", "FlightSchedule", new ArrayList<Action>(), "/FlightSchedule"));
		serviceRoot.addTransition(new CollectionResourceState("Flight", "Flight", new ArrayList<Action>(), "/Flight"));
		serviceRoot.addTransition(new CollectionResourceState("Airport", "Airport", new ArrayList<Action>(), "/Airline"));
		ResourceStateMachine hypermediaEngine = new ResourceStateMachine(serviceRoot);

		//Read the airline metadata file
		MetadataParser parserAirline = new MetadataParser();
		InputStream isAirline = parserAirline.getClass().getClassLoader().getResourceAsStream(METADATA_AIRLINE_XML_FILE);
		metadataAirline = parserAirline.parse(isAirline);
		Assert.assertNotNull(metadataAirline);
		
		//Convert metadata to odata4j metadata
		metadataAirlineOdata4j = new MetadataOData4j(metadataAirline, hypermediaEngine);
		
		//Read the Complex metadata file
		MetadataParser parserCustomerComplex = new MetadataParser();
		InputStream isCustomer = parserCustomerComplex.getClass().getClassLoader().getResourceAsStream(METADATA_CUSTOMER_NON_EXP_XML_FILE);
		Metadata complexMetadata = parserCustomerComplex.parse(isCustomer);
		Assert.assertNotNull(complexMetadata);
				
		//Convert metadata to odata4j metadata
		metadataCustomerNonExpandableModelOdata4j = new MetadataOData4j(complexMetadata, new ResourceStateMachine(new ResourceState("SD", "initial", new ArrayList<Action>(), "/")));
	}
	
	@Test(expected = AssertionError.class)
	public void testAssertIndividualInitialState() {
		CollectionResourceState serviceRoot = new CollectionResourceState("SD", "initial", new ArrayList<Action>(), "/");
		ResourceStateMachine hypermediaEngine = new ResourceStateMachine(serviceRoot);
		new MetadataOData4j(metadataAirline, hypermediaEngine);
	}
	
	@Test
	public void testCustomerEntity()
	{	
		EdmDataServices edmDataServices = metadataOdata4j.getMetadata();
		EdmType type = edmDataServices.findEdmEntityType("CustomerServiceTestModel.Customer");
		Assert.assertNotNull(type);
		Assert.assertTrue(type.getFullyQualifiedTypeName().equals("CustomerServiceTestModel.Customer"));
		Assert.assertTrue(type instanceof EdmEntityType);
		EdmEntityType entityType = (EdmEntityType) type;
		Assert.assertEquals("Customer", entityType.getName());
		Assert.assertEquals(false, entityType.findProperty("name").isNullable());			//ID fields must not be nullable
		Assert.assertEquals(false, entityType.findProperty("dateOfBirth").isNullable());
		Assert.assertEquals(false, entityType.findProperty("Customer_address").getType().isSimple());//address should be Complex
		Assert.assertEquals(CollectionKind.NONE, entityType.findProperty("Customer_address").getCollectionKind());//address should not be of CollectionKind
		Assert.assertEquals(null, entityType.findProperty("streetType"));					// This should not be part of EntityType
		
		
		EdmComplexType addressType = edmDataServices.findEdmComplexType("CustomerServiceTestModel.Customer_address");
		Assert.assertEquals(true, addressType.findProperty("town").getType().isSimple());
		Assert.assertEquals(true, addressType.findProperty("postCode").getType().isSimple());
		Assert.assertEquals(false, addressType.findProperty("Customer_street").getType().isSimple());
		Assert.assertEquals(CollectionKind.NONE, addressType.findProperty("Customer_street").getCollectionKind());//street should not be of CollectionKind
		
		EdmComplexType streetType = edmDataServices.findEdmComplexType("CustomerServiceTestModel.Customer_street");
		Assert.assertEquals(true, streetType.findProperty("streetType").isNullable());
		Assert.assertEquals(true, streetType.findProperty("streetType").getType().isSimple());
	}
	
	@Test
	public void testCustomerWithListTypeTAGEntity()
	{	
		EdmDataServices edmDataServices = metadataOdata4j.getMetadata();
		EdmType type = edmDataServices.findEdmEntityType("CustomerServiceTestModel.CustomerWithTermList");
		Assert.assertNotNull(type);
		Assert.assertTrue(type.getFullyQualifiedTypeName().equals("CustomerServiceTestModel.CustomerWithTermList"));
		Assert.assertTrue(type instanceof EdmEntityType);
		EdmEntityType entityType = (EdmEntityType) type;
		Assert.assertEquals("CustomerWithTermList", entityType.getName());
		Assert.assertEquals(false, entityType.findProperty("name").isNullable());			//ID fields must not be nullable
		Assert.assertEquals(false, entityType.findProperty("dateOfBirth").isNullable());
		Assert.assertEquals(false, entityType.findProperty("CustomerWithTermList_address").getType().isSimple());//address should be Complex
		Assert.assertEquals(CollectionKind.List, entityType.findProperty("CustomerWithTermList_address").getCollectionKind());//address should be List of Complex
		Assert.assertEquals(null, entityType.findProperty("streetType"));					// This should not be part of EntityType
		
		
		EdmComplexType addressType = edmDataServices.findEdmComplexType("CustomerServiceTestModel.CustomerWithTermList_address");
		Assert.assertEquals(true, addressType.findProperty("town").getType().isSimple());
		Assert.assertEquals(true, addressType.findProperty("postCode").getType().isSimple());
		Assert.assertEquals(false, addressType.findProperty("CustomerWithTermList_street").getType().isSimple());
		Assert.assertEquals(CollectionKind.NONE, addressType.findProperty("CustomerWithTermList_street").getCollectionKind());	// street should be complex but not list
		
		EdmComplexType streetType = edmDataServices.findEdmComplexType("CustomerServiceTestModel.CustomerWithTermList_street");
		Assert.assertEquals(true, streetType.findProperty("streetType").isNullable());
		Assert.assertEquals(true, streetType.findProperty("streetType").getType().isSimple());
	}

	@Test
	public void testCustomerComplexEntity() {
		EdmDataServices edmDataServices = metadataCustomerNonExpandableModelOdata4j.getMetadata();
		EdmType type = edmDataServices.findEdmEntityType("CustomerServiceTestModel.Customer");
		Assert.assertNotNull(type);
		Assert.assertTrue(type.getFullyQualifiedTypeName().equals("CustomerServiceTestModel.Customer"));
		Assert.assertTrue(type instanceof EdmEntityType);
		EdmEntityType entityType = (EdmEntityType) type;
		Assert.assertEquals("Customer", entityType.getName());
		Assert.assertEquals(false, entityType.findProperty("name").isNullable());			//ID fields must not be nullable
		Assert.assertEquals(false, entityType.findProperty("Customer_address").getType().isSimple());//address should be Complex
		Assert.assertEquals(CollectionKind.NONE, entityType.findProperty("Customer_address").getCollectionKind());//address should be List of Complex
		Assert.assertEquals(false, entityType.findProperty("dateOfBirth").isNullable());
		Assert.assertEquals(null, entityType.findProperty("streetType"));					// This should not be part of EntityType
		
		EdmComplexType addressType = edmDataServices.findEdmComplexType("CustomerServiceTestModel.Customer_address");
		Assert.assertEquals(true, addressType.findProperty("town").getType().isSimple());
		Assert.assertEquals(true, addressType.findProperty("postCode").getType().isSimple());
		Assert.assertEquals(false, addressType.findProperty("Customer_street").getType().isSimple());
		Assert.assertEquals(CollectionKind.NONE, addressType.findProperty("Customer_street").getCollectionKind());	// street should be complex only
		
		EdmComplexType streetType = edmDataServices.findEdmComplexType("CustomerServiceTestModel.Customer_street");
		Assert.assertEquals(true, streetType.findProperty("streetType").isNullable());
		Assert.assertEquals(true, streetType.findProperty("streetType").getType().isSimple());
	}
	
	@Test
	public void testCustomerWithTermListWithNonExpandableMetadata() {
		EdmDataServices edmDataServices = metadataCustomerNonExpandableModelOdata4j.getMetadata();
		EdmType type = edmDataServices.findEdmEntityType("CustomerServiceTestModel.CustomerWithTermList");
		Assert.assertNotNull(type);
		Assert.assertTrue(type.getFullyQualifiedTypeName().equals("CustomerServiceTestModel.CustomerWithTermList"));
		Assert.assertTrue(type instanceof EdmEntityType);
		EdmEntityType entityType = (EdmEntityType) type;
		Assert.assertEquals("CustomerWithTermList", entityType.getName());
		Assert.assertEquals(false, entityType.findProperty("name").isNullable());			//ID fields must not be nullable
		Assert.assertEquals(false, entityType.findProperty("CustomerWithTermList_address").getType().isSimple());//address should be Complex
		Assert.assertEquals(CollectionKind.List, entityType.findProperty("CustomerWithTermList_address").getCollectionKind());
		Assert.assertEquals(false, entityType.findProperty("dateOfBirth").isNullable());
		Assert.assertEquals(null, entityType.findProperty("streetType"));					// This should not be part of EntityType
		
		EdmComplexType addressType = edmDataServices.findEdmComplexType("CustomerServiceTestModel.CustomerWithTermList_address");
		Assert.assertEquals(true, addressType.findProperty("town").getType().isSimple());
		Assert.assertEquals(true, addressType.findProperty("postCode").getType().isSimple());
		Assert.assertEquals(false, addressType.findProperty("CustomerWithTermList_street").getType().isSimple());
		Assert.assertEquals(CollectionKind.NONE, addressType.findProperty("CustomerWithTermList_street").getCollectionKind());
		
		EdmComplexType streetType = edmDataServices.findEdmComplexType("CustomerServiceTestModel.CustomerWithTermList_street");
		Assert.assertEquals(true, streetType.findProperty("streetType").isNullable());
		Assert.assertEquals(true, streetType.findProperty("streetType").getType().isSimple());
	}
	
	@Test
	public void testAirlineSchemaCount()
	{	
		EdmDataServices edmDataServices = metadataAirlineOdata4j.getMetadata();
		Assert.assertEquals(1, edmDataServices.getSchemas().size());
	}
	
	@Test
	public void testAirlineEntityTypes()
	{	
		EdmDataServices edmDataServices = metadataAirlineOdata4j.getMetadata();
		EdmType type = edmDataServices.findEdmEntityType(AIRLINE_NAMESPACE + ".FlightSchedule");
		Assert.assertNotNull(type);
		Assert.assertTrue(type.getFullyQualifiedTypeName().equals("FlightResponderModel.FlightSchedule"));
		Assert.assertTrue(type instanceof EdmEntityType);
		EdmEntityType entityType = (EdmEntityType) type;
		Assert.assertEquals("FlightSchedule", entityType.getName());
		Assert.assertEquals(false, entityType.findProperty("flightScheduleID").isNullable());
	}

	@Test
	public void testAirlineEntitySets()
	{	
		EdmDataServices edmDataServices = metadataAirlineOdata4j.getMetadata();
		Assert.assertEquals(1, edmDataServices.getSchemas().size());
		Assert.assertEquals(1, edmDataServices.getSchemas().get(0).getEntityContainers().size());
		Assert.assertEquals(3, edmDataServices.getSchemas().get(0).getEntityContainers().get(0).getEntitySets().size());
		EdmEntitySet entitySetFlightSchedule = edmDataServices.findEdmEntitySet("FlightSchedule");
		Assert.assertEquals("FlightSchedule", entitySetFlightSchedule.getName());
	}
	
	@Test
	public void testManyToOneMandatoryNavProperty() {
		// create mock resource interaction (which should result in creation of mandatory Navigation Property in EdmDataService metadata)
		ResourceState initial = new ResourceState("ROOT", "initial", new ArrayList<Action>(), "/");
		
		// flights and airports
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", new ArrayList<Action>(), "/Flights");
		ResourceState flight = new ResourceState("Flight", "flight", new ArrayList<Action>(), "/Flights({id})");
		CollectionResourceState airports = new CollectionResourceState("Airport", "Airports", new ArrayList<Action>(), "/Airports");
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports({id})");
		// a flight must have a departure airport
		ResourceState flightDepartureAirport = new ResourceState("Airport", "departureAirport", new ArrayList<Action>(), "/Flights({id})/departureAirport");
		
		
		initial.addTransition(flights);
		flights.addTransitionForEachItem("GET", flight, new HashMap<String, String>());
		initial.addTransition(airports);
		airports.addTransitionForEachItem("GET", airport, new HashMap<String, String>());
		flight.addTransition(flightDepartureAirport);
		flights.addTransitionForEachItem("GET", flightDepartureAirport, new HashMap<String, String>());
		ResourceStateMachine rsm = new ResourceStateMachine(initial);
		
		MetadataOData4j metadataOData4j = new MetadataOData4j(metadataAirline, rsm);
		EdmDataServices edmMetadata = metadataOData4j.getMetadata();
		
		assertNotNull(edmMetadata);
		// entity types (one with the mandatory nav property)
		EdmEntityType entityType = (EdmEntityType) edmMetadata.findEdmEntityType(AIRLINE_NAMESPACE + ".Flight");
		EdmNavigationProperty flightNavProperty = entityType.findNavigationProperty("departureAirport");
		assertNotNull(flightNavProperty);
		assertEquals("Flight_Airport", flightNavProperty.getRelationship().getName());
		assertEquals("Flight_Airport_Source", flightNavProperty.getFromRole().getRole());
		assertEquals("Flight_Airport_Target", flightNavProperty.getToRole().getRole());
		// check association
		assertEquals("*", flightNavProperty.getFromRole().getMultiplicity().getSymbolString());
		assertEquals("1", flightNavProperty.getToRole().getMultiplicity().getSymbolString());
		
		// associations
		assertNotNull(edmMetadata.getAssociations());
		int noAssociations = 0;
		for (EdmAssociation association : edmMetadata.getAssociations()) {
			noAssociations++;
			if ("Flight_Airport".equals(association.getName())) {
			} else {
				fail("Unexpected association");
			}
		}
		assertEquals(1, noAssociations);
		
		// entity sets (from ResourceStateMachine)
		assertNotNull(edmMetadata.getEntitySets());
		int noEntitySets = 0;
		for (EdmEntitySet entitySet : edmMetadata.getEntitySets()) {
			noEntitySets++;
			if ("Flights".equals(entitySet.getName())) {
			} else if ("Airports".equals(entitySet.getName())) {
			} else {
				fail("Unexpected entity set");
			}
		}
		assertEquals(2, noEntitySets);
	}
	
	@Test
	public void testManyToManyNavProperty() throws Exception {
		// create mock resource interaction (which should result in creation of Navigation Property in EdmDataService metadata)
		ResourceState initial = new ResourceState("ROOT", "initial", new ArrayList<Action>(), "/");
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", new ArrayList<Action>(), "/Flights");
		CollectionResourceState airports = new CollectionResourceState("Airport", "Airports", new ArrayList<Action>(), "/Airports");
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "FlightSchedules", new ArrayList<Action>(), "/FlightSchedules");
		ResourceState flight = new ResourceState("Flight", "flight", new ArrayList<Action>(), "/Flights({id})");
		CollectionResourceState airportFlights = new CollectionResourceState("Airport", "AirportFlights", new ArrayList<Action>(), "/Airports({id})/Flights");
		initial.addTransition(flights);
		initial.addTransition(airports);
		initial.addTransition(flightSchedules);
		flight.addTransition("GET", airportFlights, new HashMap<String, String>());
		flights.addTransitionForEachItem("GET", airportFlights, new HashMap<String, String>());
		ResourceStateMachine rsm = new ResourceStateMachine(initial);
		
		MetadataOData4j metadataOData4j = new MetadataOData4j(metadataAirline, rsm);
		EdmDataServices edmMetadata = metadataOData4j.getMetadata();
		
		assertNotNull(edmMetadata);
		
		// entities (from Metadata)
		assertNotNull(edmMetadata.getEntitySets());
		int noEntities = 0;
		for (EdmEntityType entityType : edmMetadata.getEntityTypes()) {
			noEntities++;
			if ("Flight".equals(entityType.getName())) {
			} else if ("Airport".equals(entityType.getName())) {
			} else if ("FlightSchedule".equals(entityType.getName())) {
			} else {
				fail("Unexpected entity");
			}
		}
		assertEquals(3, noEntities);

		// entity sets (from ResourceStateMachine)
		assertNotNull(edmMetadata.getEntitySets());
		int noEntitySets = 0;
		for (EdmEntitySet entitySet : edmMetadata.getEntitySets()) {
			noEntitySets++;
			if ("Flights".equals(entitySet.getName())) {
			} else if ("Airports".equals(entitySet.getName())) {
			} else if ("FlightSchedules".equals(entitySet.getName())) {
			} else {
				fail("Unexpected entity set");
			}
		}
		assertEquals(3, noEntitySets);

		// function imports (from ResourceStateMachine where transition to Collection not from initial state)
		assertNotNull(edmMetadata.findEdmFunctionImport("AirportFlights"));
	}

	@Test
	public void testMultipleManyToOneNavProperties() {
		ResourceState initial = new ResourceState("ROOT", "initial", new ArrayList<Action>(), "/", null, new UriSpecification("ROOT", "/"));
		CollectionResourceState airports = new CollectionResourceState("Airport", "airports", new ArrayList<Action>(), "/Airports");
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports('{id}')", null, new UriSpecification("airport", "/Airports('{id}')"));
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "FlightSchedules", new ArrayList<Action>(), "/FlightSchedules({filter})", null, null);

		initial.addTransition("GET", airports, null, null);
		airports.addTransitionForEachItem("GET", airport, null, null);
		Map<String, String> uriLinkageProperties = new HashMap<String, String>();
		uriLinkageProperties.put("filter", "arrivalAirportCode eq '{code}'");
		airport.addTransition("GET", flightSchedules, null, uriLinkageProperties);
		uriLinkageProperties.put("filter", "departureAirportCode eq '{code}'");
		airport.addTransition("GET", flightSchedules, null, uriLinkageProperties);

		ResourceStateMachine rsm = new ResourceStateMachine(initial);
		MetadataOData4j metadataOData4j = new MetadataOData4j(metadataAirline, rsm);
		EdmDataServices edmMetadata = metadataOData4j.getMetadata();
		
		assertNotNull(edmMetadata);
		EdmEntityType entityType = (EdmEntityType) edmMetadata.findEdmEntityType(AIRLINE_NAMESPACE + ".Airport");

		EdmNavigationProperty flightScheduleNavProperty = entityType.findNavigationProperty("departureAirportCode eq '{code}'");
		assertNotNull(flightScheduleNavProperty);
		assertEquals("Airport_FlightSchedule", flightScheduleNavProperty.getRelationship().getName());
		assertEquals("Airport_FlightSchedule_Source", flightScheduleNavProperty.getFromRole().getRole());
		assertEquals("Airport_FlightSchedule_Target", flightScheduleNavProperty.getToRole().getRole());
		assertEquals("1", flightScheduleNavProperty.getFromRole().getMultiplicity().getSymbolString());
		assertEquals("*", flightScheduleNavProperty.getToRole().getMultiplicity().getSymbolString());

		flightScheduleNavProperty = entityType.findNavigationProperty("arrivalAirportCode eq '{code}'");
		assertNotNull(flightScheduleNavProperty);
		assertEquals("Airport_FlightSchedule", flightScheduleNavProperty.getRelationship().getName());
		assertEquals("Airport_FlightSchedule_Source", flightScheduleNavProperty.getFromRole().getRole());
		assertEquals("Airport_FlightSchedule_Target", flightScheduleNavProperty.getToRole().getRole());
		assertEquals("1", flightScheduleNavProperty.getFromRole().getMultiplicity().getSymbolString());
		assertEquals("*", flightScheduleNavProperty.getToRole().getMultiplicity().getSymbolString());
	}
	
	@Test
	public void testMultipleManyToOneNavPropertiesWithLabel() {
		ResourceState initial = new ResourceState("ROOT", "initial", new ArrayList<Action>(), "/", null, new UriSpecification("ROOT", "/"));
		CollectionResourceState airports = new CollectionResourceState("Airport", "airports", new ArrayList<Action>(), "/Airports");
		ResourceState airport = new ResourceState("Airport", "airport", new ArrayList<Action>(), "/Airports('{id}')", null, new UriSpecification("airport", "/Airports('{id}')"));
		CollectionResourceState flightSchedules = new CollectionResourceState("FlightSchedule", "FlightSchedules", new ArrayList<Action>(), "/FlightSchedules({filter})", null, null);

		initial.addTransition("GET", airports, null, null);
		airports.addTransitionForEachItem("GET", airport, null, null);
		Map<String, String> uriLinkageProperties = new HashMap<String, String>();
		uriLinkageProperties.put("filter", "arrivalAirportCode eq '{code}'");
		airport.addTransition("GET", flightSchedules, null, uriLinkageProperties, "arrivals");
		uriLinkageProperties.put("filter", "departureAirportCode eq '{code}'");
		airport.addTransition("GET", flightSchedules, null, uriLinkageProperties, "departures");

		ResourceStateMachine rsm = new ResourceStateMachine(initial);
		MetadataOData4j metadataOData4j = new MetadataOData4j(metadataAirline, rsm);
		EdmDataServices edmMetadata = metadataOData4j.getMetadata();
		
		assertNotNull(edmMetadata);
		EdmEntityType entityType = (EdmEntityType) edmMetadata.findEdmEntityType(AIRLINE_NAMESPACE + ".Airport");

		EdmNavigationProperty flightScheduleNavProperty = entityType.findNavigationProperty("departures");
		assertNotNull(flightScheduleNavProperty);
		assertEquals("Airport_FlightSchedule", flightScheduleNavProperty.getRelationship().getName());
		assertEquals("Airport_FlightSchedule_Source", flightScheduleNavProperty.getFromRole().getRole());
		assertEquals("Airport_FlightSchedule_Target", flightScheduleNavProperty.getToRole().getRole());
		assertEquals("1", flightScheduleNavProperty.getFromRole().getMultiplicity().getSymbolString());
		assertEquals("*", flightScheduleNavProperty.getToRole().getMultiplicity().getSymbolString());

		flightScheduleNavProperty = entityType.findNavigationProperty("arrivals");
		assertNotNull(flightScheduleNavProperty);
		assertEquals("Airport_FlightSchedule", flightScheduleNavProperty.getRelationship().getName());
		assertEquals("Airport_FlightSchedule_Source", flightScheduleNavProperty.getFromRole().getRole());
		assertEquals("Airport_FlightSchedule_Target", flightScheduleNavProperty.getToRole().getRole());
		assertEquals("1", flightScheduleNavProperty.getFromRole().getMultiplicity().getSymbolString());
		assertEquals("*", flightScheduleNavProperty.getToRole().getMultiplicity().getSymbolString());
	}	
}
