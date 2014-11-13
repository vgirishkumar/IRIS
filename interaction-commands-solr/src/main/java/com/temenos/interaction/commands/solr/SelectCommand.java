package com.temenos.interaction.commands.solr;

/*
 * The SOLR search command. Can be called with the following parameters.
 * 
 *      'core'      Name of the core to search. Defaults to the customer core.
 *      'q'         SOLR query term to search for.
 *      'feldname'  Name of field to search. Defaults to 'text' i.e. all fields (See schema.xml for details).
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

import javax.ws.rs.core.MultivaluedMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class SelectCommand extends AbstractSolrCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(SelectCommand.class);

	@Autowired
	@Qualifier("customersSolrServer")
	private SolrServer solrCustomerServer;

	@Autowired
	@Qualifier("accountsSolrServer")
	private SolrServer solrAccountServer;

	public SelectCommand() {
	}

	// Keys for the key/value pairs which can be passed in as part of the search
	// URL.
	private static final String CORE_KEY = "core";
	private static final String QUERY_KEY = "q";
	private static final String FIELD_NAME_KEY = "fieldname";

	public static final String SOLR_CORE_CUSTOMERS = "customer_search";
	public static final String SOLR_CORE_ACCOUNTS = "account_search";

	/**
	 * Instantiates a new select command.
	 * 
	 * @param solrCustomerServer
	 *            the solr customer server
	 * @param solrAccountServer
	 *            the solr account server
	 */
	public SelectCommand(SolrServer solrCustomerServer, SolrServer solrAccountServer) {
		this.solrCustomerServer = solrCustomerServer;
		this.solrAccountServer = solrAccountServer;
	}

	public Result execute(InteractionContext ctx) {

		// Validate passed parameters
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		String queryValue = queryParams.getFirst(QUERY_KEY);
		if (null == queryValue) {
			logger.warn("Search called with no query string.");
			return Result.FAILURE;
		}

		String coreName = queryParams.getFirst(CORE_KEY);
		String fieldName = queryParams.getFirst(FIELD_NAME_KEY);
		String entityName = ctx.getCurrentState().getEntityName();

		// TODO remove before production.
		logger.info("Calling search on core " + coreName + " entity " + entityName + " query " + queryValue
				+ " field name " + fieldName);

		// Connect to SOLR server
		SolrServer solrServer = null;
		if (null == coreName) {
			// Default to customer_search core
			solrServer = solrCustomerServer;
		} else if (coreName.equalsIgnoreCase(SOLR_CORE_CUSTOMERS)) {
			solrServer = solrCustomerServer;
		} else if (coreName.equalsIgnoreCase(SOLR_CORE_ACCOUNTS)) {
			solrServer = solrAccountServer;
		} else {
			// Unknown core. fail
			return Result.FAILURE;
		}

		// Set up query
		SolrQuery query = new SolrQuery();
		query.setQuery(buildQuery(fieldName, queryValue));

		try {
			QueryResponse rsp = solrServer.query(query);
			// SolrDocumentList list = rsp.getResults();

			ctx.setResource(buildCollectionResource(entityName, rsp.getResults()));
			return Result.SUCCESS;
		} catch (SolrException e) {
			logger.error("An unexpected error occurred while querying Solr", e);
		} catch (SolrServerException e) {
			logger.error("An unexpected error occurred while querying Solr", e);
		}

		return Result.FAILURE;
	}

	// Build a Solr query string.
	private String buildQuery(String fieldName, String query) {
		// If field name not passed use the 'text' field which contains all
		// other fields.
		if (null == fieldName) {
			fieldName = "text";
		}

		// Quote the query in case it contains any spaces etc.
		String queryStr = new String(fieldName + ":\"*" + query + "*\"");
		return (queryStr);
	}
}
