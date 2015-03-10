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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.authorization.command.AuthorizationCommand;
import com.temenos.interaction.authorization.command.util.ODataParser;
import com.temenos.interaction.authorization.mock.MockAuthorizationBean;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * The Class AuthorizationCommandTest.
 */
public class AuthorizationCommandSelectTest extends AbstractAuthorizationTest {

	/**
	 * Test no $select parameter
	 */
	@Test
	public void testSelectNone() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean("", null);
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
		assertEquals(null, ctx.getQueryParameters().getFirst(ODataParser.SELECT_KEY));
	}

	/**
	 * Test creation of $select parameter
	 */
	@Test
	public void testSelectCreate() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean("", "id");
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
		assertEquals("id", ctx.getQueryParameters().getFirst(ODataParser.SELECT_KEY));
	}

	/**
	 * Test removal of a new $select parameter
	 */
	@Test
	public void testSelectRemoveNew() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean("", "name, id");
		AuthorizationCommand command = new AuthorizationCommand(child, authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(ODataParser.SELECT_KEY, "name");

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
		// Should just have name left
		assertEquals("name", ctx.getQueryParameters().getFirst(ODataParser.SELECT_KEY));
	}

	/**
	 * Test removal of an existing $select parameter
	 */
	@Test
	public void testSelectRemoveOld() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean("", "id");
		AuthorizationCommand command = new AuthorizationCommand(child, authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(ODataParser.SELECT_KEY, "name, id");

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
		// Should just have id left
		assertEquals("id", ctx.getQueryParameters().getFirst(ODataParser.SELECT_KEY));
	}

	/**
	 * Test union of two groups of $select parameters
	 */
	@Test
	public void testSelectUnion() {

		MockCommand child = new MockCommand();
		MockAuthorizationBean authBean = new MockAuthorizationBean("", "id, name, street");
		AuthorizationCommand command = new AuthorizationCommand(child, authBean);

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(ODataParser.SELECT_KEY, "id, postcode, name");

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
		// Should just have id and name left
		String result = ctx.getQueryParameters().getFirst(ODataParser.SELECT_KEY);
		assertTrue(result.contains("id"));
		assertTrue(result.contains("name"));
		assertFalse(result.contains("postcode"));
		assertFalse(result.contains("street"));
	}
}
