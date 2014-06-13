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


import java.util.List;

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
		String entityName = ctx.getCurrentState().getEntityName();
		
		try {
			// Get Solr query param
			List<String> queryList = queryParams.get(SOLR_QUERY);
			if (queryList == null)
			{
				logger.error("Solr queries not defined!");
				return Result.FAILURE;
			}
		
			String queryStrx = queryParams.getFirst("q");
			String queryStr = queryList.get(0);
			SolrQuery query = new SolrQuery();
			query.setQuery(queryStr);

			// Get Solr core param
			List<String> cores = queryParams.get(SOLR_CORE);
			if (cores == null)
			{
				logger.error("Solr core not defined!");
				return Result.FAILURE;
			}
			QueryResponse rsp = null;
			if (cores.get(0).equals(SOLR_CORE_CUSTOMERS))
			{
				rsp = solrCustomerServer.query(query);
			}
			else if (cores.get(0).equals(SOLR_CORE_ACCOUNTS))
			{
				rsp = solrAccountServer.query(query);
			}
			else
			{
				logger.error("Solr core not defined!");
				return Result.FAILURE;
			}

			ctx.setResource(buildCollectionResource(entityName, rsp.getResults()));
			return Result.SUCCESS;
		} catch (SolrServerException e) {
			logger.error("An unexpected error occurred while querying Solr", e);
		}
	    
		return Result.FAILURE;
	}

}
