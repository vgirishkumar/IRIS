package com.temenos.interaction.commands.odata;

/*
 * #%L
 * interaction-commands-odata
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

import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.hypermedia.Transformer;

/**
 * Implements transformations from OData4J OEntity objects.
 * @see {@link Transformer}
 * @author aphethean
 */
public class OEntityTransformer implements Transformer {
	private final Logger logger = LoggerFactory.getLogger(OEntityTransformer.class);
	
	/**
	 * @precondition entity not null
	 * @precondition entity of type {@link OEntity}
	 * @postcondition return a map populated from supplied entity
	 */
	@Override
	public Map<String, Object> transform(Object entity) {
		if(entity instanceof OEntity) {
			return transform((OEntity) entity);
		} else {
			logger.error("Unable to transform entity: " + entity.toString());
			return null;
		}
	}
	
	private Map<String, Object> transform(OEntity entity) {
		assert(entity != null);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			OEntity oentity = (OEntity) entity;
			for (OProperty<?> property : oentity.getProperties()) {
				map.put(property.getName(), property.getValue());				
			}
		} catch (RuntimeException e) {
			logger.error("Error transforming OEntity to map", e);
			throw e;
		}
		return map;
	}

	/**
	 * This transformer will accept any {@link OEntity} and push its {@link OProperties} 
	 * into the returned Map.
	 */
	@Override
	public boolean canTransform(Object entity) {
		if (entity != null && entity instanceof OEntity) {
			return true;
		}
		return false;
	}
	
}