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
		
		EdmEntityType t = new EdmEntityType("AirlineModel", null, "Flight", false, new ArrayList<String>(), null, null);
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
		List<EdmProperty> properties = new ArrayList<EdmProperty>();
		properties.add(new EdmProperty("flightID", EdmSimpleType.INT64, false));
		EdmEntityType t = new EdmEntityType("AirlineModel", null, "Flight", false, keys, properties, null);
		JPAEntityInfo p = rg.createJPAEntityInfoFromEdmEntityType(t);
		
		assertEquals("flightID", p.getKeyInfo().getName());
		assertEquals("Long", p.getKeyInfo().getType());
		assertEquals(0, p.getFieldInfos().size());
	}

	@Test
	public void testJPAEntityFieldInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<String> keys = new ArrayList<String>();
		List<EdmProperty> properties = new ArrayList<EdmProperty>();
		properties.add(new EdmProperty("flightID", EdmSimpleType.INT64, false));
		properties.add(new EdmProperty("number", EdmSimpleType.STRING, false));
		properties.add(new EdmProperty("fitHostiesName", EdmSimpleType.STRING, true));
		properties.add(new EdmProperty("runway", EdmSimpleType.STRING, false));
		properties.add(new EdmProperty("passengers", EdmSimpleType.INT32, false));
		properties.add(new EdmProperty("departureDT", EdmSimpleType.DATETIME, false));
		properties.add(new EdmProperty("dinnerServed", EdmSimpleType.TIME, false));
		
		EdmEntityType t = new EdmEntityType("AirlineModel", null, "Flight", false, keys, properties, null);
		JPAEntityInfo p = rg.createJPAEntityInfoFromEdmEntityType(t);
		
		assertEquals(7, p.getFieldInfos().size());
		assertTrue(p.getFieldInfos().contains(new FieldInfo("flightID", "Long", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("number", "String", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("fitHostiesName", "String", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("runway", "String", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("passengers", "Integer", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("departureDT", "java.util.Date", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("dinnerServed", "java.sql.Timestamp", null)));
	}
	
	public void testJPAEntityFieldInfoAnnotations() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<String> keys = new ArrayList<String>();
		List<EdmProperty> properties = new ArrayList<EdmProperty>();
		properties.add(new EdmProperty("departureDT", EdmSimpleType.DATETIME, false));
		properties.add(new EdmProperty("departureTime", EdmSimpleType.TIME, false));

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
		
		EdmEntityType t = new EdmEntityType("AirlineModel", null, "Flight", false, keys, properties, null);
		JPAEntityInfo p = rg.createJPAEntityInfoFromEdmEntityType(t);
		FieldInfo fi = p.getFieldInfos().get(0);
		
		// Annotations
		assertEquals(2, fi.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIMESTAMP)", fi.getAnnotations().get(0));
		assertEquals("@Temporal(TemporalType.TIME)", fi.getAnnotations().get(1));
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
		
		List<JPAEntityInfo> entities = new ArrayList<JPAEntityInfo>();
		entities.add(new JPAEntityInfo("Flight", "AirlineModel", null, null));
		entities.add(new JPAEntityInfo("Airport", "AirlineModel", null, null));
		entities.add(new JPAEntityInfo("FlightSchedule", "AirlineModel", null, null));
		String generatedPersistenceConfig = rg.generateJPAConfiguration(entities);
		
		assertTrue(generatedPersistenceConfig.contains("<!-- Generated JPA configuration for IRIS MockResponder -->"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.Flight</class>"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.Airport</class>"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.FlightSchedule</class>"));
	}

	@Test
	public void testGenResponderDML() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<JPAEntityInfo> entities = new ArrayList<JPAEntityInfo>();
		
		List<FieldInfo> properties = new ArrayList<FieldInfo>();
		properties.add(new FieldInfo("number", "Long", null));
		properties.add(new FieldInfo("fitHostiesName", "String", null));
		properties.add(new FieldInfo("runway", "String", null));
		entities.add(new JPAEntityInfo("Flight", "AirlineModel", null, properties));

		entities.add(new JPAEntityInfo("Airport", "AirlineModel", null, null));
		entities.add(new JPAEntityInfo("FlightSchedule", "AirlineModel", null, null));
		String generatedResponderDML = rg.generateResponderDML(entities);
		
		assertTrue(generatedResponderDML.contains("#INSERT INTO `Flight`(`number` , `fitHostiesName` , `runway`) VALUES('1' , 'example' , 'example');"));
		assertTrue(generatedResponderDML.contains("#INSERT INTO `Airport`() VALUES();"));
		assertTrue(generatedResponderDML.contains("#INSERT INTO `FlightSchedule`() VALUES();"));
		}

	@Test
	public void testFormClassFilename() {
		assertEquals("/tmp/blah/com/some/package/SomeClass.java", JPAResponderGen.formClassFilename("/tmp/blah", new JPAEntityInfo("SomeClass", "com.some.package", null, null)));
	}
	
}
