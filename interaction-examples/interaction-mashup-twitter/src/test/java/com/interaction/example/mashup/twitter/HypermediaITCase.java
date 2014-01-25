package com.interaction.example.mashup.twitter;

/*
 * #%L
 * interaction-example-mashup-twitter
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import com.temenos.interaction.media.hal.MediaType;
import com.temenos.interaction.example.mashup.twitter.OAuthRequestor;
import com.temenos.interaction.example.mashup.twitter.Twitter4JConsumer;
import com.theoryinpractise.halbuilder.RepresentationFactory;
import com.theoryinpractise.halbuilder.spi.Link;
import com.theoryinpractise.halbuilder.spi.ReadableRepresentation;

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


	@Test
	public void testGetEntryPointLinks() {
		ClientResponse response = webResource.path("/").accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());

		RepresentationFactory representationFactory = new RepresentationFactory();
		ReadableRepresentation resource = representationFactory.readRepresentation(new InputStreamReader(response.getEntityInputStream()));

		List<Link> links = resource.getLinks();
		assertEquals(2, links.size());
		for (Link link : links) {
			if (link.getRel().equals("self") && link.getName().get().equals("home.initial>GET>home.initial")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/", link.getHref());
			} else if (link.getName().get().equals("home.initial>GET>User.allUsers")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/users", link.getHref());
			} else {
				fail("unexpected link [" + link.getName().get() + "]");
			}
		}
	}
	
	@Test
	public void testUsers() {
		ClientResponse response = webResource.path("/users").accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());

		RepresentationFactory representationFactory = new RepresentationFactory();
		ReadableRepresentation resource = representationFactory.readRepresentation(new InputStreamReader(response.getEntityInputStream()));

		// the links from the collection
		List<Link> links = resource.getLinks();
		assertEquals(1, links.size());
		for (Link link : links) {
			if (link.getRel().equals("self")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/users", link.getHref());
			} else {
				fail("unexpected link [" + link.getName().get() + "]");
			}
		}
		
		// the items, and links on each item
		Collection<ReadableRepresentation> subresources = resource.getResources().values();
		assertNotNull(subresources);
		for (ReadableRepresentation item : subresources) {
			List<Link> itemLinks = item.getLinks();
			assertEquals(2, itemLinks.size());
			for (Link link : itemLinks) {
				if (link.getRel().contains("self")) {
					assertEquals(Configuration.TEST_ENDPOINT_URI + "/users/" + item.getProperties().get("userID").get(), link.getHref());
				} else if (link.getName().get().contains("User.allUsers>GET>Timeline.activity")) {
					assertEquals(Configuration.TEST_ENDPOINT_URI + "/tweets/" + item.getProperties().get("twitterHandle").get(), link.getHref());
				} else {
					fail("unexpected link [" + link.getName().get() + "]");
				}
			}
		}
	}

/**
 *  DISABLED as you need to have setup authentication to make this example work
 *  @see Twitter4JConsumer
 *  @see OAuthRequestor
 */
//	@Test
	public void testTweets() {
		ClientResponse response = webResource.path("/tweets/aphethean").accept(MediaType.APPLICATION_HAL_JSON).get(ClientResponse.class);
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus()).getFamily());

		RepresentationFactory representationFactory = new RepresentationFactory();
		ReadableRepresentation resource = representationFactory.readRepresentation(new InputStreamReader(response.getEntityInputStream()));

		// the links from the collection
		List<Link> links = resource.getLinks();
		assertEquals(1, links.size());
		for (Link link : links) {
			if (link.getRel().equals("self")) {
				assertEquals(Configuration.TEST_ENDPOINT_URI + "/tweets/aphethean", link.getHref());
			} else {
				fail("unexpected link [" + link.getName().get() + "]");
			}
		}
		
		// the items, and links on each item
		Collection<ReadableRepresentation> subresources = resource.getResources().values();
		assertNotNull(subresources);
		for (ReadableRepresentation item : subresources) {
			List<Link> itemLinks = item.getLinks();
			assertEquals(1, itemLinks.size());
			for (Link link : itemLinks) {
				if (link.getRel().contains("self")) {
					assertTrue(link.getHref().startsWith(Configuration.TEST_ENDPOINT_URI + "/tweets/aphethean"));
				} else {
					fail("unexpected link [" + link.getName().get() + "]");
				}
			}
			// Print the tweet
			System.out.println(item.get("message").get());
		}
	}

}
