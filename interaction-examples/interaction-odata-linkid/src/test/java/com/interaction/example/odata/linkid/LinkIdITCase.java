package com.interaction.example.odata.linkid;

/*
 * #%L
 * interaction-example-odata-linkid
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
/**
 * TODO: Document me!
 *
 * @author mjangid
 *
 */
public class LinkIdITCase {

	private String baseUri = null;
	private HttpClient client;
	private GetMethod method = null;
	private final static String AIRPORTS = "Airports";


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		baseUri = ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI);
		client = new HttpClient();
	}

	@Test
	public void testLinkId() {
		try {
			method = new GetMethod(baseUri + AIRPORTS);
			client.executeMethod(method);
			assertEquals(200, method.getStatusCode());

			if (method.getStatusCode() == HttpStatus.SC_OK) {
				// read as string for debugging
				String response = method.getResponseBodyAsString();
				Abdera abdera = new Abdera();
				Parser parser = abdera.getParser();
				Document<Feed> feedEntry = parser.parse(new StringReader(response));
				Feed feed = feedEntry.getRoot();
				List<Entry> entries= feed.getEntries();
				assertEquals(3, entries.size());
				
				List<Link> link = entries.get(0).getLinks();
				assertNotNull(link);
				assertEquals("123", link.get(0).getAttributeValue("id"));
				
				link = entries.get(1).getLinks();
				assertNotNull(link);
				assertEquals("123", link.get(0).getAttributeValue("id"));
				
				link = entries.get(2).getLinks();
				assertNotNull(link);
				assertEquals("123", link.get(0).getAttributeValue("id"));
			}
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			method.releaseConnection();
		}		
	}

	@Test
	public void testEntryCount() {
		try {
			method = new GetMethod(baseUri + AIRPORTS);
			client.executeMethod(method);
			assertEquals(200, method.getStatusCode());

			if (method.getStatusCode() == HttpStatus.SC_OK) {
				// read as string for debugging
				String response = method.getResponseBodyAsString();
				Abdera abdera = new Abdera();
				Parser parser = abdera.getParser();
				Document<Feed> feedEntry = parser.parse(new StringReader(response));
				Feed feed = feedEntry.getRoot();
				List<Entry> entries= feed.getEntries();
				assertEquals(3, entries.size());
			}
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			method.releaseConnection();
		}
	}
}
