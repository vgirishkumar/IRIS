package com.temenos.interaction.sdk.rimdsl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Assert;
import org.junit.Test;

import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataParser;
import com.temenos.interaction.sdk.command.Commands;
import com.temenos.interaction.sdk.command.Parameter;
import com.temenos.interaction.sdk.interaction.InteractionModel;

/**
 * Unit test for {@link RimDslGenerator}.
 */
public class TestRimDslGenerator {
	public final static String METADATA_AIRLINE_XML_FILE = "AirlinesMetadata.xml";
	public final static String RIM_DSL_AIRLINE_SIMPLE_FILE = "AirlinesSimple.rim";
	public final static String RIM_DSL_AIRLINE_FILE = "Airlines.rim";
	public final static String RIM_DSL_AIRLINE_NON_STRICT_ODATA_FILE = "AirlinesNonStrictOData.rim";
	
	public final static Parameter COMMAND_SERVICE_DOCUMENT = new Parameter("ServiceDocument", false, "");
	public final static Parameter COMMAND_EDM_DATA_SERVICES = new Parameter("edmDataServices", true, "");
	public final static Parameter COMMAND_METADATA = new Parameter("Metadata", false, "");
	public final static Parameter COMMAND_METADATA_SOURCE_ODATAPRODUCER = new Parameter("producer", true, "odataProducer");
	public final static Parameter COMMAND_METADATA_SOURCE_MODEL = new Parameter("edmMetadata", true, "edmMetadata");

	@Test
	public void testGenerateRimDslAirlinesSimple() {
		//Define the basic interaction model based on the available metadata
		Metadata metadata = parseMetadata(METADATA_AIRLINE_XML_FILE);
		InteractionModel interactionModel = new InteractionModel(metadata);
		
		Commands commands = new Commands();
		
		commands.addCommand(Commands.GET_SERVICE_DOCUMENT, "com.temenos.interaction.commands.odata.GETMetadataCommand", Commands.GET_SERVICE_DOCUMENT, COMMAND_SERVICE_DOCUMENT, COMMAND_EDM_DATA_SERVICES);
		commands.addCommand(Commands.GET_METADATA, "com.temenos.interaction.commands.odata.GETMetadataCommand", Commands.GET_METADATA, COMMAND_METADATA, COMMAND_EDM_DATA_SERVICES);
		commands.addCommand(Commands.GET_ENTITIES, "com.temenos.interaction.commands.odata.GETEntitiesCommand", Commands.GET_ENTITIES, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.GET_ENTITY, "com.temenos.interaction.commands.odata.GETEntityCommand", Commands.GET_ENTITY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.CREATE_ENTITY, "com.temenos.interaction.commands.odata.CreateEntityCommand", Commands.CREATE_ENTITY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.GET_NAV_PROPERTY, "com.temenos.interaction.commands.odata.GETNavPropertyCommand", Commands.GET_NAV_PROPERTY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);

		//Add transitions
		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "departureAirportCode", "departureAirport", false, null, interactionModel.findResourceStateMachine("Airport"));
		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "arrivalAirportCode", "arrivalAirport", false, null, interactionModel.findResourceStateMachine("Airport"));
		
		//Run the generator
		RimDslGenerator generator = new RimDslGenerator(createVelocityEngine());
		String dsl = generator.generateRimDsl(interactionModel,commands, true);
		
		//Check results
		assertTrue(dsl != null && !dsl.equals(""));
		assertEquals(readTextFile(RIM_DSL_AIRLINE_SIMPLE_FILE), dsl);
	}
	
	@Test
	public void testGenerateRimDslAirlines() {
		String dsl = createAirlineModelDSL(true);
		
		//Check results
		assertTrue(dsl != null && !dsl.equals(""));
		assertEquals(readTextFile(RIM_DSL_AIRLINE_FILE), dsl);
	}

	@Test
	public void testGenerateRimDslAirlinesNonStrictOData() {
		String dsl = createAirlineModelDSL(false);
		
		//Check results
		assertTrue(dsl != null && !dsl.equals(""));
		assertEquals(readTextFile(RIM_DSL_AIRLINE_NON_STRICT_ODATA_FILE), dsl);
	}

	public String createAirlineModelDSL(boolean strictOData) {
		//Define the basic interaction model based on the available metadata
		Metadata metadata = parseMetadata(METADATA_AIRLINE_XML_FILE);
		InteractionModel interactionModel = new InteractionModel(metadata);
		Commands commands = new Commands();
		
		commands.addCommand(Commands.GET_SERVICE_DOCUMENT, "com.temenos.interaction.commands.odata.GETMetadataCommand", Commands.GET_SERVICE_DOCUMENT, COMMAND_SERVICE_DOCUMENT, COMMAND_EDM_DATA_SERVICES);
		commands.addCommand(Commands.GET_METADATA, "com.temenos.interaction.commands.odata.GETMetadataCommand", Commands.GET_METADATA, COMMAND_METADATA, COMMAND_EDM_DATA_SERVICES);
		commands.addCommand(Commands.GET_ENTITIES, "com.temenos.interaction.commands.odata.GETEntitiesCommand", Commands.GET_ENTITIES, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.GET_ENTITY, "com.temenos.interaction.commands.odata.GETEntityCommand", Commands.GET_ENTITY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.CREATE_ENTITY, "com.temenos.interaction.commands.odata.CreateEntityCommand", Commands.CREATE_ENTITY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.GET_NAV_PROPERTY, "com.temenos.interaction.commands.odata.GETNavPropertyCommand", Commands.GET_NAV_PROPERTY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);

		//Add transitions but without reciprocal links
		interactionModel.findResourceStateMachine("Flight").addTransition("FlightSchedule", "flightScheduleNum", "flightschedule", false, null, interactionModel.findResourceStateMachine("FlightSchedule"), null, "flightschedule");
		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "departureAirportCode", "departureAirport", false, null, interactionModel.findResourceStateMachine("Airport"), null, "departureAirport");
		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "arrivalAirportCode", "arrivalAirport", false, null, interactionModel.findResourceStateMachine("Airport"), null, "arrivalAirport");
		interactionModel.findResourceStateMachine("Airport").addTransition("FlightSchedule", "departures", "departures", true, null, interactionModel.findResourceStateMachine("FlightSchedule"), "departureAirportCode eq '{code}'", "departures");
		interactionModel.findResourceStateMachine("Airport").addTransition("FlightSchedule", "arrivals", "arrivals", true, null, interactionModel.findResourceStateMachine("FlightSchedule"), "arrivalAirportCode eq '{code}'", "arrivals");
		
		//Run the generator
		RimDslGenerator generator = new RimDslGenerator(createVelocityEngine());
		return generator.generateRimDsl(interactionModel,commands, strictOData);
	}
	
	/*
	 * Create a velocity engine which loads velocity 
	 * templates from the classpath.
	 */
	private VelocityEngine createVelocityEngine() {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
		ve.init();
		return ve;
	}

	/*
	 * Parse a metadata file
	 */
	private Metadata parseMetadata(String metadataFile) {
		MetadataParser parser = new MetadataParser();
		InputStream is = parser.getClass().getClassLoader().getResourceAsStream(metadataFile);
		Metadata metadata = parser.parse(is);
		Assert.assertNotNull(metadata);
		return metadata;
	}

	/*
	 * Read a text file
	 */
	private String readTextFile(String textFile) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(textFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String read;
		try {
			while((read = br.readLine()) != null) {
			    sb.append(read).append(System.getProperty("line.separator"));
			}
		}
		catch(IOException ioe) {
			fail(ioe.getMessage());
		}
		return sb.toString();		
	}
}
