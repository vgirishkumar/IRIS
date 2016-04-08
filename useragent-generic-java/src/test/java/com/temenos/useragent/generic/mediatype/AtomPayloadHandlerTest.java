package com.temenos.useragent.generic.mediatype;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.temenos.useragent.generic.Entity;
import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.internal.EntityWrapper;
import com.temenos.useragent.generic.mediatype.AtomPayloadHandler;

public class AtomPayloadHandlerTest {

	private AtomPayloadHandler handler = new AtomPayloadHandler();

	@Test
	public void testIsCollectionForTrue() throws Exception {
		handler.setPayload(IOUtils.toString(AtomPayloadHandler.class
				.getResourceAsStream("/atom_feed_with_single_entry.txt")));
		assertTrue(handler.isCollection());
	}

	@Test
	public void testIsCollectionForFalse() throws Exception {
		handler.setPayload(IOUtils.toString(AtomPayloadHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt")));
		assertFalse(handler.isCollection());
	}

	@Test
	public void testSetPayloadForNull() {
		try {
			handler.setPayload(null);
			fail("Should have thrown IllegalArgumentException");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testSetPayloadForInvalidXmlContent() {
		try {
			handler
					.setPayload("<some><valid><xml><but><invalid><atom-xml>foo</atom-xml></invalid></but></xml></valid></some>");
			fail("Should have thrown IllegalArgumentException");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testSetPayloadForInvalidTextContent() {
		try {
			handler.setPayload("foo");
			fail("Should have thrown IllegalArgumentException");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testSetPayloadForValidFeed() throws Exception {
		handler.setPayload(IOUtils.toString(AtomPayloadHandler.class
				.getResourceAsStream("/atom_feed_with_single_entry.txt")));
		assertTrue(handler.isCollection());
	}

	@Test
	public void testGetLinks() throws Exception {
		handler.setPayload(IOUtils.toString(AtomPayloadHandler.class
				.getResourceAsStream("/atom_feed_with_single_entry.txt")));
		List<Link> links = handler.links();
		assertEquals(2, links.size());

		// first 'self' link
		Link firstLink = links.get(0);
		assertEquals("self", firstLink.rel());
		assertEquals("Customers()", firstLink.href());
		assertEquals("Customers", firstLink.title());
		assertEquals("", firstLink.description());
		assertFalse(firstLink.hasEmbeddedPayload());

		// second 'new' link
		Link secondLink = links.get(1);
		assertEquals("http://mybank/rels/new", secondLink.rel());
		assertEquals("Customers()/new", secondLink.href());
		assertEquals("create new deal", secondLink.title());
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Customer", secondLink.description());
		assertFalse(secondLink.hasEmbeddedPayload());
	}

	@Test
	public void testEntities() throws Exception {
		handler.setPayload(IOUtils.toString(AtomPayloadHandler.class
				.getResourceAsStream("/atom_feed_with_single_entry.txt")));
		List<EntityWrapper> entities = handler.entities();
		assertEquals(1, entities.size());
		Entity entity = entities.get(0);
//		assertEquals(4, entity.links().all().size());
	}
}
