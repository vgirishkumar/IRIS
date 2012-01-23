package com.temenos.interaction.commands.odata;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class GETEntitiesCommand implements ResourceGetCommand {

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
	}

	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {

		String inlineCount = queryParams.getFirst("$inlinecount");
		String top = queryParams.getFirst("$top");
		String skip = queryParams.getFirst("$skip");
		String filter = queryParams.getFirst("$filter");
		String orderBy = queryParams.getFirst("$orderby");
		String format = queryParams.getFirst("$format");
		String callback = queryParams.getFirst("$callback");
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
		    
		return null;
	}

}
