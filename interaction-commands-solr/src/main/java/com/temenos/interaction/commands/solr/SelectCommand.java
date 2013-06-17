package com.temenos.interaction.commands.solr;

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
	private SolrServer solrServer;

	public SelectCommand() {}
	
	public SelectCommand(SolrServer solrServer) {
		this.solrServer = solrServer;
	}
	
	public Result execute(InteractionContext ctx) {

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
		String entityName = ctx.getCurrentState().getEntityName();
		
		try {
			String queryStr = queryParams.getFirst("q");
			SolrQuery query = new SolrQuery();
			query.setQuery(queryStr);

			QueryResponse rsp = solrServer.query(query);
			ctx.setResource(buildCollectionResource(entityName, rsp.getResults()));
			return Result.SUCCESS;
		} catch (SolrServerException e) {
			logger.error("An unexpected error occurred while querying Solr", e);
		}
	    
		return Result.FAILURE;
	}

}
