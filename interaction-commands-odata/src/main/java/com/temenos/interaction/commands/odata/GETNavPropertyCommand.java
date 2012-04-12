package com.temenos.interaction.commands.odata;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.BaseResponse;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.PropertyResponse;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class GETNavPropertyCommand implements ResourceGetCommand {
	private final Logger logger = LoggerFactory.getLogger(GETNavPropertyCommand.class);

	// Command configuration
	private String entity;
	private String navProperty;
	
	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;
	private Iterable<EdmEntityType> entityTypes;

	public GETNavPropertyCommand(String entity, String navProperty, ODataProducer producer) {
		this.entity = entity;
		this.navProperty = navProperty;
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entity);
		this.entityTypes = edmDataServices.getEntityTypes();
		assert(entity.equals(entitySet.getName()));
	}

	/* Implement ResourceGetCommand */
	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		//Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entity, id);
		} catch(Exception e) {
			return new RESTResponse(Response.Status.NOT_ACCEPTABLE, null);
		}

		QueryInfo query = null;
		if (queryParams != null) {
			String inlineCount = queryParams.getFirst("$inlinecount");
			String top = queryParams.getFirst("$top");
			String skip = queryParams.getFirst("$skip");
			String filter = queryParams.getFirst("$filter");
			String orderBy = queryParams.getFirst("$orderby");
	// TODO what are format and callback used for
//			String format = queryParams.getFirst("$format");
//			String callback = queryParams.getFirst("$callback");
			String skipToken = queryParams.getFirst("$skiptoken");
			String expand = queryParams.getFirst("$expand");
			String select = queryParams.getFirst("$select");

			query = new QueryInfo(
					OptionsQueryParser.parseInlineCount(inlineCount),
					OptionsQueryParser.parseTop(top),
					OptionsQueryParser.parseSkip(skip),
					OptionsQueryParser.parseFilter(filter),
					OptionsQueryParser.parseOrderBy(orderBy),
					OptionsQueryParser.parseSkipToken(skipToken),
					null,
					OptionsQueryParser.parseExpand(expand),
					OptionsQueryParser.parseSelect(select));
		}

		BaseResponse response = producer.getNavProperty(entity, key, navProperty, query);

		if (response instanceof PropertyResponse) {
			logger.error("We don't currently support the ability to get an item property");
		} else if (response instanceof EntityResponse) {
        	OEntity oe = ((EntityResponse) response).getEntity();
    		return new RESTResponse(Response.Status.OK, CommandHelper.createEntityResource(oe));
        } else if (response instanceof EntitiesResponse) {
        	List<OEntity> entities = ((EntitiesResponse) response).getEntities();
    		return new RESTResponse(Response.Status.OK, CommandHelper.createCollectionResource(entity, entities));
    	} else {
			logger.error("Other type of unsupported response from ODataProducer.getNavProperty");
        }

		return new RESTResponse(Response.Status.NOT_ACCEPTABLE, null);
	}

}
