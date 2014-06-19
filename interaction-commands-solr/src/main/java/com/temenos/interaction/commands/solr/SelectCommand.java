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


import java.util.Properties;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class SelectCommand extends AbstractSolrCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(SelectCommand.class);

	@Autowired
	private SolrServer solrCustomerServer;

	@Autowired
	private SolrServer solrAccountServer;

	public SelectCommand() {}
	
	private static final String SOLR_CORE = "core";
	private static final String SOLR_QUERY = "q";
	private static final String SOLR_CORE_CUSTOMERS = "customer_search";
	private static final String SOLR_CORE_ACCOUNTS = "account_search";
	
	/**
	 * Instantiates a new select command.
	 *
	 * @param solrCustomerServer the solr customer server
	 * @param solrAccountServer the solr account server
	 */
	public SelectCommand(SolrServer solrCustomerServer, SolrServer solrAccountServer) {
		this.solrCustomerServer = solrCustomerServer;
		this.solrAccountServer = solrAccountServer;
	}
	
	public Result execute(InteractionContext ctx) {

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
		String queryStr = queryParams.getFirst("q");
		SolrServer solrServer = null;
		SolrQuery query = new SolrQuery();
		String entityName = ctx.getCurrentState().getEntityName();
		if (entityName != null){
	       Properties properties = ctx.getCurrentState().getViewAction().getProperties();
	        if (properties != null)
	        {
	        	String core = properties.getProperty(SOLR_CORE);
	        	// Check for Customers Solr core in RIM
	        	if (core.equalsIgnoreCase(SOLR_CORE_CUSTOMERS) ) {
	        		solrServer = solrCustomerServer;
	        		query.setQuery(getCustomerQuery(queryStr));
	        	}
	        	// Check for Account Solr core in RIM
	        	else if (core.equalsIgnoreCase(SOLR_CORE_ACCOUNTS)) {
	        		solrServer = solrAccountServer;
	        		query.setQuery(getAccountQuery(queryStr));
	        	}
	        	else
	        	{
					logger.error("Solr core not defined!");
					return Result.FAILURE;	        	
				}
			}
		}
        else
        {
        	// Default Solr core
    		solrServer = solrCustomerServer;
    		query.setQuery(getCustomerQuery(queryStr));
        }
	
        try {
			QueryResponse rsp = solrServer.query(query);
			//SolrDocumentList list = rsp.getResults();
			//System.out.println("Found : " +rsp.getResults().size() );
			 
			ctx.setResource(buildCollectionResource(entityName, rsp.getResults()));
			return Result.SUCCESS;
		} catch (SolrServerException e) {
			logger.error("An unexpected error occurred while querying Solr", e);
		}
	    
		return Result.FAILURE;
	}

	private String getCustomerQuery(String query)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("id:\"" + query + "\"");
		sb.append(" name:\"*" + query + "*\"");
		sb.append(" mnemonic:\"*" + query + "*\"");
		sb.append(" address:\"*" + query + "*\"");
		sb.append(" postcode:\"*" + query + "*\"");
		return sb.toString();
		
	}	
	
	private String getAccountQuery(String query)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("id:\"" + query + "\"");
		sb.append(" name:\"*" + query + "*\"");
		sb.append(" mnemonic:\"*" + query + "*\"");
		return sb.toString();
	}
}
