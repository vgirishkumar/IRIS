package com.temenos.interaction.core.rim;

/*
 * #%L
 * interaction-core
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
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.core.web.RequestContext;

public class TestHeaderHelper {

	@Before
	public void setup() {
		// initialise the thread local request context with requestUri and baseUri
        RequestContext ctx = new RequestContext("http://localhost/myservice.svc", "/baseuri/", null);
        RequestContext.setRequestContext(ctx);
	}

	@Test
	public void testOptionsAllowHeader() {
		SortedSet<String> validNextStates = new TreeSet<String>();
		validNextStates.add("SEE");
		validNextStates.add("HISTORY");
		validNextStates.add("AUTHORISE");
		validNextStates.add("REVERSE");
		validNextStates.add("DELETE");
		validNextStates.add("INPUT");
		validNextStates.add("GET");
		validNextStates.add("HEAD");
		validNextStates.add("OPTIONS");
		
		Response r = HeaderHelper.allowHeader(Response.ok(), validNextStates).build();
		assertEquals("AUTHORISE, DELETE, GET, HEAD, HISTORY, INPUT, OPTIONS, REVERSE, SEE", r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testOptionsNoAllowHeader() {
		Response r = HeaderHelper.allowHeader(Response.ok(), null).build();
		assertNull(r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testOptionsNoValidStates() {
		Response r = HeaderHelper.allowHeader(Response.ok(), new HashSet<String>()).build();
		assertEquals("", r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testLocation() {
		Response r = HeaderHelper.locationHeader(Response.ok(), "/path").build();
		assertEquals("/path", r.getMetadata().getFirst("Location"));
	}
	
	@Test
	public void testLocationNull() {
		Response r = HeaderHelper.locationHeader(Response.ok(), null).build();
		assertNull(r.getMetadata().getFirst("Location"));
	}
	
}
