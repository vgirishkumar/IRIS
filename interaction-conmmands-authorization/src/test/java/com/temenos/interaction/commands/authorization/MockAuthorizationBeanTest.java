package com.temenos.interaction.commands.authorization;

/*
 * Base class for the authorization bean tests.
 * 
 * A bit pointless for the mock bean but will form a template for other authorization bean tests.
 */

/* 
 * #%L
 * interaction-commands-sms
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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	 * Test parameters are remembered.
	 */
	@Test
	public void testParameters() {

		// Create the bean
		MockAuthorizationBean bean = new MockAuthorizationBean("filter", "select");

		// Create a minimal context
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), null, null,
				mock(ResourceState.class), mock(Metadata.class));

		// Check that the expected parameter is present
		assertEquals("filter", bean.getFilter(ctx));
		assertEquals("select", bean.getSelect(ctx));
	}
	
	/**
	 * Test null parameters.
	 */
	@Test
	public void testNullParameters() {

		// Create the bean
		MockAuthorizationBean bean = new MockAuthorizationBean(null, null);

		// Create a minimal context
		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), null, null,
				mock(ResourceState.class), mock(Metadata.class));

		// Check that the expected parameter is present
		assertEquals(null, bean.getFilter(ctx));
		assertEquals(null, bean.getSelect(ctx));
	}

}
