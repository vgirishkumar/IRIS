package com.interaction.example.hateoas.banking;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.temenos.interaction.core.media.hal.MediaType;
import com.theoryinpractise.halbuilder.ResourceFactory;
import com.theoryinpractise.halbuilder.spi.Link;
import com.theoryinpractise.halbuilder.spi.ReadableResource;
import com.theoryinpractise.halbuilder.spi.Resource;

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


	@Test
	public void testGetEntryPointLinks() {
		ClientResponse response = webResource.path("/").accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());

		ResourceFactory resourceFactory = new ResourceFactory();
		ReadableResource resource = resourceFactory.newResource(new InputStreamReader(response.getEntityInputStream()));

		List<Link> links = resource.getLinks();
		assertEquals(3, links.size());
		for (Link link : links) {
			if (link.getRel().equals("self")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/", link.getHref());
			} else if (link.getName().get().equals("home.initial>Preferences.preferences")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/preferences", link.getHref());
			} else if (link.getName().get().equals("home.initial>FundsTransfer.initial")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/fundtransfers", link.getHref());
			} else {
				fail("unexpected link");
			}
		}
	}
	
	@Test
	public void testCollectionLinks() {
		ClientResponse response = webResource.path("/fundtransfers").accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());

		ResourceFactory resourceFactory = new ResourceFactory();
		ReadableResource resource = resourceFactory.newResource(new InputStreamReader(response.getEntityInputStream()));

		// the links from the collection
		List<Link> links = resource.getLinks();
		assertEquals(2, links.size());
		for (Link link : links) {
			if (link.getRel().equals("self")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/fundtransfers", link.getHref());
			} else if (link.getName().get().equals("FundsTransfer.initial>FundsTransfer.new")) {
				assertEquals("POST " + Configuration.TEST_ENDPOINT_URI + "/fundtransfers/new", link.getHref());
			} else {
				fail("unexpected link");
			}
		}
		
		// the links on each item
		List<Resource> subresources = resource.getResources();
		assertNotNull(subresources);
		assertEquals(3, subresources.size());
		for (Resource item : subresources) {
			List<Link> itemLinks = item.getLinks();
			assertEquals(1, itemLinks.size());
			for (Link link : itemLinks) {
				if (link.getRel().contains("self")) {
					assertEquals(Configuration.TEST_ENDPOINT_URI + "/fundtransfers/" + item.getProperties().get("id"), link.getHref());
				} else {
					fail("unexpected link");
				}
			}
		}
	}

	/**
	 * Attempt a DELETE to the resource (a collection resource)
	 */
	@Test
	public void deleteFundTransferMethodNotAllowed() throws Exception {
		ClientResponse response = webResource.path("/fundtransfers").delete(ClientResponse.class);
        assertEquals(405, response.getStatus());

        assertEquals(3, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

	/**
	 * Attempt a PUT to the resource (a collection resource)
	 */
	@Test
	public void putFundTransferMethodNotAllowed() throws Exception {
		String halRequest = "";
		ClientResponse response = webResource.path("/fundtransfers").type(MediaType.APPLICATION_HAL_JSON).put(ClientResponse.class, halRequest);
        assertEquals(405, response.getStatus());

        assertEquals(3, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

	/**
	 * Attempt a PUT to the resource (entity resource)
	 */
	@Test
	public void putFundTransferHalXML() throws Exception {
		String halRequest = "<resource><FundTransfer><id>123</id><body>Funds tranfer issued at 01/01/2012</body></FundTransfer><links></links></resource>";
		ClientResponse response = webResource.path("/fundtransfers/123").accept(MediaType.APPLICATION_HAL_XML).type(MediaType.APPLICATION_HAL_XML).put(ClientResponse.class, halRequest);
        assertEquals(200, response.getStatus());

		response = webResource.path("/fundtransfers/123").accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());
	}
}
