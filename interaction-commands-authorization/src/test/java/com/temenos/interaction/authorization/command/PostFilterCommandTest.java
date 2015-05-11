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
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.authorization.command.util.ODataParser;
import com.temenos.interaction.authorization.exceptions.AuthorizationException;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * The Class AuthorizationCommandTest.
 */
public class PostFilterCommandTest extends AbstractAuthorizationTest {

	/**
	 * Check that thrown if filterDone not set.
	 */
	@Test
	public void testNoFilterDoneThrows() {

		PostFilterCommand command = new PostFilterCommand();

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
		}
		catch (Exception e) {
		}
		
		// Should throw.
		assertTrue(threw);
	}
	
	/**
	 * Check that no filtering or throw if filterDone true.
	 */
	@Test
	public void testFilterDoneTrue() {

		PostFilterCommand command = new PostFilterCommand();

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		// Run command
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		
		// Set the flag
		ctx.setAttribute(PostFilterCommand.FILTER_DONE_ATTRIBUTE, Boolean.TRUE);
		
		boolean threw = false;
		try {
			command.execute(ctx);
		} catch (AuthorizationException e) {
			threw = true;
		}
		catch (Exception e) {
		}
		
		// Should not throw.
		assertFalse(threw);
	}
	
	/**
	 * Check that filtering is done if filterDone false.
	 */
	@Test
	public void testFilterDoneFalse() {

		PostFilterCommand command = new PostFilterCommand();

		// Path is not important for security
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		// Set up oData parameters
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		// Run command
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		
		// Set the flag
		ctx.setAttribute(PostFilterCommand.FILTER_DONE_ATTRIBUTE, Boolean.FALSE);
		
		// Set up filter
		ctx.getQueryParameters().add(ODataParser.FILTER_KEY, "test");
		
		boolean threw = false;
		try {
			command.execute(ctx);
		} catch (AuthorizationException e) {
			threw = true;
		}
		catch (Exception e) {
		}
		
		// Should not throw.
		assertFalse(threw);
		
		// Should have changed filterDone state.
		assertEquals(Boolean.TRUE, (Boolean) ctx.getAttribute(PostFilterCommand.FILTER_DONE_ATTRIBUTE));
		
		// Check filtering has been done.
		// TODO implement check
	}
	
}
