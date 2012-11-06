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
import com.temenos.interaction.sdk.interaction.InteractionModel;

/**
 * Unit test for {@link RimDslGenerator}.
 */
public class TestRimDslGenerator {
	public final static String METADATA_AIRLINE_XML_FILE = "AirlinesMetadata.xml";
	public final static String RIM_DSL_AIRLINE_FILE = "Airlines.rim";

	@Test
	public void testGenerateRimDslWithoutReciprocalLinks() {
		//Define the basic interaction model based on the available metadata
		Metadata metadata = parseMetadata(METADATA_AIRLINE_XML_FILE);
		InteractionModel interactionModel = new InteractionModel(metadata);

		//Add transitions but without reciprocal links
		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "departureAirportCode", "departureAirport", false, "", interactionModel.findResourceStateMachine("Airport"));
		interactionModel.findResourceStateMachine("FlightSchedule").addTransition("Airport", "arrivalAirportCode", "arrivalAirport", false, null, interactionModel.findResourceStateMachine("Airport"));
		interactionModel.findResourceStateMachine("Airport").addTransition("FlightSchedule", "departureAirportCode", "flightSchedules", true, "", interactionModel.findResourceStateMachine("FlightSchedule"));
		
		//Run the generator
		RimDslGenerator generator = new RimDslGenerator(createVelocityEngine());
		String dsl = generator.generateRimDsl(interactionModel);
		
		//Check results
		assertTrue(dsl != null && !dsl.equals(""));
		assertEquals(readTextFile(RIM_DSL_AIRLINE_FILE), dsl);
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
			    sb.append(read).append("\r\n");
			}
		}
		catch(IOException ioe) {
			fail(ioe.getMessage());
		}
		return sb.toString();		
	}
}
