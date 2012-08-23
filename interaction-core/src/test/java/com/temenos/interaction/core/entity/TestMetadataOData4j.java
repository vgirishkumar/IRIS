package com.temenos.interaction.core.entity;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmType;

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
		metadataOdata4j = new MetadataOData4j(metadata);

		//Read the airline metadata file
		MetadataParser parserAirline = new MetadataParser();
		InputStream isAirline = parserAirline.getClass().getClassLoader().getResourceAsStream(METADATA_AIRLINE_XML_FILE);
		Metadata metadataAirline = parserAirline.parse(isAirline);
		Assert.assertNotNull(metadataAirline);
		
		//Convert metadata to odata4j metadata
		metadataAirlineOdata4j = new MetadataOData4j(metadataAirline);
	}
	
	@Test
	public void testCustomerEntity()
	{	
		EdmDataServices edmDataServices = metadataOdata4j.getMetadata();
		EdmType type = edmDataServices.findEdmEntityType(MetadataOData4j.NAMESPACE + ".Customer");
		Assert.assertNotNull(type);
		Assert.assertTrue(type.getFullyQualifiedTypeName().equals(MetadataOData4j.NAMESPACE + ".Customer"));
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
	public void testAirlineEntities()
	{	
		EdmDataServices edmDataServices = metadataAirlineOdata4j.getMetadata();
		EdmType type = edmDataServices.findEdmEntityType(MetadataOData4j.NAMESPACE + ".FlightSchedule");
		Assert.assertNotNull(type);
		Assert.assertTrue(type.getFullyQualifiedTypeName().equals(MetadataOData4j.NAMESPACE + ".FlightSchedule"));
		Assert.assertTrue(type instanceof EdmEntityType);
		EdmEntityType entityType = (EdmEntityType) type;
		Assert.assertEquals("FlightSchedule", entityType.getName());
		Assert.assertEquals(false, entityType.findProperty("flightScheduleID").isNullable());
	}
}
