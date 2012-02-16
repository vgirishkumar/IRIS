package com.temenos.interaction.example.sandbox;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;

public class SandboxRIMITCase extends JerseyTest {

	 @Before
	 public void initTest() { 
		 // TODO make this configurable 
		 // test with external server 
		 webResource = Client.create().resource("http://localhost:8080/example/rest");
	 }
	 
	 @After
	 public void tearDown() {}

	public SandboxRIMITCase() throws Exception {
//		super("example", "rest", "com.temenos.interaction.example");
		// enable logging on base web resource
		System.setProperty("enableLogging", "ya");
	}

	@Test
	public void testPUTXMLEntityResourceNoObject() throws Exception {
		String sandboxUri = "/sandbox/abc";
        // create (PUT) resource
		String request = "<resource></resource>";
        ClientResponse putResponse = webResource.path(sandboxUri).type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).put(ClientResponse.class, request);
        // now created
        assertEquals(200, putResponse.getStatus());
		
        String putRespRepresentation = putResponse.getEntity(String.class);
		XMLAssert.assertXMLEqual("<resource><book><title>Farms</title><description>A kids book</description></book></resource>", putRespRepresentation);
		
	}
	
	/*
	 * TODO make json work, think the wink JSON provider is just rubbish
	@Test
	public void testPUTJSONEntityResourceNoObject() {
		String sandboxUri = "/sandbox/abc";

        ClientResponse getResponse = webResource.path(sandboxUri).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(200, getResponse.getStatus());
        String getRespRepresentation = getResponse.getEntity(String.class);
        assertEquals("{\"book\":{\"title\":\"Farms\",\"description\":\"A kids book\"}}", getRespRepresentation);
		
		// create (PUT) resource
		String request = "{\"resource\":{\"\"}}";
        ClientResponse putResponse = webResource.path(sandboxUri).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, request);
        // now created
        assertEquals(200, putResponse.getStatus());
		
        String putRespRepresentation = putResponse.getEntity(String.class);
        assertEquals("{\"book\":{\"title\":\"Farms\",\"description\":\"A kids book\"}}", putRespRepresentation);
	}
	*/
}
