package com.temenos.interaction.sdk;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class InteractionSDKTest {

    public static final File ROOT = new File("target/test-classes/");

    public static final String ARCHETYPE_GROUP_ID = "com.temenos.interaction";
    public static final String ARCHETYPE_ARTEFACT_ID = "interaction-sdk-archetype";

    public static final String TEST_GROUP_ID = "com.mycorp.airtraffic";
    
    private Verifier verifier;

    @Before
    public void setUp() throws VerificationException, IOException {
        verifier = new Verifier(ROOT.getAbsolutePath());
    }

    private Properties getSystemProperties(String artifactId) {
        Properties props = new Properties(System.getProperties());
        props.put("archetypeGroupId", ARCHETYPE_GROUP_ID);
        props.put("archetypeArtifactId", ARCHETYPE_ARTEFACT_ID);
        props.put("archetypeVersion", System.getProperty("archetypeVersion"));
        props.put("groupId", TEST_GROUP_ID);
        props.put("artifactId", artifactId);
        props.put("version", System.getProperty("flightResponderVersion"));
        props.put("interactiveMode", "false");

        return props;
    }

    @Test
    public void testCreateFlightResponder() throws VerificationException, IOException {
    	String artifactId = "FlightResponder";
        verifier.deleteArtifact(TEST_GROUP_ID, artifactId, System.getProperty("flightResponderVersion"), null);		//Remove archetype output artefacts
        verifier.deleteDirectory(artifactId);											//Remove the maven project

        createFlightResponder(artifactId, true);
    }

    @Test
    public void testCreateFlightResponderNonStrictOData() throws VerificationException, IOException {
    	String artifactId = "FlightResponderNonStrictOData";
        verifier.deleteArtifact(TEST_GROUP_ID, artifactId, System.getProperty("flightResponderVersion"), null);		//Remove archetype output artefacts
        verifier.deleteDirectory(artifactId);											//Remove the maven project

        createFlightResponder(artifactId, false);
    }
    
    private void createFlightResponder(String artifactId, boolean strictOdata) throws VerificationException {
    	System.out.println("Creating project " + artifactId);
    	
        //Generate the archetype
        Verifier verifier = new Verifier(ROOT.getAbsolutePath());
        verifier.setSystemProperties(getSystemProperties(artifactId));
        verifier.setAutoclean(false);
        verifier.executeGoal("archetype:generate");
        verifier.verifyErrorFreeLog();

        //Verify the archetype
        verifier = new Verifier(ROOT.getAbsolutePath() + "/" + artifactId);
        verifier.setAutoclean(true);
        // the RIM file has not been generated yet (see interaction-sdk:gen target)
        Properties props = new Properties();
        props.put("strictOdata", strictOdata ? "true" : "false");
        props.put("skipRIMGeneration", "true");
        verifier.setSystemProperties(props);
        verifier.executeGoal("verify");
        verifier.verifyErrorFreeLog();
        
        //Run the Interaction SDK to generate the Flight responder project
        props.put("skipRIMGeneration", "false");
        verifier.setSystemProperties(props);
        verifier.executeGoal("interaction-sdk:gen");
        verifier.verifyErrorFreeLog();
        
        //Overwrite responder insert file
        try {
        	FileUtils.copyFileToDirectory( System.getProperty("insertFile"), ROOT.getAbsolutePath() + "/" + artifactId + "/src/main/resources/META-INF");
        }
        catch(IOException ioe) {
        	new VerificationException("Failed to copy INSERT file.");
        }

        //Verify the Flight responder project
        verifier.executeGoal("verify");
        verifier.verifyErrorFreeLog();
    }
}
