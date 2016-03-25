package com.temenos.useragent.generic;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.temenos.useragent.generic.internal.EntityWrapper;

/**
 * This class maps {@link Entity entities} against some of their attributes to
 * provide convenient access.
 * 
 * @author ssethupathi
 *
 */
public class Entities {

	private List<EntityWrapper> entities = new ArrayList<EntityWrapper>();
	private boolean entitiesNotMapped = false;
	private Map<String, EntityWrapper> entitiesById = new HashMap<String, EntityWrapper>();

	public Entities(List<EntityWrapper> entities) {
		for (EntityWrapper entity : entities) {
			this.entities.add(entity);
		}
	}

	/**
	 * Returns the {@link EntityWrapper entity} for the supplied <i>id</i>.
	 * 
	 * @param id
	 * @return entity
	 */
	public EntityWrapper byId(String id) {
		if (entitiesNotMapped) {
			mapEntities();
		}
		return entitiesById.get(id);
	}

	/**
	 * Returns all {@link Entity entities} from this mapping.
	 * 
	 * @return entities
	 */
	public List<? extends Entity> all() {
		return entities;
	}

	private void mapEntities() {
		for (EntityWrapper entity : entities) {
			entitiesById.put(entity.id(), entity);
		}
	}
}
