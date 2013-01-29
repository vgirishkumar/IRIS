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
import com.temenos.interaction.sdk.JPAResponderGen;
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

		//Add transitions
		IMResourceStateMachine rsmFlightSchedule = interactionModel.findResourceStateMachine("FlightSchedule");
		IMResourceStateMachine rsmAirport = interactionModel.findResourceStateMachine("Airport");
		IMResourceStateMachine rsmFlight = interactionModel.findResourceStateMachine("Flight");
		IMResourceStateMachine rsmPassenger = interactionModel.findResourceStateMachine("Passenger");
		
		rsmFlightSchedule.addTransitionToEntityState("flightschedule", rsmAirport, "departureAirport", "departureAirportCode", null);
		rsmFlightSchedule.addTransitionToEntityState("flightschedule", rsmAirport, "arrivalAirport", "arrivalAirportCode", null);

		rsmFlightSchedule.addPseudoStateTransition("FlightSchedules", "created", "POST", null, "CreateEntity", null, true);
		rsmFlight.addPseudoStateTransition("Flights", "created", "POST", null, "CreateEntity", null, true);
		rsmAirport.addPseudoStateTransition("Airports", "created", "POST", null, "CreateEntity", null, true);
		rsmPassenger.addPseudoStateTransition("Passenger", "created", "POST", null, "CreateEntity", null, true);
		
		//Run the generator
		RimDslGenerator generator = new RimDslGenerator(createVelocityEngine());
		String dsl = generator.generateRimDsl(interactionModel, JPAResponderGen.getDefaultCommands(), true);
		
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
		Commands commands =  JPAResponderGen.getDefaultCommands();
		commands.addCommand("AuthoriseEntity", "com.temenos.interaction.commands.odata.UpdateEntityCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand("ReverseEntity", "com.temenos.interaction.commands.odata.UpdateEntityCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand("GETReversedEntities", "com.temenos.interaction.commands.odata.GETEntitiesCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand("GETReversedEntity", "com.temenos.interaction.commands.odata.GETEntityCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addRimEvent("AUTHORISE", "PUT");
		commands.addRimEvent("REVERSE", "PUT");

		//Add state transitions
		IMResourceStateMachine rsm = interactionModel.findResourceStateMachine("Sector");
		rsm.addCollectionAndEntityState("IAuth", "unauthorised input records");
		rsm.addCollectionAndEntityState("Reversed", "reversed records", "GETReversedEntities", "GETReversedEntity");

		IMPseudoState pseudoState = rsm.addPseudoStateTransition("Sectors", "input", "POST", null, "CreateEntity", null, true);
		pseudoState.addAutoTransition(rsm.getResourceState("sector_IAuth"), "GET");
		pseudoState.addAutoTransition(rsm.getResourceState("sector"), "GET");

		pseudoState = rsm.addPseudoStateTransition("sector_IAuth", "authorise", "PUT", "authorise", "AuthoriseEntity", "edit", false);
		pseudoState.addAutoTransition(rsm.getResourceState("sector_IAuth"), "GET");
		pseudoState.addAutoTransition(rsm.getResourceState("sector"), "GET");
		
		pseudoState = rsm.addPseudoStateTransition("sector_IAuth", "delete", "DELETE", "delete", "DeleteEntity", "edit", false);
		pseudoState.addAutoTransition(rsm.getResourceState("sector"), "GET");

		pseudoState = rsm.addPseudoStateTransition("sector", "reverse", "REVERSE", "reverse", "ReverseEntity", "edit", false);
		pseudoState.addAutoTransition(rsm.getResourceState("sector_Reversed"), "GET");
		
		//Run the generator
		RimDslGenerator generator = new RimDslGenerator(createVelocityEngine());
		String dsl = generator.generateRimDsl(interactionModel, commands, false);
		assertEquals(readTextFile(RIM_DSL_BANKING_FILE), dsl);
	}
	
	public String createAirlineModelDSL(boolean strictOData) {
		//Define the basic interaction model based on the available metadata
		Metadata metadata = parseMetadata(METADATA_AIRLINE_XML_FILE);
		InteractionModel interactionModel = new InteractionModel(metadata);

		//Add transitions
		IMResourceStateMachine rsmFlightSchedule = interactionModel.findResourceStateMachine("FlightSchedule");
		IMResourceStateMachine rsmAirport = interactionModel.findResourceStateMachine("Airport");
		IMResourceStateMachine rsmFlight = interactionModel.findResourceStateMachine("Flight");
		IMResourceStateMachine rsmPassenger = interactionModel.findResourceStateMachine("Passenger");
		
		rsmFlight.addTransitionToEntityState("flight", rsmFlightSchedule, "flightschedule", "flightScheduleNum", "flightschedule");
		rsmFlightSchedule.addTransitionToEntityState("flightschedule", rsmAirport, "departureAirport", "departureAirportCode", "departureAirport");
		rsmFlightSchedule.addTransitionToEntityState("flightschedule", rsmAirport, "arrivalAirport", "arrivalAirportCode", "arrivalAirport");
		rsmPassenger.addTransitionToEntityState("passenger", rsmFlight, "flight", "flightID", "flight");

		rsmAirport.addTransitionToCollectionState("airport", rsmFlightSchedule, "departures", "departureAirportCode eq '{code}'", "departures");
		rsmAirport.addTransitionToCollectionState("airport", rsmFlightSchedule, "arrivals", "arrivalAirportCode eq '{code}'", "arrivals");

		rsmFlightSchedule.addPseudoStateTransition("FlightSchedules", "created", "POST", null, "CreateEntity", null, true);
		rsmFlight.addPseudoStateTransition("Flights", "created", "POST", null, "CreateEntity", null, true);
		rsmAirport.addPseudoStateTransition("Airports", "created", "POST", null, "CreateEntity", null, true);
		rsmPassenger.addPseudoStateTransition("Passenger", "created", "POST", null, "CreateEntity", null, true);
		
		//Run the generator
		RimDslGenerator generator = new RimDslGenerator(createVelocityEngine());
		return generator.generateRimDsl(interactionModel, JPAResponderGen.getDefaultCommands(), strictOData);
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
