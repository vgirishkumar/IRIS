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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.authorization.exceptions.AuthorizationException;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * The Class AuthorizationCommandTest.
 */
public class PostSelectCommandTest extends AbstractAuthorizationTest {

	/**
	 * Check that thrown if selectDone not set.
	 */
	@Test
	public void testNoSelectDoneThrows() {

		PostSelectCommand command = new PostSelectCommand();

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
		} catch (AuthorizationException e) {
			threw = true;
		} catch (Exception e) {
		}

		// Should throw.
		assertTrue(threw);
	}

	/**
	 * Check that no selecting or throw if selectDone true.
	 */
	@Test
	public void testSelectDoneTrue() {

		PostSelectCommand command = new PostSelectCommand();

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		// Run command
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		// Set the flag
		ctx.setAttribute(AuthorizationAttributes.SELECT_DONE_ATTRIBUTE, Boolean.TRUE);

		boolean threw = false;
		try {
			command.execute(ctx);
		} catch (AuthorizationException e) {
			threw = true;
		} catch (Exception e) {
		}

		// Should not throw.
		assertFalse(threw);
	}
}
