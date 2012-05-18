package com.interaction.example.hateoas.simple;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.temenos.interaction.core.media.hal.MediaType;

/**
 * This test ensures that we can navigate from one application state
 * to another using hypermedia (links).
 * 
 * @author aphethean
 */
public class HypermediaITCase extends JerseyTest {

	public HypermediaITCase() throws Exception {
		super();
	}
	
	@Before
	public void initTest() {
		// TODO make this configurable
		// test with external server 
    	webResource = Client.create().resource(Configuration.TEST_ENDPOINT_URI); 
	}

	@After
	public void tearDown() {}


	/**
	 * Attempt a DELETE to the notes resource (a collection resource)
	 */
	@Test
	public void deletePersonMethodNotAllowed() throws Exception {
		// attempt to delete the Person root, rather than an individual
		ClientResponse response = webResource.path("/notes").delete(ClientResponse.class);
        assertEquals(405, response.getStatus());

        assertEquals(3, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

	/**
	 * Attempt a PUT to the notes resource (a collection resource)
	 */
	@Test
	public void putPersonMethodNotAllowed() throws Exception {
		String halRequest = "";
		// attempt to put to the notes collection, rather than an individual
		ClientResponse response = webResource.path("/notes").type(MediaType.APPLICATION_HAL_JSON).put(ClientResponse.class, halRequest);
        assertEquals(405, response.getStatus());

        assertEquals(3, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

}
