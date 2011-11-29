package com.temenos.interaction.example.integtest.note;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.temenos.interaction.example.integtest.utils.TestDBUtils;
import com.temenos.interaction.example.note.NoteProducerFactory;

public class TestNewNote extends JerseyTest {

	public final static String NEW_NOTE_RESOURCE = "notes/new";

	@BeforeClass
	public static void initialiseTestDB() {
    	// bootstrap the NoteProducerFactory which creates the JPA entity manager (the CREATE TABLE)
    	new NoteProducerFactory();
    	TestDBUtils.fillNoteDatabase();
	}
	
    public TestNewNote() throws Exception {
    	super("example", "rest", "com.temenos.interaction.example");
        // enable logging on base web resource
    	System.setProperty("enableLogging", "ya");
    }

    @Test
	public void testOptions() {
        String noteUri = NEW_NOTE_RESOURCE;
        ClientResponse response = webResource.path(noteUri).options(ClientResponse.class);
        assertEquals(200, response.getStatus());
        assertEquals(3, response.getAllow().size());
        assertTrue(response.getAllow().contains("POST"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

    @Test
	public void testPUTShouldFail() {
		String fabricatedNewNoteUri = NEW_NOTE_RESOURCE + "/10";
        // PUT to 'new' note resource
        ClientResponse putResponse = webResource.path(fabricatedNewNoteUri).put(ClientResponse.class, "blah");
        // PUT should return not implemented
        assertEquals(501, putResponse.getStatus());
	}

    @Test
	public void testPOSTWithIdShouldFail() {
		String fabricatedNewNoteUri = NEW_NOTE_RESOURCE + "/10";
        // POST with an ID to 'new' note resource
        ClientResponse postResponse = webResource.path(fabricatedNewNoteUri).post(ClientResponse.class, "blah");
        // POST should return error as id not supported
        assertEquals(500, postResponse.getStatus());
	}

    @Test
	public void testPOSTNextNoteIDString() {
		String newNoteUri = NEW_NOTE_RESOURCE;
        // POST to 'new' note resource
        ClientResponse postResponse = webResource.path(newNoteUri).type(MediaType.TEXT_PLAIN).post(ClientResponse.class);
        // POST should return not implemented
        assertEquals(Response.Status.OK, postResponse.getStatus());
		// next ID should be 2
        assertEquals("2", postResponse.getEntity(String.class));
	}

    @Test
	public void testPOSTNextNoteIDXML() {
		String newNoteUri = NEW_NOTE_RESOURCE;
        // POST to 'new' note resource
        ClientResponse postResponse = webResource.path(newNoteUri).type(MediaType.TEXT_XML).post(ClientResponse.class, "<resource/>");
        // POST should return not implemented
        assertEquals(Response.Status.OK, postResponse.getStatus());
		// next ID should be 2
        assertEquals("2", postResponse.getEntity(String.class));
	}

}
