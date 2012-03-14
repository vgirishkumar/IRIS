package com.temenos.interaction.commands.odata;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class GETEntitiesCommand implements ResourceGetCommand {
	private final Logger logger = LoggerFactory.getLogger(GETEntitiesCommand.class);

	// Command configuration
	private String entitySetName;
	
	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;

	public GETEntitiesCommand(String entitySetName, ODataProducer producer) {
		this.entitySetName = entitySetName;
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entitySetName);
		assert(entitySet != null);
	}

	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		logger.info("Getting entities for " + entitySet.getName());

		String inlineCount = queryParams.getFirst("$inlinecount");
		String top = queryParams.getFirst("$top");
		String skip = queryParams.getFirst("$skip");
		String filter = queryParams.getFirst("$filter");
		String orderBy = queryParams.getFirst("$orderby");
// TODO what are format and callback used for
//		String format = queryParams.getFirst("$format");
//		String callback = queryParams.getFirst("$callback");
		String skipToken = queryParams.getFirst("$skiptoken");
		String expand = queryParams.getFirst("$expand");
		String select = queryParams.getFirst("$select");
	      
		QueryInfo query = new QueryInfo(
				OptionsQueryParser.parseInlineCount(inlineCount),
				OptionsQueryParser.parseTop(top),
				OptionsQueryParser.parseSkip(skip),
				OptionsQueryParser.parseFilter(filter),
				OptionsQueryParser.parseOrderBy(orderBy),
				OptionsQueryParser.parseSkipToken(skipToken),
				null,
				OptionsQueryParser.parseExpand(expand),
				OptionsQueryParser.parseSelect(select));
		
		EntitiesResponse response = producer.getEntities(entitySetName, query);
		    
		CollectionResource<OEntity> cr = CommandHelper.createCollectionResource(entitySetName, response.getEntities());
		RESTResponse rr = new RESTResponse(Response.Status.OK, cr);
		return rr;
	}

}
