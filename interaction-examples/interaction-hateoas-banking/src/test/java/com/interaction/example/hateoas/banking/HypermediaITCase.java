package com.interaction.example.hateoas.banking;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.temenos.interaction.core.media.hal.MediaType;
import com.theoryinpractise.halbuilder.RepresentationFactory;
import com.theoryinpractise.halbuilder.spi.Link;
import com.theoryinpractise.halbuilder.spi.ReadableRepresentation;
import com.theoryinpractise.halbuilder.spi.Representation;

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

		RepresentationFactory representationFactory = new RepresentationFactory();
		ReadableRepresentation resource = representationFactory.readRepresentation(new InputStreamReader(response.getEntityInputStream()));

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
				fail("unexpected link [" + link.getName().get() + "]");
			}
		}
	}
	
	@Test
	public void testCollectionLinks() {
		ClientResponse response = webResource.path("/fundtransfers").accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());

		RepresentationFactory representationFactory = new RepresentationFactory();
		ReadableRepresentation resource = representationFactory.readRepresentation(new InputStreamReader(response.getEntityInputStream()));

		// the links from the collection
		List<Link> links = resource.getLinks();
		assertEquals(2, links.size());
		for (Link link : links) {
			if (link.getRel().equals("self")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/fundtransfers", link.getHref());
			} else if (link.getName().get().equals("FundsTransfer.initial>FundsTransfer.new")) {
				assertEquals("POST " + Configuration.TEST_ENDPOINT_URI + "/fundtransfers/new", link.getHref());
			} else {
				fail("unexpected link [" + link.getName().get() + "]");
			}
		}
		
		// the links on each item
		Collection<ReadableRepresentation> subresources = resource.getResources().values();
		assertNotNull(subresources);
		for (ReadableRepresentation item : subresources) {
			List<Link> itemLinks = item.getLinks();
			assertEquals(1, itemLinks.size());
			for (Link link : itemLinks) {
				if (link.getRel().contains("self")) {
					assertEquals(Configuration.TEST_ENDPOINT_URI + "/fundtransfers/" + item.getProperties().get("id").get(), link.getHref());
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
	 * Attempt a PUT to the resource (entity resource)
	 */
	@Test
	public void putFundTransferHalXML() throws Exception {
		double d = Math.random() * 10000000;
		String id = Integer.toString((int) d);
		String resourceUri = "/fundtransfers/" + id;
		String halRequest = buildHalResource(resourceUri, id).renderContent(RepresentationFactory.HAL_XML);

		ClientResponse response = webResource.path(resourceUri).accept(MediaType.APPLICATION_HAL_XML).type(MediaType.APPLICATION_HAL_XML).put(ClientResponse.class, halRequest);
        assertEquals(200, response.getStatus());

		response = webResource.path(resourceUri).accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());
	}

	/**
	 * Attempt a PUT to the resource (entity resource)
	 */
	@Test
	public void putFundTransferHalJSON() throws Exception {
		double d = Math.random() * 10000000;
		String id = Integer.toString((int) d);
		String resourceUri = "/fundtransfers/" + id;
		String halRequest = buildHalResource(resourceUri, id).renderContent(RepresentationFactory.HAL_JSON);

		ClientResponse response = webResource.path(resourceUri).accept(MediaType.APPLICATION_HAL_JSON).type(MediaType.APPLICATION_HAL_JSON).put(ClientResponse.class, halRequest);
        assertEquals(200, response.getStatus());

		response = webResource.path("/fundtransfers/" + id).accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());
	}
	
	private Representation buildHalResource(String resourceUri, String id) {
		RepresentationFactory representationFactory = new RepresentationFactory(Configuration.TEST_ENDPOINT_URI);
		// we have to prepend ~ to get the HalBuilder to work properly at the moment
		Representation r = representationFactory.newRepresentation("~" + resourceUri);
		r.withProperty("id", id);
		r.withProperty("body", "Funds tranfer issued at 01/01/2012");
		return r;
	}

	/**
	 * Attempt a PUT to the collection resource (which only accepts post in this example)
	 */
	@Test
	public void putMethodNotAllowed() throws Exception {
		RepresentationFactory representationFactory = new RepresentationFactory("https://example.com/");
		ReadableRepresentation r = representationFactory.newRepresentation("~/xyz/123");
		String halRequest = r.renderContent(RepresentationFactory.HAL_XML);
		
		// attempt to put to the notes collection, rather than an individual
		ClientResponse response = webResource.path("/fundtransfers").type(MediaType.APPLICATION_HAL_XML).put(ClientResponse.class, halRequest);
        assertEquals(405, response.getStatus());

        assertEquals(3, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

}
