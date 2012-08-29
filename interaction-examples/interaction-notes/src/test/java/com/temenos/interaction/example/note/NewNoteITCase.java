package com.temenos.interaction.example.note;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import org.custommonkey.xmlunit.Diff;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.theoryinpractise.halbuilder.RepresentationFactory;
import com.theoryinpractise.halbuilder.spi.ReadableRepresentation;

public class NewNoteITCase extends JerseyTest {

	public final static String NEW_NOTE_RESOURCE = "notes/new";

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
    	webResource = Client.create().resource("http://localhost:8080/example/rest"); 
	}
	
	@After
	public void tearDown() {}
	
    public NewNoteITCase() throws Exception {
    	/* Allows standalone Jersey Test
    	super("example", "rest", "com.temenos.interaction.example");
		*/
        // enable logging on base web resource
    	System.setProperty("enableLogging", "ya");
    }

    @Test
	public void testOptions() {
        String noteUri = NEW_NOTE_RESOURCE;
        ClientResponse response = webResource.path(noteUri).options(ClientResponse.class);
        assertEquals(200, response.getStatus());
        assertEquals(4, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("POST"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

    @Test
	public void testPUTShouldFail() {
		String fabricatedNewNoteUri = NEW_NOTE_RESOURCE;
        // PUT to 'new' note resource
        ClientResponse putResponse = webResource.path(fabricatedNewNoteUri).put(ClientResponse.class, "blah");
        // PUT should return 415, we know this media type is unacceptable for the server (not completely sure this is right)
        assertEquals(415, putResponse.getStatus());
	}

    @Test
	public void testPUTShouldFailXML() {
		String fabricatedNewNoteUri = NEW_NOTE_RESOURCE;
        // PUT to 'new' note resource
        ClientResponse putResponse = webResource.path(fabricatedNewNoteUri).type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).put(ClientResponse.class, "<resource></resource>");
        // PUT does not have a command registered, should return method not allowed
        assertEquals(405, putResponse.getStatus());
        // as per the http spec, 405 MUST include an Allow header
        assertEquals(4, putResponse.getAllow().size());
        assertTrue(putResponse.getAllow().contains("GET"));
        assertTrue(putResponse.getAllow().contains("POST"));
        assertTrue(putResponse.getAllow().contains("OPTIONS"));
        assertTrue(putResponse.getAllow().contains("HEAD"));
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
		// GET the ID of the most next note
        ClientResponse getResponse = webResource.path(newNoteUri).type(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML).accept(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML).get(ClientResponse.class);
        InputStream getRespIS = getResponse.getEntityInputStream();
        
        ReadableRepresentation halResource = new RepresentationFactory().readRepresentation(new InputStreamReader(getRespIS));
        String lastIdStr = halResource.get("lastId").get().toString();
        long lastId = Long.valueOf(lastIdStr).longValue();
 
        // work out what the next id should be
		Long nextID = lastId + 1;
        String nextIDStr = Long.toString(nextID);
		
        // POST to 'new' note resource
        ClientResponse postResponse = webResource.path(newNoteUri).type(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML).accept(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML).post(ClientResponse.class, "<resource/>");
        // POST should return not implemented
        assertEquals(200, postResponse.getStatus());
		// next note ID should be 2
        String actualXML = createFlatXML(postResponse.getEntity(String.class));
		String expectedXML = "<resource href=\"http://localhost:8080/example/rest/notes/new\"><link href=\"PUT http://localhost:8080/example/rest/notes/" + nextIDStr + "\" rel=\"item\" name=\"ID.new>note.exists\"/><lastId>" + nextIDStr + "</lastId></resource>";
		
		Diff diff = new Diff(expectedXML, actualXML);
		// don't worry about the order of the elements in the xml
		assertTrue(diff.similar());
	}

	private String createFlatXML(String responseString) throws Exception {
		responseString = responseString.replaceAll(System.getProperty("line.separator"), "");
		responseString = responseString.replaceAll(">\\s+<", "><");
		return responseString;
	}

    /*
     * TODO - I think wink JSON support is rubbish at the moment
    @Test
	public void testPOSTNextNoteIDJSON() {
		String newNoteUri = NEW_NOTE_RESOURCE;
        // POST to 'new' note resource
        ClientResponse postResponse = webResource.path(newNoteUri).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, "{\"resource\":{}}");
        // POST should return not implemented
        assertEquals(200, postResponse.getStatus());
		// next ID should be 2
        assertEquals("2", postResponse.getEntity(String.class));
	}
	*/
}
