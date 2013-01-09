package com.temenos.interaction.sdk;

import static org.junit.Assert.assertFalse;

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
        
        // create a thread to run the integration tests
        Verifier airlineIntTests = new Verifier(new File("../interaction-examples/interaction-odata-airline").getAbsolutePath());
        Helper helper = new Helper(airlineIntTests);
        helper.start();

        // start the Flight responder jetty server
        startFlightResponder(verifier);

        // wait for the tests to finish
        helper.join();
        assertFalse(helper.hasFailed());
    }
    
    
    private class Helper extends Thread {
    	private Verifier verifier;
    	private boolean failed = false;
    	Helper(Verifier verifier) {
    		this.verifier = verifier;
    	}
    	public boolean hasFailed() {
    		return failed;
    	}
    	public void run() {
    		try {
    	        // wait a little while until started
    	        System.out.println("Waiting 10 seconds for server start..");
    	        Thread.sleep(10000);
    	        // run the integration tests
    	        verifier.displayStreamBuffers();
    	        Properties airlineProps = new Properties();
    	        airlineProps.put("TEST_ENDPOINT_URI", "http://localhost:8080/responder/FlightResponder.svc/");
    	        verifier.setSystemProperties(airlineProps);
    	        List<String> goals = new ArrayList<String>();
    	        goals.add("package");
    	        goals.add("failsafe:integration-test");
    	        goals.add("failsafe:verify");
    	        verifier.executeGoals(goals);
    	        verifier.verifyErrorFreeLog();
			} catch (InterruptedException e) {
				failed = true;
			} catch (VerificationException e) {
				e.printStackTrace();
				failed = true;
			}
            try {
				// stop the Flight responder jetty server
				stopFlightResponder();
			} catch (VerificationException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
    	}
    }

	void stopFlightResponder() throws VerificationException {
        Verifier verifier = new Verifier(ROOT.getAbsolutePath() + "/" + TEST_ARTIFACT_ID);
        verifier.displayStreamBuffers();
        verifier.setAutoclean(false);
//        Properties props = new Properties();
//        props.put("argLine", "-DstopPort=8005 -DstopKey=STOP");
//        verifier.setSystemProperties(props);
        verifier.executeGoal("jetty:stop");
	}
	
    void startFlightResponder(Verifier verifier) throws VerificationException {
    	verifier.setAutoclean(false);
        verifier.executeGoal("jetty:run");
    }

}
