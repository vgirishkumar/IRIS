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
import com.temenos.interaction.sdk.interaction.state.IMPseudoState;
import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.InteractionModel;

/**
 * Unit test for {@link RimDslGenerator}.
 */
public class TestRimDslGenerator {
	public final static String METADATA_AIRLINE_XML_FILE = "AirlinesMetadata.xml";
	public final static String RIM_DSL_AIRLINE_SIMPLE_FILE = "AirlinesSimple.rim";
	public final static String RIM_DSL_AIRLINE_FILE = "Airlines.rim";
	public final static String RIM_DSL_AIRLINE_NON_STRICT_ODATA_FILE = "AirlinesNonStrictOData.rim";

	public final static String METADATA_BANKING_XML_FILE = "Banking.xml";
	public final static String RIM_DSL_BANKING_FILE = "Banking.rim";
	
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
		IMResourceStateMachine rsmFlightSchedule = interactionModel.findResourceStateMachine("FlightSchedule");
		IMResourceStateMachine rsmAirport = interactionModel.findResourceStateMachine("Airport");
		IMResourceStateMachine rsmFlight = interactionModel.findResourceStateMachine("Flight");
		
		rsmFlightSchedule.addTransitionToEntityState("flightschedule", rsmAirport, "departureAirport", "departureAirportCode", null);
		rsmFlightSchedule.addTransitionToEntityState("flightschedule", rsmAirport, "arrivalAirport", "arrivalAirportCode", null);

		rsmFlightSchedule.addPseudoStateTransition("FlightSchedules", "created", "POST", null, "CreateEntity", null, true);
		rsmFlight.addPseudoStateTransition("Flights", "created", "POST", null, "CreateEntity", null, true);
		rsmAirport.addPseudoStateTransition("Airports", "created", "POST", null, "CreateEntity", null, true);
		
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

	@Test
	public void createBankingRimDsl() {
		//Define the basic interaction model based on the available metadata
		Metadata metadata = parseMetadata(METADATA_BANKING_XML_FILE);
		InteractionModel interactionModel = new InteractionModel(metadata);
		Commands commands = new Commands();
		
		commands.addCommand(Commands.GET_SERVICE_DOCUMENT, "com.temenos.interaction.commands.odata.GETMetadataCommand", Commands.GET_SERVICE_DOCUMENT, COMMAND_SERVICE_DOCUMENT, COMMAND_EDM_DATA_SERVICES);
		commands.addCommand(Commands.GET_METADATA, "com.temenos.interaction.commands.odata.GETMetadataCommand", Commands.GET_METADATA, COMMAND_METADATA, COMMAND_EDM_DATA_SERVICES);
		commands.addCommand(Commands.GET_ENTITIES, "com.temenos.interaction.commands.odata.GETEntitiesCommand", Commands.GET_ENTITIES, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.GET_ENTITY, "com.temenos.interaction.commands.odata.GETEntityCommand", Commands.GET_ENTITY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.CREATE_ENTITY, "com.temenos.interaction.commands.odata.CreateEntityCommand", Commands.CREATE_ENTITY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand("AuthoriseEntity", "com.temenos.interaction.commands.odata.UpdateEntityCommand", Commands.UPDATE_ENTITY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.DELETE_ENTITY, "com.temenos.interaction.commands.odata.DeleteEntityCommand", Commands.DELETE_ENTITY, COMMAND_METADATA_SOURCE_ODATAPRODUCER);

		//Add state transitions
		IMResourceStateMachine rsm = interactionModel.findResourceStateMachine("Sector");
		rsm.addStateTransition("sector", "Live", "GET", "live records", null, null, false);
		rsm.addStateTransition("sector", "IAuth", "GET", "unauthorised input records", null, null, false);
		IMPseudoState pseudoState = rsm.addPseudoStateTransition("Sectors", "input", "POST", null, "CreateEntity", null, true);
		pseudoState.addAutoTransition(rsm.getResourceState("IAuth"), "GET");
		pseudoState.addAutoTransition(rsm.getResourceState("Live"), "GET");
		pseudoState = rsm.addPseudoStateTransition("IAuth", "authorise", "PUT", "authorise", "AuthoriseEntity", "edit", false);
		pseudoState.addAutoTransition(rsm.getResourceState("IAuth"), "GET");
		pseudoState.addAutoTransition(rsm.getResourceState("Live"), "GET");
		pseudoState = rsm.addPseudoStateTransition("IAuth", "delete", "DELETE", "delete", "DeleteEntity", "edit", false);
		pseudoState.addAutoTransition(rsm.getResourceState("Live"), "GET");
		
		//Run the generator
		RimDslGenerator generator = new RimDslGenerator(createVelocityEngine());
		String dsl = generator.generateRimDsl(interactionModel,commands, false);
		assertEquals(readTextFile(RIM_DSL_BANKING_FILE), dsl);
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

		//Add transitions
		IMResourceStateMachine rsmFlightSchedule = interactionModel.findResourceStateMachine("FlightSchedule");
		IMResourceStateMachine rsmAirport = interactionModel.findResourceStateMachine("Airport");
		IMResourceStateMachine rsmFlight = interactionModel.findResourceStateMachine("Flight");
		
		rsmFlight.addTransitionToEntityState("flight", rsmFlightSchedule, "flightschedule", "flightScheduleNum", "flightschedule");
		rsmFlightSchedule.addTransitionToEntityState("flightschedule", rsmAirport, "departureAirport", "departureAirportCode", "departureAirport");
		rsmFlightSchedule.addTransitionToEntityState("flightschedule", rsmAirport, "arrivalAirport", "arrivalAirportCode", "arrivalAirport");

		rsmAirport.addTransitionToCollectionState("airport", rsmFlightSchedule, "departures", "departureAirportCode eq '{code}'", "departures");
		rsmAirport.addTransitionToCollectionState("airport", rsmFlightSchedule, "arrivals", "arrivalAirportCode eq '{code}'", "arrivals");

		rsmFlightSchedule.addPseudoStateTransition("FlightSchedules", "created", "POST", null, "CreateEntity", null, true);
		rsmFlight.addPseudoStateTransition("Flights", "created", "POST", null, "CreateEntity", null, true);
		rsmAirport.addPseudoStateTransition("Airports", "created", "POST", null, "CreateEntity", null, true);
		
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
