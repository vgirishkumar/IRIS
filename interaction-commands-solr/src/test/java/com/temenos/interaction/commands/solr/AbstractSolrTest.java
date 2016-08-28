package com.temenos.interaction.commands.solr;

/*
 * Helper functions for the Solr unit tests.
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.util.TestHarness;
import org.junit.After;
import org.junit.Before;

/**
 * The Class SolrSearchCommandTest.
 */
public class AbstractSolrTest {

    // Name of core which will be used for testing sharding
    protected static final String SOLR_CORE_FOR_SHARD = "coreForSharding";
    protected static final String SHARD_ENTITY = "MixResults";
	
    // Names of entities. Will be the same as core names
    protected static final String ENTITY1_TYPE = "test_entity1_search";
	protected static final String ENTITY2_TYPE = "test_entity2_search";

	// Name of company
	protected static final String COMPANY_NAME = "TestBank";

	protected SolrServer shardCoreSolrServer;
	protected SolrServer entity1SolrServer;
	protected SolrServer entity2SolrServer;
	
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

		// Just initialise the core used for sharding without any data
		TestHarness shardingTestHarness = initSolrCore(COMPANY_NAME + "/" + SOLR_CORE_FOR_SHARD);
        shardCoreSolrServer = new EmbeddedSolrServer(shardingTestHarness.getCoreContainer(), shardingTestHarness.getCore()
                .getName());
	
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
	    shardCoreSolrServer.shutdown();
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
		entity1SolrServer.add(createEntity1("1111", "JOHN1", "Alan Jones", "111 Somewhere", "AA IWSH", "UK"), 0);
		entity1SolrServer.add(createEntity1("2222", "JOHN2", "B Jillian", "222 Somewhere", "BB IWSH", "UK"), 0);
		entity1SolrServer.add(createEntity1("3333", "JOHN3", "Ima Twin", "333 Somewhere", "CC IWSH", "UK"), 0);
		entity1SolrServer.add(createEntity1("4444", "JOHN4", "Ima Twin", "444 Somewhere", "DD IWSH", "FR"), 0);
		entity1SolrServer.commit(true, true);
	}

	/*
	 * Add a large numbers of extra instances of the test data
	 */
	protected void addEntity1TestData(int count) throws Exception {
		for (int i = 0; i < count; i++) {
			// Write something like a unique entity.
			entity1SolrServer.add(
					createEntity1("" + i + i + i + i, "JOHN" + i, "Ima number " + i, "" + i + i + " Somewhere",
							"DD IWSH", "FR"), 0);
		}
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
}
