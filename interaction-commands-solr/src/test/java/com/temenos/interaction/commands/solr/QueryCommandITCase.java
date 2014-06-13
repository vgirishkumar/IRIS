package com.temenos.interaction.commands.solr;

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

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

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
 * The Class QueryCommandITCase.
 */
public class QueryCommandITCase {

	private SolrServer customerSolrServer;
	private SolrServer accountSolrServer;

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

		// Populate Customer Solr core
		TestHarness customerTestHarness = initCustomerSolrCore("customer_search");
		customerSolrServer = new EmbeddedSolrServer(customerTestHarness.getCoreContainer(), customerTestHarness
				.getCore().getName());
		initCustomerTestData();

		// Populate Account Solr core
		TestHarness accountTestHarness = initCustomerSolrCore("account_search");
		accountSolrServer = new EmbeddedSolrServer(accountTestHarness.getCoreContainer(), accountTestHarness.getCore()
				.getName());
		initAccountTestData();

	}

	/**
	 * @return
	 */
	private TestHarness initCustomerSolrCore(String solr_core) {
		SolrConfig customerSolrConfig = TestHarness
				.createConfig(getSolrHome(), solr_core, getSolrConfigFile(solr_core));
		
		TestHarness h = new TestHarness(new File("./target/" + solr_core + "-test/data").getAbsolutePath(),
				customerSolrConfig, getSolrSchemaFile(solr_core));
		return h;
	}

	@After
	public void tearDown() {
		customerSolrServer.shutdown();
		accountSolrServer.shutdown();
	}

	/**
	 * Inits the customer test data.
	 *
	 * @throws Exception the exception
	 */
	private void initCustomerTestData() throws Exception {
		customerSolrServer.add(createCustomer("1", "JOHN1", "John Jones", "123 Somewhere", "W1 IWSH", "UK"), 0);
		customerSolrServer.add(createCustomer("2", "JOHN1", "John Jillian", "123 Somewhere", "W1 IWSH", "UK"), 0);
		customerSolrServer.add(createCustomer("3", "JOHN1", "John Jake", "123 Somewhere", "W1 IWSH", "UK"), 0);
		customerSolrServer.add(createCustomer("4", "JOHN1", "John Jones", "123 Somewhere", "W1 IWSH", "UK"), 0);
		customerSolrServer.add(createCustomer("5", "JOHN1", "John Joker", "123 Somewhere", "W1 IWSH", "UK"), 0);
		customerSolrServer.commit(true, true);
	}

	/**
	 * Inits the account test data.
	 *
	 * @throws Exception the exception
	 */
	private void initAccountTestData() throws Exception {
		accountSolrServer.add(createAccount("1", "JOHN1", "John Jones"), 0);
		accountSolrServer.add(createAccount("2", "JOHN1", "John Jillian"), 0);
		accountSolrServer.add(createAccount("3", "JOHN1", "John Jake"), 0);
		accountSolrServer.add(createAccount("4", "JOHN1", "John Jones"), 0);
		accountSolrServer.add(createAccount("5", "JOHN1", "John Joker"), 0);
		accountSolrServer.commit(true, true);
	}

	/**
	 * Creates the customer.
	 *
	 * @param id the id
	 * @param mnemonic the mnemonic
	 * @param name the name
	 * @param address the address
	 * @param postcode the postcode
	 * @param country the country
	 * @return the solr input document
	 */
	private SolrInputDocument createCustomer(String id, String mnemonic, String name, String address, String postcode,
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
	 * Creates the account.
	 *
	 * @param id the id
	 * @param mnemonic the mnemonic
	 * @param name the name
	 * @return the solr input document
	 */
	private SolrInputDocument createAccount(String id, String mnemonic, String name) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", id, 1.0f);
		doc.addField("mnemonic", mnemonic, 1.0f);
		doc.addField("name", name, 1.0f);
		return doc;
	}

	/**
	 * Test customer select.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomerSelect() {
		SelectCommand command = new SelectCommand(customerSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "John*");
		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(5, cr.getEntities().size());

	}

	/**
	 * Test account select.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAccountSelect() {
		SelectCommand command = new SelectCommand(accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "John*");
		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(5, cr.getEntities().size());

	}

	/**
	 * Test terms.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTerms() {
		TermsCommand command = new TermsCommand(customerSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "j");
		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(5, cr.getEntities().size());

	}

}
