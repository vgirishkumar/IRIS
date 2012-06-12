package com.temenos.interaction.commands.odata.consumer;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.resource.ServiceDocumentResource;

public class CommandHelper {
	private final static Logger logger = LoggerFactory.getLogger(CommandHelper.class);

	/**
	 * Create an OData entity resource (entry)
	 * @param e OEntity
	 * @return entity resource
	 */
	public static<OEntity> EntityResource<OEntity> createEntityResource(OEntity e) {
		return new EntityResource<OEntity>(e) {};	
	}
	
	/**
	 * Create an OData collection resource (feed)
	 * @param entitySetName Entity set name
	 * @param entities List of OData entities
	 * @return collection resource
	 */
	public static<OEntity> CollectionResource<OEntity> createCollectionResource(String entitySetName, List<OEntity> entities) {
		List<EntityResource<OEntity>> subResources = new ArrayList<EntityResource<OEntity>>();
		for (OEntity entity : entities) {
			subResources.add(createEntityResource(entity));
		}
		return new CollectionResource<OEntity>(entitySetName, subResources) {};
	}

	/**
	 * Create an OData service document (atomsvc)
	 * @param metadata Edmx
	 * @return Service document
	 */
	public static<EdmDataServices> ServiceDocumentResource<EdmDataServices> createServiceDocumentResource(EdmDataServices metadata) {
		return new ServiceDocumentResource<EdmDataServices>(metadata) {};	
	}

	/**
	 * Create an OData metadata document (edmx)
	 * @param metadata Edmx
	 * @return metadata resource
	 */
	public static<EdmDataServices> MetaDataResource<EdmDataServices> createMetaDataResource(EdmDataServices metadata) {
		return new MetaDataResource<EdmDataServices>(metadata) {};	
	}
	
	/**
	 * Create an OEntityKey instance for the specified entity id
	 * @param entityTypes List of entity types
	 * @param entity Entity name
	 * @param id Id
	 * @return An OEntityKey instance
	 * @throws Exception Error creating key 
	 */
	public static OEntityKey createEntityKey(Iterable<EdmEntityType> entityTypes, String entity, String id) throws Exception {
		//Lookup type of entity key (simple keys only)
		String keyType = null;
		for (EdmEntityType entityType : entityTypes) {
			if (entityType.getName().equals(entity)) {
				List<String> keys = entityType.getKeys();
				if(keys.size() == 1) {
					EdmProperty prop = entityType.findDeclaredProperty(keys.get(0));
					if(prop != null && prop.getType() != null) {
						keyType = prop.getType().getFullyQualifiedTypeName();
					}
					break;
				}
			}
		}		
		
		//Create an entity key
		OEntityKey key = null;
		try {
			if(keyType.equals("Edm.Int64")) {
				key = OEntityKey.create(Long.parseLong(id));
			}
			else if(keyType.equals("Edm.Int32")) {
				key = OEntityKey.create(Integer.parseInt(id));
			}
			else if(keyType.equals("Edm.DateTime")) {
				key = OEntityKey.parse(id);
			}
			else if(keyType.equals("Edm.Time")) {
				key = OEntityKey.parse(id);
			}
			else if(keyType.equals("Edm.String")) {
				key = OEntityKey.create(id);
			}
		} catch (Exception e) {
			logger.warn("Entity key type " + keyType + " is not supported by CommandHelper, trying OEntityKey.parse");
		}
		// could not parse the key, have one last attempt with OEntityKey parse
		if (key == null) {
			try {
				key = OEntityKey.parse(id);
			} catch (Exception e) {
				logger.error("OEntityKey.parse failed to parse id [" + id + "]");
			}
		}
		if (key == null)
			throw new Exception("Entity key type " + id + " is not supported.");
		return key;
	}
}
