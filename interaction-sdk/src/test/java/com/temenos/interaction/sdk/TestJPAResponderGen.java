package com.temenos.interaction.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEventReader2;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataParser;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
import com.temenos.interaction.sdk.interaction.InteractionModel;
import com.temenos.interaction.sdk.util.ReferentialConstraintParser;

/**
 * Unit test for {@link JPAResponderGen}.
 */
public class TestJPAResponderGen {
	public final static String METADATA_AIRLINE_XML_FILE = "AirlinesMetadata.xml";
	public final static String EDMX_AIRLINE_FILE = "airlines.edmx";

	//Mock out methods that write contents to files
	class MockGenerator extends JPAResponderGen {
		List<String> generatedClasses = new ArrayList<String>();
		String generatedPersistenceXML;
		String generatedSpringXML;
		String generatedSpringResourceManagerXML;
		String generateResponderDML;
		String generatedBehaviourClass;
		String generatedMetadata;
		
		@Override
		protected String getLinkProperty(String associationName, String edmxFile) {
			InputStream isEdmx = getClass().getResourceAsStream("/" + EDMX_AIRLINE_FILE);
			return ReferentialConstraintParser.getLinkProperty(associationName, isEdmx);
		}
		
		@Override
		protected boolean writeClass(String path, String classFileName, String generatedClass) {
			this.generatedClasses.add(generatedClass);
			return true;
		}
		
		@Override
		protected boolean writeJPAConfiguration(File sourceDir, String generatedPersistenceXML) {
			this.generatedPersistenceXML = generatedPersistenceXML;
			return true;
		}

		@Override
		protected boolean writeSpringConfiguration(File sourceDir, String filename, String generatedSpringXML) {
			if(filename.equals(JPAResponderGen.SPRING_CONFIG_FILE)) {
				this.generatedSpringXML = generatedSpringXML;
			}
			else if(filename.equals(JPAResponderGen.SPRING_RESOURCEMANAGER_FILE)) {
				this.generatedSpringResourceManagerXML = generatedSpringXML;
			}
			return true;
		}
		
		@Override
		protected boolean writeResponderDML(File sourceDir, String generateResponderDML) {
			this.generateResponderDML = generateResponderDML;
			return true;
		}
		
		@Override
		protected boolean writeBehaviourClass(String path, String generatedBehaviourClass) {
			this.generatedBehaviourClass = generatedBehaviourClass;
			return true;
		}
		
		@Override
		protected boolean writeMetadata(File sourceDir, String generatedMetadata) {
			this.generatedMetadata = generatedMetadata;
			return true;
		}
	};
	
	@Test
	public void testCreateJPAEntitySupportedType() {
		JPAResponderGen rg = new JPAResponderGen();
		
		EdmType t = mock(EdmType.class);
		assertNull(rg.createEntityInfoFromEdmEntityType(t));
	}
	
	@Test
	public void testEntityInfoEdmx() {
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
		EntityInfo p = rg.createEntityInfoFromEdmEntityType(t);
		
		assertEquals("Flight", p.getClazz());
		assertEquals("AirlineModel", p.getPackage());
		assertEquals("AirlineModel.Flight", p.getFQTypeName());
	}

	@Test
	public void testEntityInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
		EntityMetadata mdEntity = new EntityMetadata("Flight");
		Vocabulary voc = new Vocabulary();
		voc.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		voc.setTerm(new TermIdField(true));
		mdEntity.setPropertyVocabulary("ID", voc);
		EntityInfo p = rg.createEntityInfoFromEntityMetadata("AirlineModel", mdEntity);
		
