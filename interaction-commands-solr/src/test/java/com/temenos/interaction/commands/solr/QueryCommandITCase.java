package com.temenos.interaction.commands.solr;

/*
 * Unit tests for the SOLR Select command. These are self contained. They start a local embedded SOLR server. This is contained,
 * and only accessible by the test's local (jUnit) JVM. No external server is required. The working directory must be set to 
 * the location of the SOLR configuration files. Currently this is:
 * 
 *        Hothouse\Jenkins
 * 
 * Note : If SOLR test documents are changed this will not clear existing SOLR index files. Jenkins clears these on every run.
 *        If testing a local machine this may have to be done manually. Delete:
 *        
 *          Hothouse\Jenkins\target
 *        
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
import org.junit.Ignore;
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
	 * @throws Exception
	 *             the exception
	 */
	private void initCustomerTestData() throws Exception {
		customerSolrServer.add(createCustomer("1111", "JOHN1", "A Jones", "111 Somewhere", "AA IWSH", "UK"), 0);
		customerSolrServer.add(createCustomer("2222", "JOHN2", "B Jillian", "222 Somewhere", "BB IWSH", "UK"), 0);
		customerSolrServer.add(createCustomer("3333", "JOHN3", "Ima Twin", "333 Somewhere", "CC IWSH", "UK"), 0);
		customerSolrServer.add(createCustomer("4444", "JOHN4", "Ima Twin", "444 Somewhere", "DD IWSH", "FR"), 0);
		customerSolrServer.commit(true, true);
	}

	/**
	 * Inits the account test data.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void initAccountTestData() throws Exception {
		accountSolrServer.add(createAccount("1111", "JOHN1", "Fred Jones"), 0);
		accountSolrServer.add(createAccount("2222", "JOHN2", "John Jillian"), 0);
		accountSolrServer.commit(true, true);
	}

	/**
	 * Creates the customer.
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
	 * @param id
	 *            the id
	 * @param mnemonic
	 *            the mnemonic
	 * @param name
	 *            the name
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
	 * Test select for single customers. Search on all fields.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomerSelectAllFields() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "A Jones");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for duplicate customers. Search on all fields.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDuplicateCustomerSelectAllFields() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "Ima Twin");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(2, cr.getEntities().size());
	}

	/**
	 * Test select for single customers. Search on a named field.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomerSelectByName() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "A Jones");
		queryParams.add("fieldname", "name");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test customer select.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomerSelectByMnenomic() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "JOHN4");
		queryParams.add("fieldname", "mnemonic");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());
	}

	/**
	 * Test select for several customers with similar names.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomerSelectBySimilarName() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "JOHN");
		queryParams.add("fieldname", "mnemonic");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(4, cr.getEntities().size());
	}

	/**
	 * Test that a specific core can be selected.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGoodCoreName() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "John");
		queryParams.add("core", "account_search");
		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));
		InteractionCommand.Result result = command.execute(ctx);
		assertEquals(Result.SUCCESS, result);

		CollectionResource<Entity> cr = (CollectionResource<Entity>) ctx.getResource();
		assertEquals(1, cr.getEntities().size());

	}

	/**
	 * Test fails if query not present
	 */
	@Test
	public void testFailsOnNoQuery() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "JOHN4");
		queryParams.add("fieldname", "rubbish");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		InteractionCommand.Result result = null;
		try {
			result = command.execute(ctx);
		} catch (Exception e) {
			fail("Threw on no query");
		}
		assertEquals("Did not fail on no query", Result.FAILURE, result);
	}

	/**
	 * Test fails on unknown field name.
	 */
	@Test
	public void testFailsOnBadFieldName() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "JOHN4");
		queryParams.add("fieldname", "rubbish");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		InteractionCommand.Result result = null;
		try {
			result = command.execute(ctx);
		} catch (Exception e) {
			fail("Threw on bad field name");
		}
		assertEquals("Did not fail on bad field name", Result.FAILURE, result);
	}

	/**
	 * Test fails on unknown core.
	 */
	@Test
	public void testFailsOnBadCoreName() {
		SelectCommand command = new SelectCommand(customerSolrServer, accountSolrServer);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("q", "JOHN4");
		queryParams.add("core", "rubbish");

		InteractionContext ctx = new InteractionContext(mock(HttpHeaders.class), new MultivaluedMapImpl<String>(),
				queryParams, mock(ResourceState.class), mock(Metadata.class));

		InteractionCommand.Result result = null;
		try {
			result = command.execute(ctx);
		} catch (Exception e) {
			fail("Threw on bad core name");
		}
		assertEquals("Did not fail on bad core name", Result.FAILURE, result);
	}

	/**
	 * Test terms.
	 */
	// Looks like dead code. Skip it.
	@Ignore
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
