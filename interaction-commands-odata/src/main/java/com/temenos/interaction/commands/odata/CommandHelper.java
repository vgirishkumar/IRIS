package com.temenos.interaction.commands.odata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;

public class CommandHelper {
	private final static Logger logger = LoggerFactory.getLogger(CommandHelper.class);
	private static Pattern parameterPattern = Pattern.compile("\\{(.*?)\\}");

	/**
	 * Create an OData entity resource (entry)
	 * @param e OEntity
	 * @return entity resource
	 */
	public static EntityResource<OEntity> createEntityResource(OEntity e) {
		EntityResource<OEntity> oer = null;
		if(e != null && e.getEntityType() != null) {
			oer = new EntityResource<OEntity>(e.getEntityType().getName(), e) {};
		}
		else {
			oer = new EntityResource<OEntity>(null, e) {};			
		}
		return oer;
	}
	
	/**
	 * Create an OData collection resource (feed)
	 * @param entitySetName Entity set name
	 * @param entities List of OData entities
	 * @return collection resource
	 */
	public static CollectionResource<OEntity> createCollectionResource(String entitySetName, List<OEntity> entities) {
		List<EntityResource<OEntity>> subResources = new ArrayList<EntityResource<OEntity>>();
		for (OEntity entity : entities)
			subResources.add(createEntityResource(entity));
		return new CollectionResource<OEntity>(entitySetName, subResources) {};
	}

	/**
	 * Create an OData service document (atomsvc)
	 * @param metadata Edmx
	 * @return Service document
	 */
	public static EntityResource<EdmDataServices> createServiceDocumentResource(EdmDataServices metadata) {
		return new EntityResource<EdmDataServices>(metadata) {};	
	}

	/**
	 * Create an OData metadata document (edmx)
	 * @param metadata Edmx
	 * @return metadata resource
	 */
	public static MetaDataResource<EdmDataServices> createMetaDataResource(EdmDataServices metadata) {
		return new MetaDataResource<EdmDataServices>(metadata) {};	
	}
	
	/**
	 * Create an OEntityKey instance for the specified entity id
	 * @param edmDataServices edmDataServices
	 * @param entity Entity set name
	 * @param id Id
	 * @return An OEntityKey instance
	 * @throws Exception Error creating key 
	 */
	public static OEntityKey createEntityKey(EdmDataServices edmDataServices, String entitySetName, String id) throws Exception {
		//Lookup type of entity key (simple keys only)
		String keyType = null;
		EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(entitySetName);
		if(entitySet != null) {
			EdmEntityType entityType = entitySet.getType();
			List<String> keys = entityType.getKeys();
			if(keys.size() == 1) {
				EdmProperty prop = entityType.findDeclaredProperty(keys.get(0));
				if(prop != null && prop.getType() != null) {
					keyType = prop.getType().getFullyQualifiedTypeName();
				}
			}
		}
		assert(keyType != null) : "Should not be possible to get this far and find no key type";
		
		//Create an entity key
		OEntityKey key = null;
		try {
			if (keyType.equals("Edm.Int64")) {
				key = OEntityKey.parse(id);
			} else if(keyType.equals("Edm.Int32")) {
				key = OEntityKey.parse(id);
			} else if(keyType.equals("Edm.DateTime")) {
				key = OEntityKey.parse(id);
			} else if(keyType.equals("Edm.Time")) {
				key = OEntityKey.parse(id);
			} else if(keyType.equals("Edm.String")) {
				key = OEntityKey.parse(id);
			}
		} catch (Exception e) {
			logger.warn("Entity key type " + keyType + " is not supported by CommandHelper, trying OEntityKey.parse");
		}
		// could not parse the key, have one last attempt with OEntityKey create
		if (key == null) {
			try {
				if (keyType.equals("Edm.Int64")) {
					key = OEntityKey.create(Long.parseLong(id));
				} else if(keyType.equals("Edm.Int32")) {
					key = OEntityKey.create(Integer.parseInt(id));
				} else {
					key = OEntityKey.create(id);
				}					
			} catch (Exception e) {
				logger.error("OEntityKey.parse failed to parse id [" + id + "]");
			}
		}
		if (key == null)
			throw new Exception("Entity key type " + id + " is not supported.");
		return key;
	}
	
	/**
	 * Returns the entity set holding the specified entity (type) name
	 * @param entityName entity type name
	 * @param edmDataServices metadata
	 * @return entity set
	 * @throws Exception if entity set cannot be found
	 */
	public static EdmEntitySet getEntitySet(String entityName, EdmDataServices edmDataServices) throws Exception {
		//Find entity type
		EdmType entityType = null;
		Iterator<EdmSchema> itSchema = edmDataServices.getSchemas().iterator();
		while(entityType == null && itSchema.hasNext()) {
			entityType = edmDataServices.findEdmEntityType(itSchema.next().getNamespace() + "." + entityName);
		}
		if(entityType == null || !(entityType instanceof EdmEntityType)) {
			throw new Exception("Entity type does not exist");
		}
		
		//Find entity set
		EdmEntitySet entitySet = edmDataServices.getEdmEntitySet((EdmEntityType) entityType);
		if (entitySet == null) {
			throw new Exception("Entity set does not exist");
		}
		return entitySet;
	}
	
	/**
	 * Returns a view action property
	 * This method will try to replace template parameters "{param}"
	 * with query parameters if available 
	 * @param ctx interaction context
	 * @param property action property
	 * @return property value
	 */
	public static String getViewActionProperty(InteractionContext ctx, String property) {
		Properties properties = ctx.getCurrentState().getViewAction().getProperties();
		String prop = null;
		if(properties != null && properties.get(property) != null) {
			prop = (String) properties.get(property);		//e.g. fld eq '{id}'
			Matcher m = parameterPattern.matcher(prop);
			while(m.find()) {
				String param = m.group(1);	//e.g. id
				if(ctx.getQueryParameters().containsKey(param)) {
					prop = prop.replaceAll("\\{" + param + "\\}", ctx.getQueryParameters().getFirst(param));
				}
				else if(ctx.getPathParameters().containsKey(param)) {
					prop = prop.replaceAll("\\{" + param + "\\}", ctx.getPathParameters().getFirst(param));
				}
			}
		}
		return prop;
	}
}
