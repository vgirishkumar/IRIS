package com.temenos.interaction.commands.solr;

/*
 * Unit tests for $filter parameter
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

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.CollectionResource;

public class SolrSearchCommandFilterTest extends AbstractSolrTest {

	/**
	 * Test for filtering on a single field
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterSingleField() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id eq 1111");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test for filtering on a single duplicate field
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterSingleDuplicateField() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id eq 1111");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test for filtering on multiple fields. Where item exists.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterMultipleFieldsHits() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id eq 1111 and name eq 'Alan Jones'");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test for filtering on multiple fields. Where item does not exist.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterMultipleFieldsMisses() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id eq 1111 and name eq 'B Jillian'");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(0, cr.getEntities().size());
	}

	/**
	 * Test for filtering on a single field with near miss. i.e term is close
	 * but not exact. Since filtering is used in security, unlike the 'q='
	 * option only exact hits should be returned.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterSingleFieldMisses() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id eq 1112");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(0, cr.getEntities().size());
	}

	/*
	 * Test throws on bad filter
	 */
	@Test
	public void testBadFilter() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "rubbish");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		boolean threw = false;
		try {
			command.execute(ctx);
		} catch (Exception e) {
			threw = true;
		}
		assertTrue(threw);
	}
	
	/*
	 * Test does not throw on valid filter containing dangerous 'and', 'or' and 'eq' string.
	 */
	@Test
	public void testKeywordFilter() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "Landlord eq aequorin");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		boolean threw = false;
		try {
			command.execute(ctx);
		} catch (Exception e) {
			threw = true;
		}
		assertFalse(threw);
	}
	
	/**
	 * Test for filtering on 'not equal' single field
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterNotEqual() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id ne 1111");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		
		// Should get 3 out of 4
		assertEquals(3, cr.getEntities().size());
	}
	
	/**
	 * Test for filtering on 'greater than' single field
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterGreaterThan() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id gt 3333");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		
		// Should get 4444
		assertEquals(1, cr.getEntities().size());
	}
	
	/**
	 * Test for filtering on 'less than' single field
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterLessThan() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id lt 2222");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		
		// Should get 1111
		assertEquals(1, cr.getEntities().size());
	}
	
	/**
	 * Test for filtering on a range. GE one value and LE another.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterRange() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id gt 2222");
		queryParams.add("$filter", "id lt 3333");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		
		// Should get 2222 and 3333
		assertEquals(2, cr.getEntities().size());
	}
	
	/**
	 * Test for filtering on 'greater than or equal' single field
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterGreaterThanEqual() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id ge 3333");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		
		// Should get 3333 and 4444
		assertEquals(2, cr.getEntities().size());
	}
	
	/**
	 * Test for filtering on 'less than or equal' single field
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterLessThanEqual() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Add OData filter
		queryParams.add("$filter", "id le 2222");

		InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams,
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		
		// Should get 1111 and 2222
		assertEquals(2, cr.getEntities().size());
	}



}
