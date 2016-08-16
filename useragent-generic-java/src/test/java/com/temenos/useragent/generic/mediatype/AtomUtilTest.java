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

import java.net.MalformedURLException;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.junit.Test;

public class AtomUtilTest {

	@Test
	public void testExtractRel() {
		assertEquals("", AtomUtil.extractRel(""));
		assertEquals("foo", AtomUtil.extractRel("foo"));
		assertEquals("bar", AtomUtil.extractRel("foo bar"));
		assertEquals("foo", AtomUtil.extractRel("foo "));
	}

	@Test
	public void testExtractDescription() {
		assertEquals("", AtomUtil.extractDescription(""));
		assertEquals("foo", AtomUtil.extractDescription("foo bar"));
		assertEquals("", AtomUtil.extractDescription("foo"));
		assertEquals("foo", AtomUtil.extractDescription(" foo bar "));
		assertEquals("foo", AtomUtil.extractDescription(" foo bar whatever"));
	}

	@Test
	public void testGetBaseUrl() {
		Entry newEntry = new Abdera().newEntry();
		newEntry.setBaseUri(new IRI("http://myserver:8080/myservice/Test.svc"));
		assertEquals("http://myserver:8080/myservice/Test.svc",
				AtomUtil.getBaseUrl(newEntry));
	}

	@Test
	public void testGetBaseUrlForInvalidValue() {
		Entry newEntry = new Abdera().newEntry();
		// illegal argument exception
		newEntry.setBaseUri(new IRI("foo"));
		try {
			AtomUtil.getBaseUrl(newEntry);
			fail("IllegalArgumentException should have thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		// malformed url exception
		newEntry.setBaseUri(new IRI("foo://test:8080/bar"));
		try {
			AtomUtil.getBaseUrl(newEntry);
			fail("IllegalArgumentException should have thrown");
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof MalformedURLException);
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testBuildXmlDocumentForNullContent() {
		AtomUtil.buildXmlDocument(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testBuildXmlDocumentForEmptyContent() {
		AtomUtil.buildXmlDocument("");
	}

	@Test(expected = IllegalStateException.class)
	public void testBuildXmlDocumentForInvalidContent() {
		AtomUtil.buildXmlDocument("foo");
	}
}
