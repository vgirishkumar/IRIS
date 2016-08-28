package com.temenos.useragent.generic.internal;

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