		assertEquals("Flight", p.getClazz());
		assertEquals("AirlineModel", p.getPackage());
		assertEquals("AirlineModel.Flight", p.getFQTypeName());
	}
	
	@Test
	public void testJPAEntityKeyInfoEdmx() {
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
		EntityInfo p = rg.createEntityInfoFromEdmEntityType(t);
		
		assertEquals("flightID", p.getKeyInfo().getName());
		assertEquals("Long", p.getKeyInfo().getType());
		assertEquals(0, p.getFieldInfos().size());
	}

	@Test
	public void testJPAEntityKeyInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
		EntityMetadata mdEntity = new EntityMetadata("Flight");
		Vocabulary voc = new Vocabulary();
		voc.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		voc.setTerm(new TermIdField(true));
		mdEntity.setPropertyVocabulary("flightID", voc);
		EntityInfo p = rg.createEntityInfoFromEntityMetadata("AirlineModel", mdEntity);
		
		assertEquals("flightID", p.getKeyInfo().getName());
		assertEquals("Long", p.getKeyInfo().getType());
		assertEquals(0, p.getFieldInfos().size());
	}
	
	@Test
	public void testJPAEntityFieldInfoEdmx() {
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
		EntityInfo p = rg.createEntityInfoFromEdmEntityType(t);
		
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
	public void testJPAEntityFieldInfo() {
		JPAResponderGen rg = new JPAResponderGen();
		
		EntityMetadata mdEntity = new EntityMetadata("Flight");
		Vocabulary voc = new Vocabulary();
		voc.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		voc.setTerm(new TermIdField(true));
		mdEntity.setPropertyVocabulary("ID", voc);
		voc = new Vocabulary();
		voc.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		mdEntity.setPropertyVocabulary("flightID", voc);
		mdEntity.setPropertyVocabulary("number", new Vocabulary());
		mdEntity.setPropertyVocabulary("runway", new Vocabulary());
		voc = new Vocabulary();
		voc.setTerm(new TermValueType(TermValueType.TIMESTAMP));
		mdEntity.setPropertyVocabulary("departureDT", voc);
		EntityInfo p = rg.createEntityInfoFromEntityMetadata("AirlineModel", mdEntity);
		
		assertEquals(4, p.getFieldInfos().size());
		assertTrue(p.getFieldInfos().contains(new FieldInfo("flightID", "Long", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("number", "String", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("runway", "String", null)));
		assertTrue(p.getFieldInfos().contains(new FieldInfo("departureDT", "java.util.Date", null)));
	}
	
	@Test
	public void testJPAEntityFieldInfoAnnotationsEdmx() {
		JPAResponderGen rg = new JPAResponderGen();
		
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
		EntityInfo p = rg.createEntityInfoFromEdmEntityType(t);

		// Annotations
		FieldInfo dateFI = p.getFieldInfos().get(0);
		assertEquals(1, dateFI.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIMESTAMP)", dateFI.getAnnotations().get(0));

		FieldInfo timeFI = p.getFieldInfos().get(1);
		assertEquals(1, timeFI.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIME)", timeFI.getAnnotations().get(0));
	}
	
	@Test
	public void testJPAEntityFieldInfoAnnotations() {
		JPAResponderGen rg = new JPAResponderGen();
		
		EntityMetadata mdEntity = new EntityMetadata("Flight");
		Vocabulary voc = new Vocabulary();
		voc.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		voc.setTerm(new TermIdField(true));
		mdEntity.setPropertyVocabulary("ID", voc);
		voc = new Vocabulary();
		voc.setTerm(new TermValueType(TermValueType.TIMESTAMP));
		mdEntity.setPropertyVocabulary("departureDT", voc);
		voc = new Vocabulary();
		voc.setTerm(new TermValueType(TermValueType.TIMESTAMP));
		mdEntity.setPropertyVocabulary("departureTime", voc);
		EntityInfo p = rg.createEntityInfoFromEntityMetadata("AirlineModel", mdEntity);

		// Annotations
		FieldInfo dateFI = p.getFieldInfos().get(0);
		assertEquals(1, dateFI.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIMESTAMP)", dateFI.getAnnotations().get(0));

		FieldInfo timeFI = p.getFieldInfos().get(1);
		assertEquals(1, timeFI.getAnnotations().size());
		assertEquals("@Temporal(TemporalType.TIMESTAMP)", timeFI.getAnnotations().get(0));
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
		
		EntityInfo jpaEntity = new EntityInfo("Flight", "AirlineModel.model", keyInfo, properties);
		String generatedClass = rg.generateJPAEntityClass(jpaEntity);
		
		assertTrue(generatedClass.contains("package AirlineModel.model;"));
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
		assertTrue(generatedClass.contains("public Flight() {"));
	}
	
	@Test
	public void testGenJPAConfiguration() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<EntityInfo> entitiesInfo = new ArrayList<EntityInfo>();
		entitiesInfo.add(new EntityInfo("Flight", "AirlineModel", null, null));
		entitiesInfo.add(new EntityInfo("Airport", "AirlineModel", null, null));
		entitiesInfo.add(new EntityInfo("FlightSchedule", "AirlineModel", null, null));
		String generatedPersistenceConfig = rg.generateJPAConfiguration(entitiesInfo);
		
		assertTrue(generatedPersistenceConfig.contains("<!-- Generated JPA configuration for IRIS MockResponder -->"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.Flight</class>"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.Airport</class>"));
		assertTrue(generatedPersistenceConfig.contains("<class>AirlineModel.FlightSchedule</class>"));
	}

	@Test
	public void testGenResponderDML() {
		JPAResponderGen rg = new JPAResponderGen();
		
		List<EntityInfo> entitiesInfo = new ArrayList<EntityInfo>();
		
		List<FieldInfo> properties = new ArrayList<FieldInfo>();
		properties.add(new FieldInfo("number", "Long", null));
		properties.add(new FieldInfo("fitHostiesName", "String", null));
		properties.add(new FieldInfo("runway", "String", null));
		entitiesInfo.add(new EntityInfo("Flight", "AirlineModel", null, properties));
		entitiesInfo.add(new EntityInfo("Airport", "AirlineModel", null, null));
		entitiesInfo.add(new EntityInfo("FlightSchedule", "AirlineModel", null, null));
		String generatedResponderDML = rg.generateResponderDML(entitiesInfo);
		
		assertTrue(generatedResponderDML.contains("INSERT INTO `Flight`(`number` , `fitHostiesName` , `runway`) VALUES('1' , 'abc' , 'abc');"));
		assertTrue(generatedResponderDML.contains("INSERT INTO `Airport`() VALUES();"));
		assertTrue(generatedResponderDML.contains("INSERT INTO `FlightSchedule`() VALUES();"));
		}

	@Test
	public void testFormClassFilename() {
		assertEquals("/tmp/blah/com/some/package/SomeClass.java", JPAResponderGen.formClassFilename("/tmp/blah/com/some/package", new EntityInfo("SomeClass", "com.some.package", null, null)));
	}
	
	@Test
	public void testGeneratedArtifactsFromConceptualModels() {
		//Parse the test metadata
		MetadataParser parser = new MetadataParser();
		InputStream is = parser.getClass().getClassLoader().getResourceAsStream(METADATA_AIRLINE_XML_FILE);
		Metadata metadata = parser.parse(is);
		Assert.assertNotNull(metadata);

		//Define the interaction model
		InteractionModel interactionModel = new InteractionModel(metadata);

		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "departureAirportCode", "departureAirport", false, "flightschedules", interactionModel.findResourceStateMachine("Airport"));
		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "arrivalAirportCode", "arrivalAirport", false, "flightschedules", interactionModel.findResourceStateMachine("Airport"));
		interactionModel.findResourceStateMachine("Airport").addTransition("FlightSchedule", "departureAirportCode", "flightSchedules", true, "departureAirport", interactionModel.findResourceStateMachine("FlightSchedule"));
		
		//Run the generator
		MockGenerator generator = new MockGenerator();
		boolean status = generator.generateArtifacts(metadata, interactionModel, new File("."), new File("."), true);
		
		//Check results
		assertTrue(status);
		
		assertTrue(generator.generatedClasses.size() == 3);
		assertTrue(generator.generatedClasses.get(0).contains("public class Flight"));
		assertTrue(generator.generatedClasses.get(1).contains("public class Airport"));
		assertTrue(generator.generatedClasses.get(2).contains("public class FlightSchedule"));

		assertTrue(generator.generateResponderDML.contains("INSERT INTO `Flight`("));
		assertTrue(generator.generateResponderDML.contains("INSERT INTO `Airport`("));
		assertTrue(generator.generateResponderDML.contains("INSERT INTO `FlightSchedule`("));

		assertTrue(generator.generatedPersistenceXML.contains("<class>FlightResponderModel.Flight</class>"));
		assertTrue(generator.generatedPersistenceXML.contains("<class>FlightResponderModel.Airport</class>"));
		assertTrue(generator.generatedPersistenceXML.contains("<class>FlightResponderModel.FlightSchedule</class>"));

		assertTrue(generator.generatedSpringXML.contains("<bean id=\"behaviour\" class=\"FlightResponderModel.Behaviour\" />"));
		assertTrue(generator.generatedSpringXML.contains("<entry key=\"GET+/Flight\" value-ref=\"cmdGETEntitiesFlight\" />"));
		assertTrue(generator.generatedSpringXML.contains("<entry key=\"GET+/Airport\" value-ref=\"cmdGETEntitiesAirport\" />"));
		assertTrue(generator.generatedSpringXML.contains("<entry key=\"GET+/FlightSchedule\" value-ref=\"cmdGETEntitiesFlightSchedule\" />"));

		assertTrue(generator.generatedSpringResourceManagerXML.contains("<constructor-arg name=\"namespace\" value=\"FlightResponder\" />"));		
		
		//Test resource states
		assertTrue(generator.generatedBehaviourClass.contains("public class Behaviour {"));
		assertTrue(generator.generatedBehaviourClass.contains("initialState.addTransition(\"GET\", flightschedules);"));
		assertTrue(generator.generatedBehaviourClass.contains("initialState.addTransition(\"GET\", airports);"));
		assertTrue(generator.generatedBehaviourClass.contains("initialState.addTransition(\"GET\", flights);"));

		//Test transitions between entities
		assertTrue(generator.generatedBehaviourClass.contains("((CollectionResourceState) airports.getState(\"/Airport({id})/flightSchedules\")).addTransitionForEachItem(\"GET\", flightschedules.getState(\"/FlightSchedule({id})\"), uriLinkageMap);"));
		assertTrue(generator.generatedBehaviourClass.contains("flightschedules.getState(\"/FlightSchedule({id})/departureAirport\").addTransition(\"GET\", airports.getState(\"/Airport({id})/flightschedules\"), uriLinkageMap);"));
		assertTrue(generator.generatedBehaviourClass.contains("flightschedules.getState(\"/FlightSchedule({id})/arrivalAirport\").addTransition(\"GET\", airports.getState(\"/Airport({id})/flightschedules\"), uriLinkageMap);"));

	}

	@Test
	public void testGeneratedArtifactsFromEdmx() {
		//Parse the test metadata
		InputStream isEdmx = getClass().getResourceAsStream("/" + EDMX_AIRLINE_FILE);
		XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(isEdmx)));
		EdmDataServices edmDataServices = new EdmxFormatParser().parseMetadata(reader);
		Assert.assertNotNull(edmDataServices);

		//Define the interaction model
		InteractionModel interactionModel = new InteractionModel(edmDataServices);

		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "departureAirportCode", "departureAirport", false, "flightschedules", interactionModel.findResourceStateMachine("Airport"));
		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "arrivalAirportCode", "arrivalAirport", false, "flightschedules", interactionModel.findResourceStateMachine("Airport"));
		interactionModel.findResourceStateMachine("Airport").addTransition("FlightSchedule", "departureAirportCode", "flightSchedules", true, "departureAirport", interactionModel.findResourceStateMachine("FlightSchedule"));
		
		//Run the generator
		MockGenerator generator = new MockGenerator();
		boolean status = generator.generateArtifacts(EDMX_AIRLINE_FILE, edmDataServices, new File("."), new File("."));
		
		//Check results
		assertTrue(status);
		
		assertTrue(generator.generatedClasses.size() == 3);
		assertTrue(generator.generatedClasses.get(0).contains("public class FlightSchedule"));
		assertTrue(generator.generatedClasses.get(1).contains("public class Airport"));
		assertTrue(generator.generatedClasses.get(2).contains("public class Flight"));

		assertTrue(generator.generateResponderDML.contains("INSERT INTO `Flight`("));
		assertTrue(generator.generateResponderDML.contains("INSERT INTO `Airport`("));
		assertTrue(generator.generateResponderDML.contains("INSERT INTO `FlightSchedule`("));

		assertTrue(generator.generatedPersistenceXML.contains("<class>FlightResponderModel.Flight</class>"));
		assertTrue(generator.generatedPersistenceXML.contains("<class>FlightResponderModel.Airport</class>"));
		assertTrue(generator.generatedPersistenceXML.contains("<class>FlightResponderModel.FlightSchedule</class>"));

		assertTrue(generator.generatedSpringXML.contains("<bean id=\"behaviour\" class=\"FlightResponderModel.Behaviour\" />"));
		assertTrue(generator.generatedSpringXML.contains("<entry key=\"GET+/Flight\" value-ref=\"cmdGETEntitiesFlight\" />"));
		assertTrue(generator.generatedSpringXML.contains("<entry key=\"GET+/Airport\" value-ref=\"cmdGETEntitiesAirport\" />"));
		assertTrue(generator.generatedSpringXML.contains("<entry key=\"GET+/FlightSchedule\" value-ref=\"cmdGETEntitiesFlightSchedule\" />"));

		assertTrue(generator.generatedSpringResourceManagerXML.contains("<constructor-arg name=\"namespace\" value=\"FlightResponder\" />"));		
		
		//Test resource states
		assertTrue(generator.generatedBehaviourClass.contains("public class Behaviour {"));
		assertTrue(generator.generatedBehaviourClass.contains("initialState.addTransition(\"GET\", flightschedules);"));
		assertTrue(generator.generatedBehaviourClass.contains("initialState.addTransition(\"GET\", airports);"));
		assertTrue(generator.generatedBehaviourClass.contains("initialState.addTransition(\"GET\", flights);"));

		//Test transitions between entities
		assertTrue(generator.generatedBehaviourClass.contains("flightschedules.getState(\"/FlightSchedule({id})/departureAirport\").addTransition(\"GET\", airports.getState(\"/Airport({id})/flightSchedules\"), uriLinkageMap);"));
		assertTrue(generator.generatedBehaviourClass.contains("flightschedules.getState(\"/FlightSchedule({id})/arrivalAirport\").addTransition(\"GET\", airports.getState(\"/Airport({id})/flightSchedules\"), uriLinkageMap);"));
		assertTrue(generator.generatedBehaviourClass.contains("((CollectionResourceState) airports.getState(\"/Airport({id})/flightSchedules\")).addTransitionForEachItem(\"GET\", flightschedules.getState(\"/FlightSchedule({id})\"), uriLinkageMap);"));
	}
}
