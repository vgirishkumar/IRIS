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
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.util.TestHarness;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.CollectionResource;

/**
 * The Class SolrSearchCommandITCase.
 */
public class SolrSearchCommandITCase {

	// Names of entities. Will be the same as core names
	private static final String ENTITY1_TYPE = "test_entity1_search";
	private static final String ENTITY2_TYPE = "test_entity2_search";

	// Name of company
	private static final String COMPANY_NAME = "TestBank";

	private SolrServer entity1SolrServer;
	private SolrServer entity2SolrServer;

	private String getSolrHome() {
		return "solr";
	}

	private String getSolrSchemaFile(String solr_core) {
		return "solr/" + solr_core + "/conf/schema.xml";
	}

	private String getSolrConfigFile(String solr_core) {
		return "solr/" + solr_core + "/conf/solrconfig.xml";
	}

	@Before
	public void setUp() throws Exception {
		System.setProperty("solr.solr.home", getSolrHome());

		// Populate Entity1 Solr core
		TestHarness entity1TestHarness = initSolrCore(COMPANY_NAME + "/" + ENTITY1_TYPE);
		entity1SolrServer = new EmbeddedSolrServer(entity1TestHarness.getCoreContainer(), entity1TestHarness.getCore()
				.getName());
		initEntity1TestData();

		// Populate Entity2 Solr core
		TestHarness entity2TestHarness = initSolrCore(COMPANY_NAME + "/" + ENTITY2_TYPE);
		entity2SolrServer = new EmbeddedSolrServer(entity2TestHarness.getCoreContainer(), entity2TestHarness.getCore()
				.getName());
		initEntity2TestData();

	}

	/**
	 * @return
	 */
	private TestHarness initSolrCore(String solrCore) {
		File dataDir = getSolrDataDir(solrCore);

		// Clear any junk from the index.
		clearDataDir(dataDir);

		SolrConfig solrConfig = TestHarness.createConfig(getSolrHome(), solrCore, getSolrConfigFile(solrCore));

		TestHarness h = new TestHarness(dataDir.getAbsolutePath(), solrConfig, getSolrSchemaFile(solrCore));
		return h;
	}

	/**
	 * Find location of the Solr data.
	 * 
	 * @param solrCore
	 * @return
	 */
	private File getSolrDataDir(String solrCore) {
		return (new File("./target/" + COMPANY_NAME + '/' + solrCore + "-test/data"));
	}

	/**
	 * Clear out any existing data in index.
	 */
	private void clearDataDir(File dataDir) {
		try {
			FileUtils.deleteDirectory(dataDir);
		} catch (IOException e) {
			// No problem. Probably did not exist.
		}
	}

	@After
	public void tearDown() {
		entity1SolrServer.shutdown();
		entity2SolrServer.shutdown();

		// Tidy all indexes
		clearDataDir(getSolrDataDir(ENTITY1_TYPE));
		clearDataDir(getSolrDataDir(ENTITY2_TYPE));
	}

	/**
	 * Inits the entity1 test data.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void initEntity1TestData() throws Exception {
		entity1SolrServer.add(createEntity1("1111", "JOHN1", "A Jones", "111 Somewhere", "AA IWSH", "UK"), 0);
		entity1SolrServer.add(createEntity1("2222", "JOHN2", "B Jillian", "222 Somewhere", "BB IWSH", "UK"), 0);
		entity1SolrServer.add(createEntity1("3333", "JOHN3", "Ima Twin", "333 Somewhere", "CC IWSH", "UK"), 0);
		entity1SolrServer.add(createEntity1("4444", "JOHN4", "Ima Twin", "444 Somewhere", "DD IWSH", "FR"), 0);
		entity1SolrServer.commit(true, true);
	}

	/**
	 * Inits the entity2 test data.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void initEntity2TestData() throws Exception {
		entity2SolrServer.add(createEntity2("1111", "JOHN1", "Fred Jones"), 0);
		entity2SolrServer.add(createEntity2("2222", "JOHN2", "John Jillian"), 0);
		entity2SolrServer.commit(true, true);
	}

	/**
	 * Creates the entity1.
	 * 
	 * @param id
	 *            the id
	 * @param mnemonic
	 *            the mnemonic
	 * @param name
	 *            the name
	 * @param address
	 *            the address
	 * @param postcode
	 *            the postcode
	 * @param country
	 *            the country
	 * @return the solr input document
	 */
	private SolrInputDocument createEntity1(String id, String mnemonic, String name, String address, String postcode,
			String country) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", id, 1.0f);
		doc.addField("mnemonic", mnemonic, 1.0f);
		doc.addField("name", name, 1.0f);
		doc.addField("address", address, 1.0f);
		doc.addField("postcode", postcode, 1.0f);
		// doc.addField("country", country, 1.0f);
		return doc;
	}

	/**
	 * Creates the entity2.
	 * 
	 * @param id
	 *            the id
	 * @param mnemonic
	 *            the mnemonic
	 * @param name
	 *            the name
	 * @return the solr input document
	 */
	private SolrInputDocument createEntity2(String id, String mnemonic, String name) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", id, 1.0f);
		doc.addField("mnemonic", mnemonic, 1.0f);
		doc.addField("name", name, 1.0f);
		return doc;
	}

	/**
	 * Test select for single entity1s. Search on all fields.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEntity1SelectAllFields() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "A Jones");

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));

		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for duplicate entity1s. Search on all fields.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDuplicateEntity1SelectAllFields() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "Ima Twin");

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(2, cr.getEntities().size());
	}

	/**
	 * Test select for single entity1s. Search on a named field.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEntity1SelectByName() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "A Jones");
		queryParams.add("fieldname", "name");

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test entity1 select.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEntity1SelectByMnenomic() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "JOHN4");
		queryParams.add("fieldname", "mnemonic");

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test that a specific core can be selected.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSpecificCoreName() {
		// Specify a core as the default
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "John");

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		// Specify a different core for the query
		queryParams.add("core", ENTITY2_TYPE);
		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());

	}

	/**
	 * Test fails if query not present
	 */
	@Test
	public void testFailsOnMissingQuery() {
		
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));

		InteractionCommand.Result result = command.execute(ctx);

		assertEquals("Did not fail on no query", Result.FAILURE, result);
	}

	/**
	 * Test fails on unknown field name.
	 */
	@Test
	public void testFailsOnBadFieldName() {
		
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "A Jones");
		queryParams.add("fieldname", "rubbish");

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));

		InteractionCommand.Result result = command.execute(ctx);

		assertEquals("Did not fail on bad fieldname", Result.FAILURE, result);
	}
	
	/**
	 * Test fails if company not present
	 */
	@Test
	public void testFailsOnMissingComapny() {
		
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "A Jones");

		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));

		InteractionCommand.Result result = command.execute(ctx);

		assertEquals("Did not fail on missing company", Result.FAILURE, result);
	}

	
	/**
	 * Check that select still works if there are additional (ignored) parameters.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWorksWithAdditionalParams() {
		SolrSearchCommand command = new SolrSearchCommand(entity1SolrServer, entity2SolrServer, ENTITY1_TYPE);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "A Jones");
		
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("companyid", COMPANY_NAME);
		
		// Extra params
		queryParams.add("filter", "rubbish");
		queryParams.add("rubbish", "rubbish");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), pathParams, queryParams,
				mock(ResourceState.class), mock(Metadata.class));

		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

}
