package com.temenos.interaction.example.integtest.note;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.temenos.interaction.core.decorator.hal.MediaType;
import com.temenos.interaction.example.integtest.utils.TestDBUtils;
import com.temenos.interaction.example.note.NoteProducerFactory;

public class TestCreateReadDeleteNote extends JerseyTest {

	public final static String NOTES_RESOURCE = "notes";

	@BeforeClass
	public static void initialiseTestDB() {
    	// bootstrap the NoteProducerFactory which creates the JPA entity manager (the CREATE TABLE)
    	new NoteProducerFactory();
    	TestDBUtils.fillNoteDatabase();
	}
	
    public TestCreateReadDeleteNote() throws Exception {
    	super("example", "rest", "com.temenos.interaction.example");
        // enable logging on base web resource
    	System.setProperty("enableLogging", "ya");
    }

    @Test
	public void testOptions() {
        String noteUri = NOTES_RESOURCE + "/1";
        ClientResponse response = webResource.path(noteUri).options(ClientResponse.class);
        assertEquals(200, response.getStatus());
	}

    @Test
	public void testCreateReadDelete() {
		String noteUri = NOTES_RESOURCE + "/10";
		// not created yet
        ClientResponse response = webResource.path(noteUri).accept(MediaType.APPLICATION_HAL_XML).get(ClientResponse.class);
        assertEquals(404, response.getStatus());
        
        // create (PUT) resource
        ClientResponse putResponse = webResource.path(noteUri).type(MediaType.APPLICATION_HAL_XML).accept(MediaType.APPLICATION_HAL_XML).put(ClientResponse.class, new NoteRepresentation("test note"));
        // now created
        assertEquals(200, putResponse.getStatus());
		
        // GET new resource
        ClientResponse getResponse = webResource.path(noteUri).accept(MediaType.APPLICATION_HAL_XML).get(ClientResponse.class);
        assertEquals(200, getResponse.getStatus());
        String noteHALRepresentation = getResponse.getEntity(String.class);
//      NoteRepresentation nr = getResponse.getEntity(NoteRepresentation.class);
        NoteRepresentation nr = NoteRepresentation.fromXmlString(noteHALRepresentation);
        assertEquals("test note", nr.getBody());

	}

}
