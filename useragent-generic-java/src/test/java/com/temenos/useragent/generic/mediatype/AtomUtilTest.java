package com.temenos.useragent.generic.mediatype;

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
}
