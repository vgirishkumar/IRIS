package com.temenos.interaction.core.entity;

import java.io.InputStream;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmType;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class TestMetadataOData4j {
	public final static String METADATA_XML_FILE = "TestMetadataParser.xml";
	public final static String METADATA_AIRLINE_XML_FILE = "AirlinesMetadata.xml";

	private static MetadataOData4j metadataOdata4j;
	private static MetadataOData4j metadataAirlineOdata4j;
	
	@BeforeClass
	public static void setup()
	{
		//Read the metadata file
		MetadataParser parser = new MetadataParser();
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
		Metadata metadataAirline = parserAirline.parse(isAirline);
		Assert.assertNotNull(metadataAirline);
		
		//Convert metadata to odata4j metadata
		metadataAirlineOdata4j = new MetadataOData4j(metadataAirline, hypermediaEngine);
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
		Assert.assertEquals(false, entityType.findProperty("dateOfBirth").isNullable());
		Assert.assertEquals(true, entityType.findProperty("postCode").isNullable());
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
		EdmType type = edmDataServices.findEdmEntityType("FlightResponderModel.FlightSchedule");
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
}
