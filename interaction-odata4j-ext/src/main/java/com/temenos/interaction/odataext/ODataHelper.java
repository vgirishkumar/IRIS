package com.temenos.interaction.odataext;

import java.util.Iterator;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



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


/**
 * Helper class which can be used by any OData dependent project.
 * 
 * @author sjunejo
 *
 */
public class ODataHelper {

	private final static Logger logger = LoggerFactory.getLogger(ODataHelper.class);
	
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
			throw new Exception(String.format("Entity type %s does not exist", entityName));
		}
		
		//Find entity set
		EdmEntitySet entitySet = null;
		try {
			entitySet = edmDataServices.getEdmEntitySet((EdmEntityType) entityType);
		} catch (Exception e) {
			logger.debug("Entity set does not exist for " + entityName, e);
		}
		return entitySet;
	}
	
}
