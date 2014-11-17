package com.interaction.example.hateoas.simple;

/*
 * #%L
 * interaction-example-hateoas-simple
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
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.temenos.interaction.media.hal.MediaType;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

/**
 * This test checks that queries are returned from cache when appropriate
 * 
 * @author amcguinness
 */
public class CacheITCase extends JerseyTest {

	public CacheITCase() throws Exception {
		super();
	}

	@Before
	public void initTest() {
		// -DTEST_ENDPOINT_URI={someurl} to test with external server
		webResource = Client.create().resource(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI));
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCollectionLinks() {
		ClientResponse response = webResource.path("/notes").accept(MediaType.APPLICATION_HAL_JSON)
				.get(ClientResponse.class);
		assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(response.getStatus())
				.getFamily());

		RepresentationFactory representationFactory = new StandardRepresentationFactory();
		ReadableRepresentation resource = representationFactory.readRepresentation(new InputStreamReader(response
				.getEntityInputStream()));

		// the items, and links on each item
		Collection<Map.Entry<String, ReadableRepresentation>> subresources = resource.getResources();
		assertNotNull(subresources);
		/*
		 * Test that there are actually some subresource returned. If the 'self'
		 * link rel in the HALProvider is broken then we won't get any
		 * subresources here.
		 */
		assertTrue(subresources.size() > 0);
		for (Map.Entry<String, ReadableRepresentation> entry : subresources) {
			ReadableRepresentation item = entry.getValue();
			List<Link> itemLinks = item.getLinks();
			assertEquals(2, itemLinks.size());
			for (Link link : itemLinks) {
				if (link.getRel().contains("self")) {
					assertEquals(Configuration.TEST_ENDPOINT_URI + "/notes/" + item.getProperties().get("noteID"),
							link.getHref());
				} else if (link.getName().contains("Note.deletedNote")) {
					assertEquals(Configuration.TEST_ENDPOINT_URI + "/notes/" + item.getProperties().get("noteID"),
							link.getHref());
				} else {
					fail("unexpected link [" + link.getName() + "]");
				}
			}
		}
		
		ClientResponse itemResponse = webResource.path("/notes/4").accept(MediaType.APPLICATION_HAL_JSON)
				.get(ClientResponse.class);
		assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(itemResponse.getStatus())
				.getFamily());
		String cacheHeader = itemResponse.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
		assertEquals("max-age=120", cacheHeader);
		
		long start = System.currentTimeMillis();
		ClientResponse repeatedResponse = webResource.path("/notes/4").accept(MediaType.APPLICATION_HAL_JSON)
				.get(ClientResponse.class);
		assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(repeatedResponse.getStatus())
				.getFamily());
		long finish = System.currentTimeMillis();
		assertTrue(finish < (1000 + start));
		System.out.println( "request took " + (finish-start) + "ms" );
	}

	/**
	 * Found a small issue where a GET to a non-existent resource still
	 * generated the links and this resulted in a server side error (500)
	 */
	@Test
	public void testGET404() {
		ClientResponse response = webResource.path("/notes/666").accept(MediaType.APPLICATION_HAL_JSON)
				.get(ClientResponse.class);
		assertEquals(404, response.getStatus());
	}

	/**
	 * Attempt a PUT to the notes collection resource (method not allowed)
	 */
	@Test
	public void putNoteToCollection() throws Exception {
		String halRequest = "{}";
		// attempt to put to the notes collection, rather than an individual
		ClientResponse response = webResource.path("/notes").type(MediaType.APPLICATION_HAL_JSON)
				.put(ClientResponse.class, halRequest);
		assertEquals(405, response.getStatus());
	}

	/**
	 * Attempt a PUT an invalid notes resource (a collection resource)
	 */
	@Test
	public void putNoteBadRequest() throws Exception {
		String halRequest = "{{";
		// attempt to put to the notes collection, rather than an individual
		ClientResponse response = webResource.path("/notes").type(MediaType.APPLICATION_HAL_JSON)
				.post(ClientResponse.class, halRequest);
		assertEquals(400, response.getStatus());
	}

}
