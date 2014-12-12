package com.temenos.interaction.commands.solr;

/*
 * The SOLR search command. Can be called with the following parameters.
 * 
 *      'core'      Name of the core to search. Defaults to the entity1 core.
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

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class SolrSearchCommand extends AbstractSolrCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(SolrSearchCommand.class);

	// Somewhere to store references to the embedded Solr servers used during
	// testing.
	private SolrServer testEntity1SolrServer = null;
	private SolrServer testEntity2SolrServer = null;

	// Type of entity used during testing.
	private String testEntityType;

	// Root of the Solr URL
	private String solrRootURL;

	// Keys for the key/value pairs which can be passed in as part of the search
	// URL.
	private static final String CORE_KEY = "core";
	private static final String QUERY_KEY = "q";
	private static final String FIELD_NAME_KEY = "fieldname";
	private static final String COMPANY_NAME_KEY = "companyid";

	/**
	 * Instantiates a new select command.
	 * 
	 * For production we pass in, and connect to, the URL of an external server
	 * for each search request.
	 * 
	 * In future we may introduce connection pooling.
	 */
	public SolrSearchCommand(String solrRootURL) {
		this.solrRootURL = solrRootURL;
	}

	/**
	 * Instantiates a new select command for unittests.
	 * 
	 * Unit tests use an embedded Solr server. It has no URL so just pass in the
	 * server references.
	 * 
	 * The third argument tells the test which entity type to associate witht he first test server.
	 * 
	 * NOT TO BE USED FOR PRODUCTION CODE.
	 * 
	 * @param solrServer
	 *            the solr server
	 */
	public SolrSearchCommand(SolrServer entity1Server, SolrServer entity2Server, String entityType) {
		testEntity1SolrServer = entity1Server;
		testEntity2SolrServer = entity2Server;
		testEntityType = entityType;
	}

	public Result execute(InteractionContext ctx) {

		// Validate passed parameters
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		String queryValue = queryParams.getFirst(QUERY_KEY);
		if (null == queryValue) {
			logger.warn("Search called with no query string.");
			return Result.FAILURE;
		}
		
		// Dump query parameters
		/* Iterator<String> it = ctx.getQueryParameters().keySet().iterator();
		while (it.hasNext()) {
			String theKey = (String) it.next();
			logger.info("    Key " + theKey + " = Value " + ctx.getQueryParameters().getFirst(theKey));
		}	
		*/
		
		String coreName = queryParams.getFirst(CORE_KEY);
		String fieldName = queryParams.getFirst(FIELD_NAME_KEY);
		String entityType = ctx.getCurrentState().getEntityName();

		String companyName = ctx.getPathParameters().getFirst(COMPANY_NAME_KEY);
		if (null == companyName) {
			logger.warn("Search called with no company string.");
			return Result.FAILURE;
		}

		// TODO remove before production.
		logger.info("Calling search on company " + companyName + " core " + coreName + " entity " + entityType
				+ " query " + queryValue + " field name " + fieldName);

		// Validate entity type
		if (null == entityType) {
			if (null == testEntity1SolrServer) {
				// In production there must always be a valid entity name
				logger.error("Select invoked with null entity type.");
				return (Result.FAILURE);
			} else {
				// For test this expected. Use core or passed entity type.
				if (null == coreName) {
					entityType = testEntityType;
				} else {
					entityType = coreName;
				}
			}
		}

		// Work out which core should be used.
		if (null == coreName) {
			// Use a core with the same name as the entity type.
			coreName = entityType;
		}

		// Set up a client side stub connecting to a Solr server
		SolrServer solrServer;
		if (null != testEntity1SolrServer) {
			// Use one of the test servers
			if (testEntityType == entityType) {
				solrServer = testEntity1SolrServer;
			} else {
				solrServer = testEntity2SolrServer;
			}
		} else {
			// Connect to an external SOLR server
			try {
				URL coreURL = new URL(solrRootURL + "/" + companyName + "_" + coreName);
				logger.info("Connecting to external Solr server " + coreURL + ".");
				solrServer = new HttpSolrServer(coreURL.toString());
			} catch (MalformedURLException e) {
				logger.error("Malformed URL when connecting to Solr Server. " + e);
				return (Result.FAILURE);
			}
		}

		// Set up query
		SolrQuery query = new SolrQuery();
		// query.setTermsLimit(100);
		// query.setTermsMaxCount(100);
		
		// By default SolrQuery only returns 10 rows. This si true even if more rows are available. Since we will be 
		// reading more than this increase the number of rows returned.
		query.setRows(MAX_ENTITIES_RETURNED);
		query.setQuery(buildQuery(fieldName, queryValue));

		Result res = Result.FAILURE;
		try {
			QueryResponse rsp = solrServer.query(query);
			// SolrDocumentList list = rsp.getResults();

			ctx.setResource(buildCollectionResource(entityType, rsp.getResults()));
			res = Result.SUCCESS;
		} catch (SolrException e) {
			logger.error("An unexpected internal error occurred while querying Solr " + e);
		} catch (SolrServerException e) {
			logger.error("An unexpected error occurred while querying Solr " + e);
		}

		// If we connected to an external server disconnect.
		if (null == testEntity1SolrServer) {
			// If we started a server connection close it down.
			solrServer.shutdown();
		}

		return (res);
	}

	// Build a Solr query string.
	private String buildQuery(String fieldName, String query) {
		// If field name not passed use the 'text' field which contains all
		// other fields.
		if (null == fieldName) {
			fieldName = "text";
		}

		// Quote the query in case it contains any spaces etc.
		String queryStr = new String(fieldName + ":" + query);
		
		logger.info("Executing query " + queryStr + ".");
		
		return (queryStr);
	}
}
