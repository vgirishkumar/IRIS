package com.temenos.interaction.commands.solr;

/*
 * Unit tests for the SOLR Select command. These are self contained. They start a local embedded SOLR server. This is contained
 * in, and only accessible by, the test's local (jUnit) JVM. No external server is required. The working directory must be set
 * to the location of the SOLR configuration files. Currently this is:
 * 
 *        Hothouse\Jenkins  
 *        
 * Due to mocking of the server connection there are some features (e.g. failure on bad core names) that cannot be tested here.
 * These should be tested by HotHouse integration tests,
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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.temenos.interaction.commands.solr.data.SolrConstants;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.CollectionResource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URL;


/**
 * The Class SolrSearchCommandTest.
 */
public class SolrSearchCommandTest extends AbstractSolrTest {

	private @Mock ResourceState currentState;

	private SolrSearchCommand command;
	private InteractionContext ctx;
	private MultivaluedMap<String, String> queryParams;
	private MultivaluedMap<String, String> pathParams;


	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		command = new SolrSearchCommand();
		queryParams = new MultivaluedMapImpl<String>();
		pathParams = new MultivaluedMapImpl<String>();
		ctx = spy(new InteractionContext(
				mock(UriInfo.class),
				mock(HttpHeaders.class),
				pathParams,
				queryParams,
				mock(ResourceState.class),
				mock(Metadata.class)));

		when(ctx.getCurrentState()).thenReturn(currentState);
	}

	/**
	 * Test select for single entities. Search on all fields.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEntity1SelectAllFields() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		queryParams.add("q", "A Jones");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for duplicate entities. Search on all fields.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDuplicateEntity1SelectAllFields() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		queryParams.add("q", "Ima Twin");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(2, cr.getEntities().size());
	}

	/**
	 * Test select for single Entity1 search on a named field.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEntity1SelectByName() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		queryParams.add("q", "name:Alan Jones");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for single Entity2 search on a named field.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEntity2SelectByName() {
		when(currentState.getEntityName()).thenReturn(ENTITY2_TYPE);

		queryParams.add("q", "name:Alan Jones");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity2SolrServer);
    		assertEquals(Result.SUCCESS, result);
    
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for partial match with wildcard at start. Should work.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWildcardStart() {
		when(currentState.getEntityName()).thenReturn(ENTITY2_TYPE);

		// Add part of "Alan Jones"
		queryParams.add("q", "name:*lan Jones");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity2SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for partial match with wildcard at end. Should work.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWildcardEnd() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		// Add part of "Alan Jones"
		queryParams.add("q", "name:A Jon*");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for partial match with wildcard at start and end . Should
	 * work.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWildcardBoth() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		// Add part of "Alan Jones"
		queryParams.add("q", "name:*lan Jon*");
		pathParams.add("companyid", COMPANY_NAME);
		try {
		    InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for partial match with no wildcard. Some wildcarding is done
	 * automatically so use quotes to force an exact match. Should not work.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWildcardNeither() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		// Add part of "Alan Jones"
		queryParams.add("q", "name:\"lan Jones\"");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(0, cr.getEntities().size());
	}

	/**
	 * Test that a specific core can be selected.
	 */
	@Test
	public void testSpecificCoreName() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		queryParams.add("q", "John");
		pathParams.add("companyid", COMPANY_NAME);
		// Specify a different core for the query
		queryParams.add("core", ENTITY2_TYPE);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity2SolrServer);
    		assertEquals(Result.SUCCESS, result);
		} catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());

	}

	/**
	 * May be called with 'q=' missing ... filtering on '$filter='
	 */
	@Test
	public void testMissingQuery() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals("Did failed on mission 'q='", Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }
	}

	/**
	 * Test fails on unknown field name.
	 */
	@Test
	public void testFailsOnBadFieldName() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);
		queryParams.add("q", "rubbish:A Jones");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals("Did not fail on bad fieldname", Result.FAILURE, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }
	}

	/**
	 * Test fails if company not present
	 */
	@Ignore("Invalid test, it should not be here as its T24 specific test")
	@Test
	public void testFailsOnMissingComapny() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);
		queryParams.add("q", "A Jones");
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals("Did not fail on missing company", Result.FAILURE, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }
	}

	/**
	 * Check that select still works if there are additional (ignored)
	 * parameters.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWorksWithAdditionalParams() {
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);

		queryParams.add("q", "A Jones");
		pathParams.add("companyid", COMPANY_NAME);
		// Extra params
		queryParams.add("filter", "rubbish");
		queryParams.add("rubbish", "rubbish");
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test for a large number of entries
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEntity1SelectLarge() {

		try {
			// Add twice as many test entities as the max number returned.
			addEntity1TestData(SolrSearchCommand.MAX_ENTITIES_RETURNED * 2);
		} catch (Exception e) {
			fail("Adding extra entities threw. " + e);
		}
		when(currentState.getEntityName()).thenReturn(ENTITY1_TYPE);
		// Get all the entries
		queryParams.add("q", "*");
		pathParams.add("companyid", COMPANY_NAME);
		try {
    		InteractionCommand.Result result = command.execute(ctx, entity1SolrServer);
    		assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(SolrSearchCommand.MAX_ENTITIES_RETURNED, cr.getEntities().size());
	}
	
	@Ignore ("This test is currently ignored because distributed search is not supported by Embedded Solr. See https://issues.apache.org/jira/browse/SOLR-1858 for more details")
	@Test
	public void testSolrSharding() {
		when(currentState.getEntityName()).thenReturn(SHARD_ENTITY);
        // Search for a string Jillian, according to the test data we should get data from both cores
        queryParams.add(SolrConstants.SOLR_QUERY_KEY, "Jillian");
        pathParams.add(SolrConstants.SOLR_COMPANY_NAME_KEY, COMPANY_NAME);
        // Add the core and shards param
        queryParams.add(SolrConstants.SOLR_CORE_KEY, SOLR_CORE_FOR_SHARD);
        queryParams.add(SolrConstants.SOLR_SHARDS_KEY, ENTITY1_TYPE + "," + ENTITY2_TYPE);
        queryParams.add(SolrConstants.SOLR_SHARDS_TOLERANT_KEY, "true");
        // Extra params
        queryParams.add("filter", "rubbish");
        queryParams.add("rubbish", "rubbish");
        try {
            InteractionCommand.Result result = command.execute(ctx, shardCoreSolrServer);
            assertEquals(Result.SUCCESS, result);
        } catch (InteractionException e) {
            fail("InteractionException : " + e.getHttpStatus().toString() + " - " + e.getMessage());
        }

        CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
        assertEquals(2, cr.getEntities().size());
	}
	
}
