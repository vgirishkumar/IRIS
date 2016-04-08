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

import org.junit.Test;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.mediatype.AtomEntryHandler;

public class AtomEntryHandlerTest {

	private AtomEntryHandler handler = new AtomEntryHandler();

	@Test
	public void testGetLinks() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		List<Link> links = handler.getLinks();
		assertEquals(4, links.size());

		// first 'self' link
		Link firstLink = links.get(0);
		assertEquals("self", firstLink.rel());
		assertEquals("Customers('100974')", firstLink.href());
		assertEquals("Customer",firstLink.title());
		assertEquals("",firstLink.description());
		assertFalse(firstLink.hasEmbeddedPayload());

		// second 'see' link
		Link secondLink = links.get(1);
		assertEquals("http://mybank/rels/see", secondLink.rel());
		assertEquals("Customers('100974')/see", secondLink.href());
		assertEquals("see record",secondLink.title());
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Customer",secondLink.description());
		assertFalse(secondLink.hasEmbeddedPayload());

		// third 'see' link
		Link thirdLink = links.get(2);
		assertEquals("http://mybank/rels/input", thirdLink.rel());
		assertEquals("Customers('100974')/input", thirdLink.href());
		assertEquals("input deal",thirdLink.title());
		assertEquals("",thirdLink.description());
		assertFalse(thirdLink.hasEmbeddedPayload());

		// fourth 'see' link
		Link fourthLink = links.get(3);
		assertEquals("http://mybank/rels/review", fourthLink.rel());
		assertEquals("Customers('100974')/review", fourthLink.href());
		assertEquals("audit deal", fourthLink.title());
		assertEquals("",fourthLink.description());
		assertFalse(fourthLink.hasEmbeddedPayload());
	}

	@Test
	public void testGetId() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals("100974", handler.getId());
	}

	@Test
	public void testGetCount() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals(1, handler.getCount("GivenNames"));
//		assertEquals(2, transformer.getCount("Customer_LegalIdGroup/LegalId"));
	}

//	@Test
//	public void testGetValue() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetContent() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetEntry() {
//		fail("Not yet implemented");
//	}

}
