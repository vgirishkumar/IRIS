package com.temenos.interaction.example.integtest.note;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.custommonkey.xmlunit.XMLAssert;
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
	public void testOptionsNotFound() {
        String noteUri = NEW_NOTE_RESOURCE;
        ClientResponse response = webResource.path(noteUri).options(ClientResponse.class);
        assertEquals(404, response.getStatus());
	}

    @Test
	public void testPUTShouldFail() {
		String fabricatedNewNoteUri = NEW_NOTE_RESOURCE;
        // PUT to 'new' note resource
        ClientResponse putResponse = webResource.path(fabricatedNewNoteUri).put(ClientResponse.class, "blah");
        // PUT should return not implemented
        assertEquals(501, putResponse.getStatus());
	}

    @Test
	public void testPOSTWithIdShouldFail() {
		String fabricatedNewNoteUri = NEW_NOTE_RESOURCE + "/10";
        // POST with an ID to 'new' note resource
        ClientResponse postResponse = webResource.path(fabricatedNewNoteUri).type(MediaType.TEXT_PLAIN).post(ClientResponse.class, "blah");
        // POST should return error as id not supported
        assertEquals(404, postResponse.getStatus());
	}

    /* TODO disabled
    @Test
	public void testPOSTNextNoteIDString() {
		String newNoteUri = NEW_NOTE_RESOURCE;
        // POST to 'new' note resource
        ClientResponse postResponse = webResource.path(newNoteUri).type(MediaType.TEXT_PLAIN).post(ClientResponse.class);
        // POST should return not implemented
        assertEquals(Response.Status.OK.getStatusCode(), postResponse.getStatus());
		// next ID should be 2
        assertEquals("2", postResponse.getEntity(String.class));
	}
*/
    @Test
	public void testPOSTNextNoteIDXML() throws Exception {
		String newNoteUri = NEW_NOTE_RESOURCE;
        // POST to 'new' note resource
        ClientResponse postResponse = webResource.path(newNoteUri).type(com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML).accept(com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML).post(ClientResponse.class, "<resource/>");
        // POST should return not implemented
        assertEquals(200, postResponse.getStatus());
		// next note ID should be 2
        String actualXML = postResponse.getEntity(String.class);
		String expectedXML = "<resource><links><link href=\"/notes/2\" rel=\"_new\" title=\"NewNote\"/></links></resource>";
		XMLAssert.assertXMLEqual(expectedXML, actualXML);
	}

    /*
    @Test
	public void testPOSTNextNoteIDJSON() {
		String newNoteUri = NEW_NOTE_RESOURCE;
        // POST to 'new' note resource
		// TODO change type to JSON, default accept type is JSON already
        ClientResponse postResponse = webResource.path(newNoteUri).type(com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML).post(ClientResponse.class, "<resource/>");
        // POST should return not implemented
        assertEquals(200, postResponse.getStatus());
		// next ID should be 2
        assertEquals("2", postResponse.getEntity(String.class));
	}
*/
}
