package com.temenos.interaction.sdk;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        verifier.displayStreamBuffers();
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
    public void testCreateFlightResponder() throws VerificationException, InterruptedException {
        //Generate the archetype
        Verifier verifier = new Verifier(ROOT.getAbsolutePath());
        verifier.displayStreamBuffers();
        verifier.setSystemProperties(getSystemProperties());
        verifier.setAutoclean(false);
        verifier.executeGoal("archetype:generate");
        verifier.verifyErrorFreeLog();

        //Verify the archetype
        verifier = new Verifier(ROOT.getAbsolutePath() + "/" + TEST_ARTIFACT_ID);
        verifier.setAutoclean(true);
        // the RIM file has not been generated yet (see interaction-sdk:gen target)
        Properties props = new Properties();
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
        	FileUtils.copyFileToDirectory( System.getProperty("insertFile"), ROOT.getAbsolutePath() + "/" + TEST_ARTIFACT_ID + "/src/main/resources/META-INF");
        }
        catch(IOException ioe) {
        	new VerificationException("Failed to copy INSERT file.");
        }

        // Verify the Flight responder project (this target builds the war)
        verifier.executeGoal("verify");
        verifier.verifyErrorFreeLog();
        System.out.println("Finished responder generate");
        
        // start the Flight responder jetty server
        Helper helper = new Helper(verifier);
        helper.start();
        // wail a little while until started
        System.out.println("Waiting 5 seconds for server start..");
        Thread.sleep(5000);
        
        // run the integration tests
        Verifier airlineIntTests = new Verifier(new File("../interaction-examples/interaction-odata-airline").getAbsolutePath());
        verifier.displayStreamBuffers();
        Properties airlineProps = new Properties();
        airlineProps.put("TEST_ENDPOINT_URI", "http://localhost:8080/responder/FlightResponder.svc/");
        airlineIntTests.setSystemProperties(airlineProps);
        List<String> goals = new ArrayList<String>();
        goals.add("package");
        goals.add("failsafe:integration-test");
        goals.add("failsafe:verify");
        airlineIntTests.executeGoals(goals);
        airlineIntTests.verifyErrorFreeLog();

        // stop the Flight responder jetty server
        helper.stopFlightResponder();
        verifier.verifyErrorFreeLog();
        helper.join();
    }
    
    
    private class Helper extends Thread {
    	private Verifier verifier;
    	Helper(Verifier verifier) {
    		this.verifier = verifier;
    	}
    	public void run() {
    		try {
				startFlightResponder();
			} catch (VerificationException e) {
				throw new RuntimeException(e);
			}
    	}
    	void stopFlightResponder() throws VerificationException {
            Properties props = new Properties();
            props.put("argLine", "-DstopPort=8005 -DstopKey=STOP");
            verifier.setSystemProperties(props);
            verifier.executeGoal("jetty:stop");
    	}
    	
        void startFlightResponder() throws VerificationException {
        	verifier.setAutoclean(false);
            verifier.executeGoal("jetty:run");
        }
    }
    
}
