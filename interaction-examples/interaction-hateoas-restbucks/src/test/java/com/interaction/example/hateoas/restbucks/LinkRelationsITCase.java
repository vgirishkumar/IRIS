package com.interaction.example.hateoas.restbucks;

/*
 * #%L
 * interaction-example-odata-airline
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.temenos.interaction.media.hal.MediaType;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

/**
 * Test RESTful following of links provided through hypermedia.
 */
public class LinkRelationsITCase extends JerseyTest {

	private RepresentationFactory representationFactory;
	
	public LinkRelationsITCase() throws Exception {
		super();
	}
	
	@Before
	public void initTest() {
		// -DTEST_ENDPOINT_URI={someurl} to test with external server 
    	webResource = Client.create().resource(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)); 
		representationFactory = new StandardRepresentationFactory();
	}

	@After
	public void tearDown() {}

	@Test
	public void testEncodedQueryParameterPassing() throws Exception {
		ReadableRepresentation resource = get(webResource.path("/123456/history")
				.queryParam("query", "test%40abc.com")
				.queryParam("email", "test%40abc.com"));
		Link orders = resource.getLinkByRel("collection");
		assertNotNull("history of 'orders' link relation", orders);
		assertEquals("http://localhost:8080/example/interaction-hateoas-restbucks.svc/123456/Orders()?query=email+eq+'test%40abc.com'&email=test%40abc.com", orders.getHref());
	}

	@Test
	public void testOrderHistory() throws Exception {
	    // request root
		ReadableRepresentation rootResource = get(webResource.path("/123456/shop"));
	    
		// POST order
		Link order = rootResource.getLinkByRel("http://relations.restbucks.com/order");
		assertNotNull("'order' link relation", order);
		UUID id = UUID.randomUUID();
		Representation orderRequest = buildOrderRequest(id.toString());
		ReadableRepresentation orderResource = post(webResource.uri(new URI(order.getHref())), orderRequest.toString(MediaType.APPLICATION_HAL_JSON));

		// use the link on the order
		Link history = orderResource.getLinkByRel("http://relations.restbucks.com/history");
		assertNotNull(history);
		webResource = webResource.uri(new URI(history.getHref()));
		ReadableRepresentation historyResource1 = get(webResource);

		// get the orders
		Link orders1 = historyResource1.getLinkByRel("collection");
		ReadableRepresentation historyOrders1 = get(webResource.uri(new URI(orders1.getHref())));
		assertEquals(1, historyOrders1.getResourceMap().size());

		// use the 'history' link on root to find all orders with same email address
		Link rootHistory = rootResource.getLinkByRel("http://relations.restbucks.com/history");
		assertNotNull(rootHistory);
		webResource = webResource.uri(new URI(rootHistory.getHref())).queryParam("email", URLEncoder.encode(id.toString()+"+test@somewhere.com", "UTF-8"));
		ReadableRepresentation historyResource2 = get(webResource);

		// get the orders
		Link orders2 = historyResource2.getLinkByRel("collection");
		ReadableRepresentation historyOrders2 = get(webResource.uri(new URI(orders2.getHref())));
		assertEquals(1, historyOrders2.getResourceMap().size());
	}

	@Test
	public void testOrderStatus() throws Exception {
		// RB1000 already exists, inserted during test initialisation
		ReadableRepresentation orderResource = get(webResource.path("/123456/Orders('RB1000')"));

		// check the availability of the link to make the payment
		// should be paid and therefore no link to pay
		Link payment = orderResource.getLinkByRel("http://relations.restbucks.com/payment");
		assertNull("'payment' link relation", payment);

		// should be a link to the payment
		List<Link> links = orderResource.getLinks();
		boolean found = false;
		for (Link link : links) {
			if (link.getHref().equals("http://localhost:8080/example/interaction-hateoas-restbucks.svc/123456/Orders(RB1000)/payment")) {
				found = true;
			}
		}
		assertTrue("Payment link", found);
	}

	@Test
	public void testCupOfCoffee() throws Exception {
	    
	    /*
	     * HypermediaClient client = new HypermediaClient(httpClient);
	     * client.register(hal)
	     * 		 .register(odata);
	     * client.get(new URL("/api"))
	     * 		.get("orders")
	     * 		.property("milk", "yes").property("name", "Aaron").property("quantity", 1)
	     * 		.post("self")
	     * 		.use()  // use the response in the next request payload
	     * 		.clear()  // clear the request payload
	     * 		.post("payment")
	     */
	    
	    // request root
		ReadableRepresentation rootResource = get(webResource.path("/123456/shop"));
	    
		// POST order
		Link order = rootResource.getLinkByRel("http://relations.restbucks.com/order");
		assertNotNull("'order' link relation", order);
		UUID id = UUID.randomUUID();
		Representation orderRequest = buildOrderRequest(id.toString());
		ReadableRepresentation orderResource = post(webResource.uri(new URI(order.getHref())), orderRequest.toString(MediaType.APPLICATION_HAL_JSON));
	    
		// check the availability of the link to make the payment
		Link payment = orderResource.getLinkByRel("http://relations.restbucks.com/payment");
		assertNotNull("'payment' link relation", payment);
		
		// make the payment
		Representation paymentRequest = buildPaymentRequest(orderResource.getValue("Id").toString());
		ReadableRepresentation paymentResource = post(webResource.uri(new URI(payment.getHref())), paymentRequest.toString(MediaType.APPLICATION_HAL_JSON));
		assertNotNull(paymentResource);
		
		// confirm the payment was successful, no payment possible
		ReadableRepresentation updatedOrderResource = get(webResource.uri(new URI(order.getHref())));
		assertNull(updatedOrderResource.getLinkByRel("http://relations.restbucks.com/payment"));

	}

	private ReadableRepresentation get(WebResource resource) throws Exception {
        ReadableRepresentation representation;
        Reader reader = null;
        ClientResponse response = null;
		try {
			response = resource
					.accept(MediaType.APPLICATION_HAL_JSON)
					.get(ClientResponse.class);
			assertEquals(Status.Family.SUCCESSFUL, response.getClientResponseStatus().getFamily());
			reader = new InputStreamReader(response.getEntityInputStream());
			representation = representationFactory.readRepresentation(MediaType.APPLICATION_HAL_JSON.toString(),reader);
		} finally {
			if (reader != null)
				reader.close();
			if (response != null)
				response.close();
		}
		return representation;
	}

	private ReadableRepresentation post(WebResource resource, String payload) throws Exception {
        ReadableRepresentation representation;
        Reader reader = null;
        ClientResponse response = null;
		try {
			response = resource
					.accept(MediaType.APPLICATION_HAL_JSON)
					.type(MediaType.APPLICATION_HAL_JSON)
					.post(ClientResponse.class, payload);
			assertEquals(Status.Family.SUCCESSFUL, response.getClientResponseStatus().getFamily());
			reader = new InputStreamReader(response.getEntityInputStream());
			representation = representationFactory.readRepresentation(MediaType.APPLICATION_HAL_JSON.toString(),reader);
		} finally {
			if (reader != null)
				reader.close();
			if (response != null)
				response.close();
		}
		return representation;
	}

    private Representation buildOrderRequest(String id) {
        return representationFactory.newRepresentation()
                .withProperty("Id", id)
                .withProperty("milk", "yes")
                .withProperty("name", "Aaron")
                .withProperty("email", id+"+test@somewhere.com")   // keep the + character in the email address to test url encoding
                .withProperty("quantity", 1);
    }
    
    private Representation buildPaymentRequest(String orderId) {
        return representationFactory.newRepresentation()
                .withProperty("orderId", orderId)
                .withProperty("authorisationCode", "authorised");
    }
    
}
