package com.temenos.interaction.commands.odata;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.exceptions.ODataProducerException;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;

public class CreateEntityCommand extends AbstractODataCommand implements InteractionCommand {
	private final Logger logger = LoggerFactory.getLogger(CreateEntityCommand.class);

	private ODataProducer producer;

	public CreateEntityCommand(ODataProducer producer) {
		this.producer = producer;
	}

	/* Implement InteractionCommand interface */
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert(ctx != null);
		assert(ctx.getCurrentState() != null);
		assert(ctx.getCurrentState().getEntityName() != null && !ctx.getCurrentState().getEntityName().equals(""));
		assert(ctx.getResource() != null);
		
		// create the entity
		OEntity entity = null;
		try {
			entity = ((EntityResource<OEntity>) ctx.getResource()).getEntity();
		} catch (ClassCastException cce) {
			entity = create(((EntityResource<Entity>) ctx.getResource()).getEntity());
		}
		String entityName = getEntityName(ctx);
		
		logger.debug("Creating entity for " + entityName);
		OEntity oEntity;
		try {
			EntityResponse er = producer.createEntity(entityName, entity);
			oEntity = er.getEntity();
		}
		catch(ODataProducerException ope) {
			logger.debug("Failed to create entity [" + entityName + "]: " + ope.getMessage());
			throw new InteractionException(ope.getHttpStatus(), ope.getMessage());
		}
		catch(Exception e) {
			logger.debug("Error while creating entity [" + entityName + "]: " + e.getMessage());
			throw new InteractionException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		ctx.setResource(CommandHelper.createEntityResource(oEntity));
		return Result.SUCCESS;
	}

	// TODO move this transformation up to where we have all the metadata, note the hacked hardcoded "Id"
	private OEntity create(Entity entity) throws InteractionException {
		try {
			assert(entity != null);
			assert(entity.getName() != null);
			EdmEntityType entityType = (EdmEntityType) producer.getMetadata().findEdmEntityType(entity.getName());
			EdmEntitySet entitySet = producer.getMetadata().getEdmEntitySet(entityType);
					
			String id = null;
			EntityProperties entityProps = entity.getProperties();
			List<OProperty<?>> eProps = new ArrayList<OProperty<?>>();
			for (String propKey : entityProps.getProperties().keySet()) {
				EntityProperty prop = entityProps.getProperty(propKey);
				if (prop.getName().equals("Id")) {
					id = prop.getValue().toString();
				} else if (entityType.findProperty(prop.getName()) != null) {
					EdmProperty eProp = entityType.findProperty(prop.getName());
					if (eProp.getType().equals(EdmSimpleType.STRING)) {
						eProps.add(OProperties.string(prop.getName(), prop.getValue().toString()));
					} else if (eProp.getType().equals(EdmSimpleType.INT32)) {
						eProps.add(OProperties.int32(prop.getName(), new Integer(prop.getValue().toString())));
					}
				}
			}
//			assert(id != null) : "Id property not found";
			OEntityKey eKey = null;
			if (id != null) {
				eKey = CommandHelper.createEntityKey(producer.getMetadata(), entity.getName(), id);				
				return OEntities.create(entitySet, eKey, eProps, null);
			} else {
				return OEntities.createRequest(entitySet, eProps, null);
			}
		} catch (Exception e) {
			throw new InteractionException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    }
}
