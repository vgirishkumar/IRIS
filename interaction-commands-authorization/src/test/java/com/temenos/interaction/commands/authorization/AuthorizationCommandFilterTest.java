package com.temenos.interaction.commands.authorization;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.commands.authorization.AuthorizationCommand;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * The Class AuthorizationCommandTest.
 */
public class AuthorizationCommandFilterTest extends AbstractAuthorizationTest {

	/**
	 * Test no $filter parameter
	 */
	@Test
	public void testFilterNone() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean(null, null);
		AuthorizationCommand command = new AuthorizationCommand(child, authBean);

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
		InteractionContext finalCtx = child.getCtx();
		assertEquals(null ,finalCtx.getQueryParameters().getFirst(AuthorizationCommand.FILTER_KEY));
	}

	/**
	 * Test creation of $filter parameter
	 */
	@Test
	public void testFilterCreate() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean("name eq Tim", null);
		AuthorizationCommand command = new AuthorizationCommand(child, authBean);

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
		InteractionContext finalCtx = child.getCtx();
		assertEquals("name eq Tim", finalCtx.getQueryParameters().getFirst(AuthorizationCommand.FILTER_KEY));
	}

	/**
	 * Test addition of $filter parameter
	 */
	@Test
	public void testFilterAdd() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean("id eq 1234", null);
		AuthorizationCommand command = new AuthorizationCommand(child, authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(AuthorizationCommand.FILTER_KEY, "name eq Tim");

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
		InteractionContext finalCtx = child.getCtx();
		String resultStr = finalCtx.getQueryParameters().getFirst(AuthorizationCommand.FILTER_KEY);
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(resultStr.split("\\s* and \\s*")));
		assertTrue(result.contains("name eq Tim"));
		assertTrue(result.contains("id eq 1234"));
	}

	/**
	 * Test dangerous names containing keywords 'and', 'or' etc.
	 */
	@Test
	public void testFilterKeywords() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean("Landlord eq Thor", null);
		AuthorizationCommand command = new AuthorizationCommand(child, authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(AuthorizationCommand.FILTER_KEY, "origin eq andriod");

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
		InteractionContext finalCtx = child.getCtx();
		String resultStr = finalCtx.getQueryParameters().getFirst(AuthorizationCommand.FILTER_KEY);
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(resultStr.split("\\s* and \\s*")));
		assertTrue(result.contains("Landlord eq Thor"));
		assertTrue(result.contains("origin eq andriod"));
	}
}
