package com.temenos.useragent.generic.internal;

import static org.junit.Assert.*;

import org.junit.Test;

public class LinkImplTest {

	@Test
	public void testHref() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("foo('123')", builder.build().href());
	}

	@Test
	public void testHrefForEmpty() {
		LinkImpl.Builder builder = new LinkImpl.Builder("");
		assertEquals("", builder.build().href());

		builder = new LinkImpl.Builder(null);
		assertEquals("", builder.build().href());
	}

	@Test
	public void testHasEmbeddedPayload() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		builder.payload(null);
		assertFalse(builder.build().hasEmbeddedPayload());

		builder = new LinkImpl.Builder("foo('123')");
		builder.payload(new DefaultPayloadWrapper());
		assertTrue(builder.build().hasEmbeddedPayload());
	}

	@Test
	public void testEmbedded() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		builder.payload(null);
		assertNull(builder.build().embedded());

		builder = new LinkImpl.Builder("foo('123')");
		builder.payload(new DefaultPayloadWrapper());
		assertNotNull(builder.build().embedded());
	}

	@Test
	public void testRel() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("bar", builder.rel("bar").build().rel());
	}

	@Test
	public void testRelForEmpty() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("", builder.rel("").build().rel());

		builder = new LinkImpl.Builder("foo('123')");
		assertEquals("", builder.rel(null).build().rel());
	}

	@Test
	public void testBaseUrl() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("bar", builder.baseUrl("bar").build().baseUrl());

	}

	@Test
	public void testBaseUrlForEmpty() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("", builder.baseUrl("").build().baseUrl());

		builder = new LinkImpl.Builder("foo('123')");
		assertEquals("", builder.baseUrl(null).build().baseUrl());
	}

	@Test
	public void testTitle() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("bar", builder.title("bar").build().title());

	}

	@Test
	public void testTitleForEmpty() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("", builder.title("").build().title());

		builder = new LinkImpl.Builder("foo('123')");
		assertEquals("", builder.title(null).build().title());
	}

	@Test
	public void testId() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("bar", builder.id("bar").build().id());

	}

	@Test
	public void testIdForEmpty() {
		LinkImpl.Builder builder = new LinkImpl.Builder("foo('123')");
		assertEquals("", builder.id("").build().id());

		builder = new LinkImpl.Builder("foo('123')");
		assertEquals("", builder.id(null).build().id());
	}

}
