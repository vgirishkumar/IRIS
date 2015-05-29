package com.temenos.interaction.commands.solr;

/*
 * Unit tests for $select parameter
 */

/* 
 * #%L
 * interaction-commands-solr
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.authorization.command.AuthorizationAttributes;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public class SolrSearchCommandSelectTest extends AbstractSolrTest {

	/**
	 * Test for selection on a single field
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectSingleField() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$select", "name");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		// Unpack results
		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		Collection<EntityResource<Entity>> entities = cr.getEntities();
		assertEquals(4, entities.size());

		// Check results only have the required fields.
		Iterator<EntityResource<Entity>> it = entities.iterator();
		while (it.hasNext()) {
			EntityResource<Entity> entRes = it.next();
			Entity entity = entRes.getEntity();
			EntityProperties props = entity.getProperties();
			/*
			 * // List entries for (Map.Entry<String, EntityProperty> entry :
			 * props.getProperties().entrySet()) { System.out.println("Its " +
			 * entry.getKey() + " " + entry.getValue().getValue()); }
			 */

			Map<String, EntityProperty> entrySet = props.getProperties();
			assertTrue(entrySet.containsKey("name"));
			assertFalse(entrySet.containsKey("id"));
			assertFalse(entrySet.containsKey("mnemonic"));
			assertFalse(entrySet.containsKey("address"));
			assertFalse(entrySet.containsKey("postcode"));
		}
	}

	/**
	 * Test for selection on a multiple fields
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectMultipleField() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$select", "name, mnemonic");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		// Unpack results
		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		Collection<EntityResource<Entity>> entities = cr.getEntities();
		assertEquals(4, entities.size());

		// Check results only have the required fields.
		Iterator<EntityResource<Entity>> it = entities.iterator();
		while (it.hasNext()) {
			EntityResource<Entity> entRes = it.next();
			Entity entity = entRes.getEntity();
			EntityProperties props = entity.getProperties();
			/*
			 * // List entries for (Map.Entry<String, EntityProperty> entry :
			 * props.getProperties().entrySet()) { System.out.println("Its " +
			 * entry.getKey() + " " + entry.getValue().getValue()); }
			 */

			Map<String, EntityProperty> entrySet = props.getProperties();
			assertTrue(entrySet.containsKey("name"));
			assertFalse(entrySet.containsKey("id"));
			assertTrue(entrySet.containsKey("mnemonic"));
			assertFalse(entrySet.containsKey("address"));
			assertFalse(entrySet.containsKey("postcode"));
		}
	}

	/**
	 * Test that select done flag changes state.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectDoneFlag() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$select", "name, mnemonic");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
	
		// Set the flag to the not done state.
		ctx.setAttribute(AuthorizationAttributes.SELECT_DONE_ATTRIBUTE, Boolean.FALSE);
		
		command.execute(ctx);
		
		// Check filtering has been done
		assertEquals(Boolean.TRUE, (Boolean) ctx.getAttribute(AuthorizationAttributes.SELECT_DONE_ATTRIBUTE));
	}
	
	/*
	 * Test throws on bad $select
	 * 
	 * Add if a form of invalid select list can be identified.
	 */
}
