package com.temenos.interaction.test.atom;

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

import com.temenos.interaction.test.Link;
import com.temenos.interaction.test.mediatype.AtomEntryHandler;

public class AtomEntryHandlerTest {

	private AtomEntryHandler transformer = new AtomEntryHandler();

	@Test
	public void testGetLinks() {
		transformer.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		List<Link> links = transformer.getLinks();
		assertEquals(4, links.size());

		// first 'self' link
		Link firstLink = links.get(0);
		assertEquals("self", firstLink.rel());
		assertEquals("Customers('100974')", firstLink.href());
		assertFalse(firstLink.hasEmbeddedPayload());

		// second 'see' link
		Link secondLink = links.get(1);
		assertEquals("http://mybank/rels/see", secondLink.rel());
		assertEquals("Customers('100974')/see", secondLink.href());
		assertFalse(secondLink.hasEmbeddedPayload());

		// third 'see' link
		Link thirdLink = links.get(2);
		assertEquals("http://mybank/rels/input", thirdLink.rel());
		assertEquals("Customers('100974')/input", thirdLink.href());
		assertFalse(thirdLink.hasEmbeddedPayload());

		// fourth 'see' link
		Link fourthLink = links.get(3);
		assertEquals("http://mybank/rels/review", fourthLink.rel());
		assertEquals("Customers('100974')/review", fourthLink.href());
		assertFalse(fourthLink.hasEmbeddedPayload());
	}

	@Test
	public void testGetId() {
		transformer.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals("100974", transformer.getId());
	}

	@Test
	public void testGetCount() {
		transformer.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals(1, transformer.getCount("GivenNames"));
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
