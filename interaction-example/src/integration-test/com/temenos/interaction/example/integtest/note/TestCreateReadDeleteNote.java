package com.temenos.interaction.example.integtest.note;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.temenos.interaction.example.note.client.NoteRepresentation;

public class TestCreateReadDeleteNote extends JerseyTest {

	public final static String NOTES_RESOURCE = "notes";
	
	/*
	@BeforeClass
	public static void initialiseTestDB() {
    	// bootstrap the NoteProducerFactory which creates the JPA entity manager (the CREATE TABLE)
    	new NoteProducerFactory();
    	TestDBUtils.fillNoteDatabase();
	}
	*/
	
	@Before
	public void initTest() {
		// TODO make this configurable
		// test with external server 
    	webResource = Client.create().resource("http://localhost:8080/example/rest"); 
	}
	
	@After
	public void tearDown() {}

    public TestCreateReadDeleteNote() throws Exception {
    	//super("example", "rest", "com.temenos.interaction.example");
        // enable logging on base web resource
    	System.setProperty("enableLogging", "ya");
    }

    @Test
	public void testOptions() {
        String noteUri = NOTES_RESOURCE + "/1";
        ClientResponse response = webResource.path(noteUri).options(ClientResponse.class);
        assertEquals(200, response.getStatus());
        assertEquals(5, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("PUT"));
        assertTrue(response.getAllow().contains("DELETE"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

    @Test
	public void testDelete() {
		// delete Note number 1 (which exists)
		String noteUri = NOTES_RESOURCE + "/2";
		ClientResponse response = webResource.path(noteUri).delete(ClientResponse.class);
        assertEquals(200, response.getStatus());

		// make sure Note number 1 is really gone
		String deletedNoteUri = NOTES_RESOURCE + "/2";
		ClientResponse deletedResponse = webResource.path(deletedNoteUri).get(ClientResponse.class);
        assertEquals(404, deletedResponse.getStatus());

		// delete Note number 56 (which does not exist)
		String notFoundNoteUri = NOTES_RESOURCE + "/56";
		ClientResponse nresponse = webResource.path(notFoundNoteUri).delete(ClientResponse.class);
        assertEquals(200, nresponse.getStatus());
    }

    /*
     * This test will use JAXB
     */
    @Test
	public void testCreateReadDeleteAPPLICATION_XML() {
		String noteUri = NOTES_RESOURCE + "/10";
		// not created yet (delete it to make sure as tests could be run multiple times)
        webResource.path(noteUri).delete();
        ClientResponse response = webResource.path(noteUri).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals(404, response.getStatus());
        
        // create (PUT) resource
        ClientResponse putResponse = webResource.path(noteUri).type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).put(ClientResponse.class, new NoteRepresentation("test note"));
        // now created
        assertEquals(200, putResponse.getStatus());
		
        // GET new resource
        ClientResponse getResponse = webResource.path(noteUri).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals(200, getResponse.getStatus());
        String noteXMLRepresentation = getResponse.getEntity(String.class);
//      NoteRepresentation nr = getResponse.getEntity(NoteRepresentation.class);
        NoteRepresentation nr = NoteRepresentation.fromXmlString(noteXMLRepresentation);
        assertEquals("test note", nr.getBody());
	}

    /*
     * This test will use our HAL jax-rs Provider
     */
    @Test
	public void testCreateReadDeleteAPPLICATION_HAL_XML() {
		String noteUri = NOTES_RESOURCE + "/10";
		// not created yet (delete it to make sure as tests could be run multiple times)
        webResource.path(noteUri).delete();
        ClientResponse response = webResource.path(noteUri).accept(com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML).get(ClientResponse.class);
        assertEquals(404, response.getStatus());
        
        // create (PUT) resource
        ClientResponse putResponse = webResource.path(noteUri).type(com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML).accept(com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML).put(ClientResponse.class, new NoteRepresentation("test note"));
        // now created
        assertEquals(200, putResponse.getStatus());
		
        // GET new resource
        ClientResponse getResponse = webResource.path(noteUri).accept(com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML).get(ClientResponse.class);
        assertEquals(200, getResponse.getStatus());
        String noteHALRepresentation = getResponse.getEntity(String.class);
//      NoteRepresentation nr = getResponse.getEntity(NoteRepresentation.class);
        NoteRepresentation nr = NoteRepresentation.fromXmlString(noteHALRepresentation);
        assertEquals("test note", nr.getBody());
	}

}
