package com.temenos.interaction.example.odata.notes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;

public class CreateReadDeleteNoteITCase extends JerseyTest {

	public final static String PERSONS_RESOURCE = "/Persons";
	public final static String NOTES_RESOURCE = "/Notes";
	
	/* Allows standalone Jersey Test
	@BeforeClass
	public static void initialiseTestDB() {
    	// bootstrap the NoteProducerFactory which creates the JPA entity manager (the CREATE TABLE)
    	new NoteProducerFactory();
	}
	 */

	@Before
	public void initTest() {
		// TODO make this configurable
		// test with external server 
    	webResource = Client.create().resource(Configuration.TEST_ENDPOINT_URI); 

    	// Create note 3, linked to person 2 if it doesn't exist
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();
		OEntity person = consumer.getEntity("Persons", 2).execute();
		if (person == null) {
			person = consumer
						.createEntity("Persons")
						.properties(OProperties.string("name", "Ron"))
						.execute();
		}
		OEntity note = consumer.getEntity("Notes", 3).execute();
		if (note == null) {
			note = consumer
					.createEntity("Notes")
					.properties(OProperties.string("body", "test"))
					.link("Persons", person)
					.execute();
		}		
	}
	
	@After
	public void tearDown() {}

    public CreateReadDeleteNoteITCase() throws Exception {
    	/* Allows standalone Jersey Test
    	super("example", "rest", "com.temenos.interaction.example");
		*/
        // enable logging on base web resource
    	System.setProperty("enableLogging", "ya");
    }
    
    @Test
	public void testOptions() {
        String noteUri = NOTES_RESOURCE + "(1)";
        ClientResponse response = webResource.path(noteUri).options(ClientResponse.class);
        assertEquals(200, response.getStatus());
        assertEquals(6, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("PUT"));
        assertTrue(response.getAllow().contains("POST"));
        assertTrue(response.getAllow().contains("DELETE"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

    @Test
	public void testDeleteNote() {
		String noteUri = NOTES_RESOURCE + "(3)";

        // delete Note number 3 (which should now exists see initTest)
		ClientResponse response = webResource.path(noteUri).delete(ClientResponse.class);
        assertEquals(204, response.getStatus());

		// make sure Note number 3 is really gone
		ClientResponse deletedResponse = webResource.path(noteUri).get(ClientResponse.class);
        assertEquals(404, deletedResponse.getStatus());

		// delete Note number 56 (which does not exist)
		String notFoundNoteUri = NOTES_RESOURCE + "(56)";
		ClientResponse nresponse = webResource.path(notFoundNoteUri).delete(ClientResponse.class);
        assertEquals(204, nresponse.getStatus());
    }

    @Test
	public void testDeletePerson() {
		// delete Person number 1 (which exists), but we have bound a NoopDELETECommand
		String noteUri = PERSONS_RESOURCE + "(1)";
		ClientResponse response = webResource.path(noteUri).delete(ClientResponse.class);
        assertEquals(405, response.getStatus());
    }

    // TODO test disabled until AtomXMLProvider support better matching of URIs to resources
    // @Test
    public void testUpdateNote() {
    	// Create note for person 1
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();
		OEntity person = consumer.getEntity("Persons", 1).execute();
		OEntity note = consumer
					.createEntity("Notes")
					.properties(OProperties.string("body", "test"))
					.link("Persons", person)
					.execute();
		// update the note text
		consumer.updateEntity(note)
				.properties(OProperties.string("body", "new text for note"))
				.execute();

		// TODO read the note again, check text
		
    }
}
