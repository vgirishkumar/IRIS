package com.temenos.interaction.authorization.command;

/* 
 * #%L
 * interaction-commands-Authorization
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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.authorization.mock.MockAuthorizationBean;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.odataext.odataparser.ODataParser;

/**
 * The Class AuthorizationCommandTest.
 */
public class AuthorizationCommandFilterTest extends AbstractAuthorizationTest {

	/**
	 * Check that a forced internal error throws.
	 */
	@Test
	public void testInternalErrorThrows() {

		MockAuthorizationBean authBean = new MockAuthorizationBean(new InteractionException(Status.UNAUTHORIZED, "Test exception"));
		AuthorizationCommand command = new AuthorizationCommand(authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		// Run command
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		boolean threw = false;
		try {
			command.execute(ctx);
		} catch (InteractionException e) {
			threw = true;
		}
		
		// Should throw.
		assertTrue(threw);
	}
	
	
	/**
	 * Test creation of $filter parameter
	 */
	@Test
	public void testFilterCreate() {

		MockAuthorizationBean authBean = new MockAuthorizationBean("name eq Tim", "");
		AuthorizationCommand command = new AuthorizationCommand(authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		// Run command
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		try {
			InteractionCommand.Result result = command.execute(ctx);

			// Should work.
			assertEquals(Result.SUCCESS, result);
		} catch (InteractionException e) {
			// Should never throw.
			fail();
		}
		
		// Check that the expected parameter is present
		assertEquals("name eq Tim", ctx.getQueryParameters().getFirst(ODataParser.FILTER_KEY));
		
		// Check filtering has not yet been done
		assertEquals(Boolean.FALSE, (Boolean) ctx.getAttribute(AuthorizationAttributes.FILTER_DONE_ATTRIBUTE));	
	}

	/**
	 * Test addition of $filter parameter
	 */
	@Test
	public void testFilterAdd() {

		MockAuthorizationBean authBean = new MockAuthorizationBean("id eq 1234", "");
		AuthorizationCommand command = new AuthorizationCommand(authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(ODataParser.FILTER_KEY, "name eq Tim");

		// Run command
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		try {
			InteractionCommand.Result result = command.execute(ctx);

			// Should work.
			assertEquals(Result.SUCCESS, result);
		} catch (InteractionException e) {
			// Should never throw.
			fail();
		}
		// Check that the expected parameter is present
		String resultStr = ctx.getQueryParameters().getFirst(ODataParser.FILTER_KEY);
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(resultStr.split("\\s* and \\s*")));
		assertTrue(result.contains("name eq Tim"));
		assertTrue(result.contains("id eq 1234"));
	}
	
	/**
     * Test addition to the more complex $filter parameters handled by the new parser.
     */
    @Test
    public void testComplexFilterAdd() {

        MockAuthorizationBean authBean = new MockAuthorizationBean("id eq 1234", "");
        AuthorizationCommand command = new AuthorizationCommand(authBean);

        // Path is not important for security
        MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

        // Set up oData parameters
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
        queryParams.add(ODataParser.FILTER_KEY, "name eq Tim or value eq tolower('AVALUE')");

        // Run command
        InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
                queryParams, mock(ResourceState.class), mock(Metadata.class));
        try {
            InteractionCommand.Result result = command.execute(ctx);

            // Should work.
            assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            // Should never throw.
            fail();
        }
        // Check that the expected parameter is present
        String resultStr = ctx.getQueryParameters().getFirst(ODataParser.FILTER_KEY);
        ArrayList<String> result = new ArrayList<String>(Arrays.asList(resultStr.split("\\s* and \\s*")));
        assertTrue(result.contains("name eq Tim or value eq tolower('AVALUE')"));
        assertTrue(result.contains("id eq 1234"));
    }

	/**
	 * Test dangerous names containing keywords 'and', 'or' etc.
	 */
	@Test
	public void testFilterKeywords() {

		MockAuthorizationBean authBean = new MockAuthorizationBean("Landlord eq Thor", "");
		AuthorizationCommand command = new AuthorizationCommand(authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(ODataParser.FILTER_KEY, "origin eq andriod");

		// Run command
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		try {
			InteractionCommand.Result result = command.execute(ctx);

			// Should work.
			assertEquals(Result.SUCCESS, result);
		} catch (InteractionException e) {
			// Should never throw.
			fail();
		}
		// Check that the expected parameter is present
		String resultStr = ctx.getQueryParameters().getFirst(ODataParser.FILTER_KEY);
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(resultStr.split("\\s* and \\s*")));
		assertTrue(result.contains("Landlord eq Thor"));
		assertTrue(result.contains("origin eq andriod"));
	}
}
