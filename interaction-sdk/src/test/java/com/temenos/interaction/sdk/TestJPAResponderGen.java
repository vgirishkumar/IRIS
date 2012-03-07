package com.temenos.interaction.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;

/**
 * Unit test for {@link JPAResponderGen}.
 */
public class TestJPAResponderGen {

	@Test
	public void testCreateJPAEntitySupportedType() {
		JPAResponderGen rg = new JPAResponderGen();
		
		EdmType t = mock(EdmType.class);
		assertNull(rg.createJPAEntityInfoFromEdmEntityType(t));
	}
	
	@Test
	public void testJPAEntityInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
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
		JPAEntityInfo p = rg.createJPAEntityInfoFromEdmEntityType(t);
		
		assertEquals("Flight", p.getClazz());
		assertEquals("AirlineModel", p.getPackage());
		assertEquals("AirlineModel.Flight", p.getFQTypeName());
	}
	
	@Test
	public void testJPAEntityKeyInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
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
		JPAEntityInfo p = rg.createJPAEntityInfoFromEdmEntityType(t);
		
		assertEquals("flightID", p.getKeyInfo().getName());
		assertEquals("Long", p.getKeyInfo().getType());
		assertEquals(0, p.getFieldInfos().size());
	}

	@Test
	public void testJPAEntityFieldInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
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
		JPAEntityInfo p = rg.createJPAEntityInfoFromEdmEntityType(t);
		
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
	public void testJPAEntityFieldInfoAnnotations() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<String> keys = new ArrayList<String>();
		keys.add("ID");

		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		properties.add(EdmProperty.newBuilder("ID").setType(EdmSimpleType.INT64));
		properties.add(EdmProperty.newBuilder("departureDT").setType(EdmSimpleType.DATETIME));
		properties.add(EdmProperty.newBuilder("departureTime").setType(EdmSimpleType.TIME));

		/*
@Temporal(TemporalType.TIME)
@Column(nullable = false, length = 8)
private Date departureTime;

@Temporal(TemporalType.TIMESTAMP)
@Column(nullable = false)
private Calendar firstDeparture;

@Temporal(TemporalType.DATE)
private Date lastDeparture;
*/
		
		EdmEntityType.Builder entityTypeBuilder = EdmEntityType.newBuilder();
		entityTypeBuilder.setNamespace("AirlineModel");
		entityTypeBuilder.setName("Flight");
		entityTypeBuilder.addKeys(keys);
		entityTypeBuilder.addProperties(properties);
		EdmEntityType t = entityTypeBuilder.build();
		JPAEntityInfo p = rg.createJPAEntityInfoFromEdmEntityType(t);

		// Annotations
		FieldInfo dateFI = p.getFieldInfos().get(0);
		assertEquals(1, dateFI.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIMESTAMP)", dateFI.getAnnotations().get(0));

		FieldInfo timeFI = p.getFieldInfos().get(1);
		assertEquals(1, timeFI.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIME)", timeFI.getAnnotations().get(0));
	}

	@Test(expected = AssertionError.class)
	public void testGenJPAEntityNull() {
		JPAResponderGen rg = new JPAResponderGen();
		assertNull(rg.generateJPAEntityClass(null));
	}

	@Test
	public void testGenJPAEntity() {
		JPAResponderGen rg = new JPAResponderGen();
		
		FieldInfo keyInfo = new FieldInfo("flightID", "Long", null);
		List<FieldInfo> properties = new ArrayList<FieldInfo>();
		properties.add(new FieldInfo("number", "String", null));
		properties.add(new FieldInfo("fitHostiesName", "String", null));
		properties.add(new FieldInfo("runway", "String", null));
		properties.add(new FieldInfo("passengers", "Integer", null));
		
		List<String> annotations = new ArrayList<String>();
		annotations.add("@Temporal(TemporalType.TIMESTAMP)");
		properties.add(new FieldInfo("departureDT", "java.util.Date", annotations));
		
		properties.add(new FieldInfo("dinnerServed", "java.sql.Timestamp", null));
		
		JPAEntityInfo jpaEntity = new JPAEntityInfo("Flight", "AirlineModel", keyInfo, properties);
		String generatedClass = rg.generateJPAEntityClass(jpaEntity);
		
		assertTrue(generatedClass.contains("package AirlineModel;"));
		assertTrue(generatedClass.contains("import javax.persistence.Entity;"));
		assertTrue(generatedClass.contains("@Entity"));
		assertTrue(generatedClass.contains("public class Flight {"));
		assertTrue(generatedClass.contains("@Id"));
		assertTrue(generatedClass.contains("@Basic(optional = false)"));
		assertTrue(generatedClass.contains("private Long flightID;"));
		assertTrue(generatedClass.contains("private String number;"));
		assertTrue(generatedClass.contains("private String fitHostiesName;"));
		assertTrue(generatedClass.contains("private String runway;"));
		assertTrue(generatedClass.contains("private Integer passengers;"));

		// date handling needs some special support
		assertTrue(generatedClass.contains("@Temporal(TemporalType.TIMESTAMP)"));
		assertTrue(generatedClass.contains("private java.util.Date departureDT;"));

		assertTrue(generatedClass.contains("private java.sql.Timestamp dinnerServed;"));
		assertTrue(generatedClass.contains("public Flight() {}"));
	}
	
	@Test
	public void testGenJPAConfiguration() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<ResourceInfo> resourcesInfo = new ArrayList<ResourceInfo>();
		resourcesInfo.add(new ResourceInfo("/Flight/{id}", new JPAEntityInfo("Flight", "AirlineModel", null, null), "com.temenos.interaction.commands.odata.GETEntityCommand"));
		resourcesInfo.add(new ResourceInfo("/Airport/{id}", new JPAEntityInfo("Airport", "AirlineModel", null, null), "com.temenos.interaction.commands.odata.GETEntityCommand"));
		resourcesInfo.add(new ResourceInfo("/FlightSchedule/{id}", new JPAEntityInfo("FlightSchedule", "AirlineModel", null, null), "com.temenos.interaction.commands.odata.GETEntityCommand"));
		String generatedPersistenceConfig = rg.generateJPAConfiguration(resourcesInfo);
		
		assertTrue(generatedPersistenceConfig.contains("<!-- Generated JPA configuration for IRIS MockResponder -->"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.Flight</class>"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.Airport</class>"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.FlightSchedule</class>"));
	}

	@Test
	public void testGenResponderDML() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<ResourceInfo> resourcesInfo = new ArrayList<ResourceInfo>();
		
		List<FieldInfo> properties = new ArrayList<FieldInfo>();
		properties.add(new FieldInfo("number", "Long", null));
		properties.add(new FieldInfo("fitHostiesName", "String", null));
		properties.add(new FieldInfo("runway", "String", null));
		resourcesInfo.add(new ResourceInfo("/Flight/{id}", new JPAEntityInfo("Flight", "AirlineModel", null, properties), "com.temenos.interaction.commands.odata.GETEntityCommand"));
		resourcesInfo.add(new ResourceInfo("/Airport/{id}", new JPAEntityInfo("Airport", "AirlineModel", null, null), "com.temenos.interaction.commands.odata.GETEntityCommand"));
		resourcesInfo.add(new ResourceInfo("/FlightSchedule/{id}", new JPAEntityInfo("FlightSchedule", "AirlineModel", null, null), "com.temenos.interaction.commands.odata.GETEntityCommand"));
		String generatedResponderDML = rg.generateResponderDML(resourcesInfo);
		
		assertTrue(generatedResponderDML.contains("#INSERT INTO `Flight`(`number` , `fitHostiesName` , `runway`) VALUES('1' , 'example' , 'example');"));
		assertTrue(generatedResponderDML.contains("#INSERT INTO `Airport`() VALUES();"));
		assertTrue(generatedResponderDML.contains("#INSERT INTO `FlightSchedule`() VALUES();"));
		}

	@Test
	public void testFormClassFilename() {
		assertEquals("/tmp/blah/com/some/package/SomeClass.java", JPAResponderGen.formClassFilename("/tmp/blah", new JPAEntityInfo("SomeClass", "com.some.package", null, null)));
	}
	
}
