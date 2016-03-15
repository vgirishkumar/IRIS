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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.authorization.command.AuthorizationAttributes;
import com.temenos.interaction.commands.solr.data.SolrConstants;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.odataext.odataparser.ODataParser;
import com.temenos.interaction.odataext.odataparser.data.FieldName;
import com.temenos.interaction.odataext.odataparser.data.RowFilter;

public class SolrSearchCommand extends AbstractSolrCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(SolrSearchCommand.class);

	// Somewhere to store references to the embedded Solr servers used during
	// testing.
	private SolrServer testEntity1SolrServer = null;
	private SolrServer testEntity2SolrServer = null;

	// Type of entity used during testing.
	private String testEntityType;
	
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
	 * The third argument tells the test which entity type to associate witht he
	 * first test server.
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

	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {

		// Validate passed parameters
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// Dump query parameters
		Iterator<String> it = ctx.getQueryParameters().keySet().iterator();
		logger.info("SolrSearch command parameters:");
		while (it.hasNext()) {
			String theKey = (String) it.next();
			logger.info("    " + theKey + " = " + ctx.getQueryParameters().getFirst(theKey));
		}

		String coreName = queryParams.getFirst(SolrConstants.SOLR_CORE_KEY);
		String entityName = ctx.getCurrentState().getEntityName();
        if (entityName == null || entityName.isEmpty()) {
            if (testEntity1SolrServer == null) {
                // Still no luck fail fast
                logger.error("Solr search called with null Entity and Solr Core name whcih is used in resolving base Solr Core, giving up request...");
                throw new InteractionException(Status.INTERNAL_SERVER_ERROR, "Solr search called with null Entity and Solr Core name whcih is used in resolving base Solr Core, giving up request...");    
            } else {
                // For testing only...it should not be here though...unusual test
                entityName = testEntityType;
            }
        }
        // If core is not present, use entity name as base core name
        if (coreName == null || coreName.isEmpty()) {
            coreName = entityName; 
        }
   
		// Set up query
		SolrQuery query = buildQuery(queryParams);
		if (null == query) {
			// Could not build a valid query.
			throw new InteractionException(Status.BAD_REQUEST, "SolrQuery is empty, please provide vali options");
		}

		// Set up a client side stub connecting to a Solr server
		SolrServer solrServer;
		if (null != testEntity1SolrServer) {
			// Use one of the test servers
			if (testEntityType == entityName) {
				solrServer = testEntity1SolrServer;
			} else {
				solrServer = testEntity2SolrServer;
			}
		} else {
			// Connect to an external SOLR server
			try {
				URL coreURL = new URL(solrRootURL + "/" + coreName);
				logger.info("Connecting to external Solr server " + coreURL + ".");
				solrServer = new HttpSolrServer(coreURL.toString());
			} catch (MalformedURLException e) {
				logger.error("Malformed URL when connecting to Solr Server. " + e);
				throw new InteractionException(Status.BAD_REQUEST, "Malformed URL when connecting to Solr Server", e);
			}
		}

		// Run the query
		Result res = Result.FAILURE;
		try {
		    QueryResponse rsp = solrServer.query(query);
			// SolrDocumentList list = rsp.getResults();

			ctx.setResource(buildCollectionResource(entityName, rsp.getResults()));
	
			// Indicate that database level filtering was successful.
			ctx.setAttribute(AuthorizationAttributes.FILTER_DONE_ATTRIBUTE, Boolean.TRUE);
			ctx.setAttribute(AuthorizationAttributes.SELECT_DONE_ATTRIBUTE, Boolean.TRUE);
			
			res = Result.SUCCESS;
		} catch (SolrException e) {
			logger.error("An unexpected internal error occurred while querying Solr " + e);
		} catch (SolrServerException e) {
			logger.error("An unexpected error occurred while querying Solr " + e);
		}

		// If we connected to an external server disconnect.
		if (null == testEntity1SolrServer && testEntity2SolrServer == null) {
			// If we started a server connection close it down.
			solrServer.shutdown();
		}

		return (res);
	}

	// Build up a query from the parameters. Returns null on failure.
	SolrQuery buildQuery(MultivaluedMap<String, String> queryParams) {
		SolrQuery query = new SolrQuery();

		// Add Number of rows to fetch
		addNumOfRows(query, queryParams);
		
		// Add Shards for Distributed Query support
		addShards(query, queryParams);
		
		// Build the query string.
		String queryString = buildQueryString(queryParams);
		if (null != queryString) {
			query.setQuery(queryString);
		}

		// Add the filter string (like query but does hard matching).
		addFilter(query, queryParams);

		// If returned fields have been limited by authorization set them
		addSelect(query, queryParams);

		return (query);
	}
	
	/**
	 * By default SolrQuery only returns 10 rows. This is true even if more
     * rows are available. This method will check if user has provided its preference
     * using $top, otherwise use Solr Default
	 * @param query
	 * @param queryParams
	 */
	private void addNumOfRows(SolrQuery query, MultivaluedMap<String, String> queryParams) {
	    int top = 0;
	   try {
	       String topStr = queryParams.getFirst("$top");
	       top = topStr == null || topStr.isEmpty() ? 0 : Integer.parseInt(topStr);
	   } catch (NumberFormatException nfe) {
	       // Do nothing and ignore as we have default value to use
	       
	   }
	   if (top > 0) {
           query.setRows(top);
       } else {
           query.setRows(MAX_ENTITIES_RETURNED);
       }
    }

	/**
	 * This method will add Shards to the Query
	 * @param query
	 * @param queryParams
	 */
	private void addShards(SolrQuery query, MultivaluedMap<String, String> queryParams) {
	    String shards = queryParams.getFirst(SolrConstants.SOLR_SHARDS_KEY);
	    if (shards != null && !shards.isEmpty()) {
	        query.setParam("shards", shards);
	        // Check if user has specified shards.tolerant, add if available
	        String shardsTolerant = queryParams.getFirst(SolrConstants.SOLR_SHARDS_TOLERANT_KEY);
	        if (shardsTolerant != null && !shardsTolerant.isEmpty()) {
	            query.setParam(SolrConstants.SOLR_SHARDS_TOLERANT_KEY, shardsTolerant);
	        }
	    }
	}
	
	// Build Solr field list from an OData $select option.
	private void addSelect(SolrQuery query, MultivaluedMap<String, String> queryParams) {

		// If we were passed an OData $select parse it and add to the query
		String selectOption = queryParams.getFirst(ODataParser.SELECT_KEY);
		if (null != selectOption) {
			// Its a comma separated list of fields.
			Set<FieldName> fields = ODataParser.parseSelect(selectOption);

			logger.info("Adding selects:");
			for (FieldName field : fields) {
				logger.info("    " + field.getName());
				query.addField(field.getName());
			}
		}
		return;
	}

	// Build the Solr query string from passed request.
	private String buildQueryString(MultivaluedMap<String, String> queryParams) {
		String queryStr = new String();

		// If field name not passed use the 'text' field which contains all
		// other fields.
		String fieldName = queryParams.getFirst(SolrConstants.SOLR_FIELD_NAME_KEY);
		if (null == fieldName) {
			fieldName = "text";
		}

		// Add "q=" option if present.
		String query = queryParams.getFirst(SolrConstants.SOLR_QUERY_KEY);
		if (null != query) {
			queryStr = queryStr.concat(fieldName + ":" + query);
		} else {
			// If no query go with everything.
			logger.info("Search called with no query string. Searching on '*'.");
			queryStr = queryStr.concat(fieldName + ":*");
		}
		
		logger.info("Executing query " + queryStr + ".");

		return (queryStr);
	}

	// Build the Solr query string from passed request and any authorization
	// restrictions.
	private void addFilter(SolrQuery query, MultivaluedMap<String, String> queryParams) {

		// If we were passed an OData $filter parse it and add to the query
		String filterOption = queryParams.getFirst(ODataParser.FILTER_KEY);
		if (null != filterOption) {
			try {
				List<RowFilter> filters = ODataParser.parseFilter(filterOption);

				logger.info("Adding filters:");
				for (RowFilter filter : filters) {
					logger.info("    " + filter.getFieldName().getName() + " " + filter.getRelation().getoDataString()
							+ " " + filter.getValue());
					
					// Build filter query. Filter query (fq) syntax is non obvious. Check out on line references.

					switch (filter.getRelation()) {
					case EQ:
						query.addFilterQuery(filter.getFieldName().getName() + ":\"" + filter.getValue() + "\"");
						break;

					case NE:
						query.addFilterQuery("-" + filter.getFieldName().getName() + ":\"" + filter.getValue() + "\"");
						break;

					case LT:
						// fq comparisons uses 'inclusive' [x TO y] syntax. To get an 'exclusive' lt use 'not gt'.
						query.addFilterQuery("-" + filter.getFieldName().getName() + ":[\"" + filter.getValue() + "\" TO *]");
						break;

					case GT:
						// fq comparisons uses 'inclusive' [x TO y] syntax. To get an 'exclusive' gt use 'not lt'.
						query.addFilterQuery("-" + filter.getFieldName().getName() + ":[* TO \"" + filter.getValue() + "\"]");
						break;
						
					case LE:
						query.addFilterQuery(filter.getFieldName().getName() + ":[* TO \"" + filter.getValue() + "\"]");
						break;

					case GE:
						query.addFilterQuery(filter.getFieldName().getName() + ":[\"" + filter.getValue() + "\" TO *]");
						break;

					default:
						logger.warn("Filter condition \"" + filter.getRelation()
								+ "\" not yet implemented ... ignored.");
					}
				}
			} catch (ODataParser.UnsupportedQueryOperationException e) {
				logger.error("Could not interpret OData " + ODataParser.FILTER_KEY + " = " + filterOption);
				return;
			}
		}
		return;
	}
	
	@Override
	protected void customizeEntityProperties(SolrDocument doc, EntityProperties properties) {
	    // By default nothing needs to be done
	}
}
