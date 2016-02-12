package com.temenos.interaction.authorization.mock;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.odataext.odataparser.ODataParser;
import com.temenos.interaction.odataext.odataparser.data.AccessProfile;
import com.temenos.interaction.odataext.odataparser.data.FieldName;
import com.temenos.interaction.odataext.odataparser.data.RowFilters;

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
        String expectedFilters = "field1 eq value1 and field2 eq value2";

        // Create the bean
        MockAuthorizationBean bean = new MockAuthorizationBean(expectedFilters, "select1, select2");

        // Create a minimal context
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
        MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
        InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
                queryParams, mock(ResourceState.class), mock(Metadata.class));

        boolean threw = false;
        RowFilters filters = null;
        try {
            filters = bean.getFilters(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        // Check that the expected parameter is present
        assertEquals(expectedFilters, ODataParser.toFilters(filters));

        Set<FieldName> selects = null;
        try {
            selects = bean.getSelect(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        assertEquals(2, selects.size());
        assertTrue(selects.contains(new FieldName("select1")));
        assertTrue(selects.contains(new FieldName("select2")));
    }

    /**
     * Test throws if asked to.
     */
    @Test
    public void testThrows() {

        // Create the bean
        MockAuthorizationBean bean = new MockAuthorizationBean(new InteractionException(Status.UNAUTHORIZED,
                "Test exception"));

        // Create a minimal context
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
        MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
        InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
                queryParams, mock(ResourceState.class), mock(Metadata.class));

        boolean threw = false;
        RowFilters filters = null;
        try {
            filters = bean.getFilters(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertTrue(threw);

        threw = false;

        Set<FieldName> selects = null;
        try {
            selects = bean.getSelect(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertTrue(threw);

        AccessProfile profile = null;
        try {
            profile = bean.getAccessProfile(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertTrue(threw);
    }

    /**
     * Test valid parameters are returned in AccessProfile.
     */
    @Test
    public void testAccessProfile() {

        // Create the bean
        String expectedFilters = "field1 eq value1 and field2 eq value2";
        MockAuthorizationBean bean = new MockAuthorizationBean(expectedFilters,
                "select1, select2");

        // Create a minimal context
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
        MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
        InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
                queryParams, mock(ResourceState.class), mock(Metadata.class));

        // Check that the expected parameters are present
        boolean threw = false;
        AccessProfile profile = null;
        try {
            profile = bean.getAccessProfile(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        assertEquals(expectedFilters, ODataParser.toFilters(profile.getNewRowFilters()));
        
        assertEquals(2, profile.getFieldNames().size());
        assertTrue(profile.getFieldNames().contains(new FieldName("select1")));
        assertTrue(profile.getFieldNames().contains(new FieldName("select2")));
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

        boolean threw = false;
        RowFilters filters = null;
        try {
            filters = bean.getFilters(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        Set<FieldName> selects = null;
        try {
            selects = bean.getSelect(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        assertEquals(null, filters);
        assertEquals(null, selects);
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

        boolean threw = false;
        RowFilters filters = null;
        try {
            filters = bean.getFilters(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        Set<FieldName> selects = null;
        try {
            selects = bean.getSelect(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        assertTrue(null, filters.isEmpty());
        assertTrue(null, selects.isEmpty());
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
        boolean threw = false;
        RowFilters filters = null;
        try {
            filters = bean.getFilters(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        assertEquals(null, filters);
    }

    /**
     * Test passed in parameters override construction time parameters.
     */
    @Test
    public void testPassedParameters() {

        // Create the bean
        MockAuthorizationBean bean = new MockAuthorizationBean("badField eq badValue", "badSelect");

        // Create parameter list with different filter and select parameters.
        String expectedFilters = "field1 eq value1 and field2 eq value2";
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
        queryParams.add(MockAuthorizationBean.TEST_FILTER_KEY, expectedFilters);
        queryParams.add(MockAuthorizationBean.TEST_SELECT_KEY, "goodSelect");

        // Create a minimal context
        MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
        InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
                queryParams, mock(ResourceState.class), mock(Metadata.class));

        // Check that the expected parameter is present
        boolean threw = false;
        RowFilters filters = null;
        try {
            filters = bean.getFilters(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        assertEquals(expectedFilters, ODataParser.toFilters(filters));

        Set<FieldName> selects = null;
        try {
            selects = bean.getSelect(ctx);
        } catch (Exception e) {
            threw = true;
        }
        assertFalse(threw);

        assertEquals(1, selects.size());
        assertTrue(selects.contains(new FieldName("goodSelect")));
    }
}
