package com.temenos.interaction.commands.odata;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class GETLinkEntityCommand implements InteractionCommand {

	// Command configuration
	private String entity;
	private String linkEntity;
	private String linkProperty;
	
	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	private EdmEntitySet entitySet;
	private Iterable<EdmEntityType> entityTypes;

	public GETLinkEntityCommand(String entity, String linkProperty, String linkEntity, ODataProducer producer) {
		this.entity = entity;
		this.linkProperty = linkProperty;
		this.linkEntity = linkEntity;
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
		this.entitySet = edmDataServices.getEdmEntitySet(entity);
		this.entityTypes = edmDataServices.getEntityTypes();
		assert(entity.equals(entitySet.getName()));
	}
	
	protected ODataProducer getProducer() {
		return producer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		
		//Create entity key (simple types only)
		OEntityKey key;
		try {
			key = CommandHelper.createEntityKey(entityTypes, entity, ctx.getId());
		} catch(Exception e) {
			return Result.FAILURE;
		}
		
		//Get the entity
		EntityResponse er = getProducer().getEntity(entity, key, null);
		if(er != null) {
			//Create the key to the link entity
			OProperty<String> linkOProperty = (OProperty<String>) er.getEntity().getProperty(linkProperty);
			try {
				key = CommandHelper.createEntityKey(entityTypes, linkEntity, linkOProperty.getValue());
			} catch(Exception e) {
				return Result.FAILURE;
			}
			
			//Get the link Entity
			EntityResponse linkEr = getProducer().getEntity(linkEntity, key, null);
			OEntity linkOEntity = linkEr.getEntity();
			
			EntityResource<OEntity> oer = CommandHelper.createEntityResource(linkOEntity);
			ctx.setResource(oer);		
			return Result.SUCCESS;
		}
		else {
			return Result.FAILURE;
		}
	}

	@Override
	public String getMethod() {
		return HttpMethod.GET;
	}
}
