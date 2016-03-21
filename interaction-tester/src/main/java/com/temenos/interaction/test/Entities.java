package com.temenos.interaction.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.temenos.interaction.test.internal.EntityWrapper;

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
