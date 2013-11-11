package com.temenos.interaction.example.odata.notes;

/*
 * #%L
 * interaction-example-odata-notes
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


import static org.junit.Assert.*;

import java.io.InputStream;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.junit.Test;

public class ODataValidationITCase {

	/**
	 * An <atom:link> element with a rel="self" attribute MUST contain an href 
	 * attribute with a value equal to the URI used to identify the set that the 
	 * parent <atom:feed> element represents. [Specification] [Section: 2.2.6.2.1]
	 */
	@Test
	public void validateFeedSelfLink() throws Exception {
		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);

		ClientResponse response = client.get(Configuration.TEST_ENDPOINT_URI + "/Persons");
		InputStream in = response.getInputStream();

		Parser parser = abdera.getParser();
		Document<Feed> doc = parser.parse(in);
		Feed feed = doc.getRoot();
		
		assertEquals("Persons", feed.getSelfLink().getHref().getASCIIPath());
	}

	/**
	 * An <atom:link> element with a rel="self" attribute MUST contain an href 
	 * attribute with a value equal to the URI used to identify the set that the 
	 * parent <atom:feed> element represents. [Specification] [Section: 2.2.6.2.1]
	 */
	@Test
	public void validateNavPropertyFeedSelfLink() throws Exception {
		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);

		ClientResponse response = client.get(Configuration.TEST_ENDPOINT_URI + "/Persons(1)/PersonNotes");
		InputStream in = response.getInputStream();

		Parser parser = abdera.getParser();
		Document<Feed> doc = parser.parse(in);
		Feed feed = doc.getRoot();
		
		assertEquals("Persons(1)/PersonNotes", feed.getSelfLink().getHref().getPath());
	}

	/**
	 * An <atom:link> element with a rel="self" attribute MUST contain an href 
	 * attribute with a value equal to the URI used to identify the set that the 
	 * parent <atom:feed> element represents. [Specification] [Section: 2.2.6.2.1]
	 */
	@Test
	public void validateFeedNavPropertyRel() throws Exception {
		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);

		ClientResponse response = client.get(Configuration.TEST_ENDPOINT_URI + "/Persons");
		InputStream in = response.getInputStream();

		Parser parser = abdera.getParser();
		Document<Feed> doc = parser.parse(in);
		Feed feed = doc.getRoot();
		String navPropertyRel = null;
		for (Entry entry : feed.getEntries()) {
			for (Link l : entry.getLinks()) {
				if (l.getHref().getPath().contains("Persons(1)/PersonNotes")) {
					navPropertyRel = l.getRel();
				}
			}
		}		
		assertNotNull("Found link rel for nav property", navPropertyRel);
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Notes", navPropertyRel);
	}

}
