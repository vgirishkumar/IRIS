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
public class ResponderGenTest {

	@Test
	public void testCreateJPAEntitySupportedType() {
		JPAResponderGen rg = new JPAResponderGen();
		
		EdmType t = mock(EdmType.class);
		assertNull(rg.createJPAEntityClassFromEdmEntityType(t));
	}
	
	@Test
	public void testJPAEntityInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
		EdmEntityType t = new EdmEntityType("AirlineModel", null, "Flight", false, new ArrayList<String>(), null, null);
		JPAEntityInfo p = rg.createJPAEntityClassFromEdmEntityType(t);
		
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
		JPAEntityInfo p = rg.createJPAEntityClassFromEdmEntityType(t);
		
		assertEquals("flightID", p.getKeyInfo().getName());
		assertEquals("Long", p.getKeyInfo().getType());
		assertEquals(0, p.getFieldInfos().size());
	}

	@Test
	public void testJPAEntityPropertiesInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<String> keys = new ArrayList<String>();
		List<EdmProperty> properties = new ArrayList<EdmProperty>();
		properties.add(new EdmProperty("flightID", EdmSimpleType.INT64, false));
		properties.add(new EdmProperty("number", EdmSimpleType.STRING, false));
		properties.add(new EdmProperty("fitHostiesName", EdmSimpleType.STRING, true));
		properties.add(new EdmProperty("runway", EdmSimpleType.STRING, false));
		EdmEntityType t = new EdmEntityType("AirlineModel", null, "Flight", false, keys, properties, null);
		JPAEntityInfo p = rg.createJPAEntityClassFromEdmEntityType(t);
		
		assertEquals(4, p.getFieldInfos().size());
		assertTrue(p.getFieldInfos().contains(new FieldInfo("flightID", "Long")));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("number", "String")));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("fitHostiesName", "String")));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("runway", "String")));
	}

	@Test(expected = AssertionError.class)
	public void testGenJPAEntityNull() {
		JPAResponderGen rg = new JPAResponderGen();
		assertNull(rg.generateJPAEntityClass(null));
	}

	@Test
	public void testGenJPAEntity() {
		JPAResponderGen rg = new JPAResponderGen();
		
		FieldInfo keyInfo = new FieldInfo("flightID", "Long");
		List<FieldInfo> properties = new ArrayList<FieldInfo>();
		properties.add(new FieldInfo("number", "String"));
		properties.add(new FieldInfo("fitHostiesName", "String"));
		properties.add(new FieldInfo("runway", "String"));
		
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
		assertTrue(generatedClass.contains("public Flight() {}"));
	}
	
	@Test
	public void testFormClassFilename() {
		assertEquals("/tmp/blah/com/some/package/SomeClass.java", JPAResponderGen.formClassFilename("/tmp/blah", new JPAEntityInfo("SomeClass", "com.some.package", null, null)));
	}
	
}
