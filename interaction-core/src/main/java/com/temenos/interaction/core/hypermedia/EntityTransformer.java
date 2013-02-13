package com.temenos.interaction.core.hypermedia;

import java.util.HashMap;
import java.util.Map;

import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;

/**
 * Implements transformations from Entity objects
 * @see {@link Transformer}
 */
public class EntityTransformer implements Transformer {
	private final Logger logger = LoggerFactory.getLogger(EntityTransformer.class);
	
	/**
	 * @precondition entity not null
	 */
	@Override
	public Map<String, Object> transform(Object entity) {
		if(entity instanceof OEntity) {
			return transform((OEntity) entity);
		}
		else if(entity instanceof Entity) {
			return transform((Entity) entity);
		}
		else if(!(entity instanceof EdmDataServices)) {		//Cater for ServiceDocument
			logger.error("Unable to transform entity: " + entity.toString());
		}
		return null;
	}
	
	private Map<String, Object> transform(OEntity entity) {
		assert(entity != null);
		Map<String, Object> map = new HashMap<String, Object>();
		for(OProperty<?> prop : entity.getProperties()) {
			String name = prop.getName();
			Object value = prop.getValue();
			map.put(name, value);				
		}
		return map;
	}
	
	private Map<String, Object> transform(Entity entity) {
		assert(entity != null);
		Map<String, Object> map = new HashMap<String, Object>();
		EntityProperties entityProperties = entity.getProperties();
		Map<String, EntityProperty> properties = entityProperties.getProperties();
				
		for (Map.Entry<String, EntityProperty> property : properties.entrySet()) {
			String name = property.getKey(); 
			EntityProperty propertyValue = (EntityProperty) property.getValue();
	   		map.put(name, propertyValue.getValue());	
		}
		return map;
	}

	/**
	 * This transformer will accept any {@link Entity} and push its {@link EntityProperties} 
	 * into the returned Map.
	 */
	@Override
	public boolean canTransform(Object entity) {
		if (entity != null 
				&& (entity instanceof Entity || entity instanceof OEntity)) {
			return true;
		}
		return false;
	}

}
