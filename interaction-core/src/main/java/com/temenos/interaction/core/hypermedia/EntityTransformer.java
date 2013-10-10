package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.util.HashMap;
import java.util.Map;

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
		if(entity instanceof Entity) {
			return transform((Entity) entity);
		} else {
			logger.error("Unable to transform entity: " + entity.toString());
		}
		return null;
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
				&& (entity instanceof Entity)) {
			return true;
		}
		return false;
	}

}
