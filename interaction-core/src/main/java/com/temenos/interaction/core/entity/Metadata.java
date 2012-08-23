package com.temenos.interaction.core.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds metadata information about resource entities.
 */
public class Metadata  {
	//Map of <Entity name, Entity metadata>
	private Map<String, EntityMetadata> entitiesMetadata = new HashMap<String, EntityMetadata>();

	/**
	 * Returns the metadata of the specified entity
	 * @param entityName Entity name
	 * @return entity metadata
	 */
	public EntityMetadata getEntityMetadata(String entityName) {
		return entitiesMetadata.get(entityName);
	}
	
	/**
	 * Sets the metadata for the specified entity
	 * @param entityName Entity name
	 * @param entityMetadata Entity metadata
	 */
	public void setEntityMetadata(EntityMetadata entityMetadata) {
		entitiesMetadata.put(entityMetadata.getEntityName(), entityMetadata);
	}
	
	/**
	 * Returns a map of <entity name, entity metadata> 
	 * @return entities metadata map
	 */
	public Map<String, EntityMetadata> getEntitiesMetadata() {
		return entitiesMetadata;
	}
}
