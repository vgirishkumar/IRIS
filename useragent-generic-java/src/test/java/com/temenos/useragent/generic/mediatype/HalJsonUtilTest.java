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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.junit.Test;

import com.temenos.useragent.generic.Link;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.Representation;

public class HalJsonUtilTest {

	@Test
	public void testExtractLinksForRepresentationWithNoLinks() {
		try {
			HalJsonUtil.extractLinks(null);
			fail("IllegalArgumentException should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		ReadableRepresentation representation = HalJsonUtil
				.initRepresentationFactory().newRepresentation();
		List<Link> links = HalJsonUtil.extractLinks(representation);
		assertTrue(links.isEmpty());
	}

	@Test
	public void testExtractLinksForRepresentationWithLinks() {
		Representation mockRepresentation = mock(Representation.class);
		com.theoryinpractise.halbuilder.api.Link mockLink = mock(com.theoryinpractise.halbuilder.api.Link.class);
		when(mockLink.getHref()).thenReturn("mock_link_href");
		when(mockLink.getRel()).thenReturn("mock_link_rel");
		when(mockLink.getTitle()).thenReturn("mock_link_title");
		List<com.theoryinpractise.halbuilder.api.Link> mockLinks = new ArrayList<com.theoryinpractise.halbuilder.api.Link>();
		mockLinks.add(mockLink);
		when(mockRepresentation.getLinks()).thenReturn(mockLinks);

		List<Link> links = HalJsonUtil.extractLinks(mockRepresentation);
		assertEquals(1, links.size());
		assertEquals("mock_link_title", links.get(0).title());
		assertEquals("mock_link_rel", links.get(0).rel());
		assertEquals("mock_link_href", links.get(0).href());
		assertEquals("", links.get(0).baseUrl());
	}

	@Test
	public void testCloneLastChildWithEmptyArray() {
		JSONArray jsonArr = new JSONArray();
		JSONArray clonedArr = HalJsonUtil.cloneLastChild(jsonArr);
		assertEquals(0, clonedArr.length());
	}

	@Test
	public void testCloneLastSimpleJsonObjectChild() {
		// original array [ { "name" : "peter" } ]
		JSONArray originalArr = new JSONArray("[ {\"name\" : \"peter\"} ]");
		// expected array [ { "name" : "peter" }, { } ]
		JSONArray expectedArr = new JSONArray("[ {\"name\" : \"peter\"}, { } ]");
		JSONArray actualArr = HalJsonUtil.cloneLastChild(originalArr);
		assertTrue("Expected: " + expectedArr + " but actual " + actualArr,
				expectedArr.similar(actualArr));
	}

	@Test
	public void testCloneLastSimpleJsonArrayChild() {
		// original array [ [ { "name" : "peter" } ] ]
		JSONArray originalArr = new JSONArray("[ [ {\"name\" : \"peter\"} ] ]");
		// expected array [ [ { "name" : "peter" } ], [ { } ] ]
		JSONArray expectedArr = new JSONArray(
				"[ [ {\"name\" : \"peter\"} ], [ { } ] ]");
		JSONArray actualArr = HalJsonUtil.cloneLastChild(originalArr);
		assertTrue("Expected: " + expectedArr + " but actual " + actualArr,
				expectedArr.similar(actualArr));
	}

	@Test
	public void testCloneLastNestedJsonObjectChild() {
		// original array [ { "candidates" : [ {"name" : "peter"} ] } ]
		JSONArray originalArr = new JSONArray(
				"[ { \"candidates\" : [ {\"name\" : \"peter\"} ] } ]");
		// expected array [ { "candidates" : [ {"name" : "peter"} ] }, {
		// "candidates" : [ { } ] } ]
		JSONArray expectedArr = new JSONArray(
				"[ { \"candidates\" : [ {\"name\" : \"peter\"} ] }, { \"candidates\" : [ { } ] } ]");
		JSONArray actualArr = HalJsonUtil.cloneLastChild(originalArr);
		assertTrue("Expected: " + expectedArr + " but actual " + actualArr,
				expectedArr.similar(actualArr));
	}

	@Test
	public void testCloneLastNestedMixedChildren() {
		// original array [ { "job" : "developer" }, { "candidates" : [ {"name"
		// : "peter"} ] } ]
		JSONArray originalArr = new JSONArray(
				"[ { \"job\" : \"developer\" }, { \"candidates\" : [ {\"name\" : \"peter\"} ] } ]");
		// expected array [ { "job" : "developer" }, { "candidates" : [ {"name" : "peter"} ] }, {
		// "candidates" : [ { } ] } ]
		JSONArray expectedArr = new JSONArray(
				"[ { \"job\" : \"developer\" }, { \"candidates\" : [ {\"name\" : \"peter\"} ] }, { \"candidates\" : [ { } ] } ]");
		JSONArray actualArr = HalJsonUtil.cloneLastChild(originalArr);
		assertTrue("Expected: " + expectedArr + " but actual " + actualArr,
				expectedArr.similar(actualArr));
	}
}
