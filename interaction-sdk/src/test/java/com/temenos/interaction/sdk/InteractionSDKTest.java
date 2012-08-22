package com.temenos.interaction.sdk;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
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
    public static final String TEST_ARTIFACT_ID = "FlightResponder";

    @Before
    public void setUp() throws VerificationException, IOException {
        Verifier verifier;
        verifier = new Verifier(ROOT.getAbsolutePath());
        verifier.deleteArtifact(TEST_GROUP_ID, TEST_ARTIFACT_ID, System.getProperty("flightResponderVersion"), null);		//Remove archetype output artefacts
        verifier.deleteDirectory(TEST_ARTIFACT_ID);											//Remove the maven project
    }

    private Properties getSystemProperties() {
        Properties props = new Properties(System.getProperties());
        props.put("archetypeGroupId", ARCHETYPE_GROUP_ID);
        props.put("archetypeArtifactId", ARCHETYPE_ARTEFACT_ID);
        props.put("archetypeVersion", System.getProperty("archetypeVersion"));
        props.put("groupId", TEST_GROUP_ID);
        props.put("artifactId", TEST_ARTIFACT_ID);
        props.put("version", System.getProperty("flightResponderVersion"));
        props.put("interactiveMode", "false");

        return props;
    }

    @Test
    public void testCreateFlightResponder() throws VerificationException {
        //Generate the archetype
        Verifier verifier = new Verifier(ROOT.getAbsolutePath());
        verifier.setSystemProperties(getSystemProperties());
        verifier.setAutoclean(false);
        verifier.executeGoal("archetype:generate");
        verifier.verifyErrorFreeLog();

        //Verify the archetype
        verifier = new Verifier(ROOT.getAbsolutePath() + "/" + TEST_ARTIFACT_ID);
        verifier.setAutoclean(true);
        verifier.executeGoal("verify");
        verifier.verifyErrorFreeLog();
        
        //Run the Interaction SDK to generate the Flight responder project
        verifier.executeGoal("interaction-sdk:gen");
        verifier.verifyErrorFreeLog();
        
        //Verify the Flight responder project
        verifier.executeGoal("verify");
        verifier.verifyErrorFreeLog();
    }
}
