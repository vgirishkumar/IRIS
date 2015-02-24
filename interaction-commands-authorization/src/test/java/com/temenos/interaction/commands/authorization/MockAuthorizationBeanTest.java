package com.temenos.interaction.commands.authorization;

/*
 * Base class for the authorization bean tests.
 */

/* 
 * #%L
 * interaction-commands-authorization
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.commands.authorization.RowFilter.Relation;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;

public class MockAuthorizationBeanTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test valid parameters are remembered.
	 */
	@Test
	public void testParameters() {

		// Create the bean
		MockAuthorizationBean bean = new MockAuthorizationBean("field1 eq value1 and field2 eq value2",
				"select1, select2");

		// Create a minimal context
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		// Check that the expected parameter is present
		assertEquals(2, bean.getFilters(ctx).size());
		assertEquals("field1", bean.getFilters(ctx).get(0).getFieldName().getName());
		assertEquals(Relation.EQ, bean.getFilters(ctx).get(0).getRelation());
		assertEquals("value1", bean.getFilters(ctx).get(0).getValue());
		assertEquals("field2", bean.getFilters(ctx).get(1).getFieldName().getName());
		assertEquals(Relation.EQ, bean.getFilters(ctx).get(1).getRelation());
		assertEquals("value2", bean.getFilters(ctx).get(1).getValue());

		assertEquals(2, bean.getSelect(ctx).size());
		assertTrue(bean.getSelect(ctx).contains(new FieldName("select1")));
		assertTrue(bean.getSelect(ctx).contains(new FieldName("select2")));
	}

	/**
	 * Test null parameters.
	 */
	@Test
	public void testNullParameters() {

		// Create the bean
		MockAuthorizationBean bean = new MockAuthorizationBean(null, null);

		// Create a minimal context
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		assertEquals(null, bean.getFilters(ctx));
		assertEquals(null, bean.getSelect(ctx));
	}
	
	/**
	 * Test empty parameters.
	 */
	@Test
	public void testEmptyParameters() {

		// Create the bean
		MockAuthorizationBean bean = new MockAuthorizationBean("", "");

		// Create a minimal context
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		
		assertTrue(null, bean.getFilters(ctx).isEmpty());
		assertTrue(null, bean.getSelect(ctx).isEmpty());
	}

	/**
	 * Test parameters that won't parse.
	 */
	@Test
	public void testBadParameters() {

		// Create the bean
		MockAuthorizationBean bean = new MockAuthorizationBean("filter", "select");
		
		// Create a minimal context
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		
		// Should have created the 'no results' filter
		assertEquals(null, bean.getFilters(ctx));
	}

	/**
	 * Test passed in parameters override construction time parameters.
	 */
	@Test
	public void testPassedParameters() {

		// Create the bean
		MockAuthorizationBean bean = new MockAuthorizationBean("badField eq badValue", "badSelect");

		// Create parameter list with different filter and select parameters.
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(MockAuthorizationBean.TEST_FILTER_KEY, "field1 eq value1 and field2 eq value2");
		queryParams.add(MockAuthorizationBean.TEST_SELECT_KEY, "goodSelect");

		// Create a minimal context
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		// Check that the expected parameter is present
		assertEquals(2, bean.getFilters(ctx).size());
		assertEquals("field1", bean.getFilters(ctx).get(0).getFieldName().getName());
		assertEquals(Relation.EQ, bean.getFilters(ctx).get(0).getRelation());
		assertEquals("value1", bean.getFilters(ctx).get(0).getValue());
		assertEquals("field2", bean.getFilters(ctx).get(1).getFieldName().getName());
		assertEquals(Relation.EQ, bean.getFilters(ctx).get(1).getRelation());
		assertEquals("value2", bean.getFilters(ctx).get(1).getValue());

		assertEquals(1, bean.getSelect(ctx).size());
		assertTrue(bean.getSelect(ctx).contains(new FieldName("goodSelect")));
	}
}
