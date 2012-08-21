package com.temenos.interaction.sdk.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information about the entity model
 */
public class EntityModel {

	private List<EMEntity> entities = new ArrayList<EMEntity>();
	
	public EntityModel() {
	}
	
	public void addEntity(EMEntity entity) {
		entities.add(entity);
	}
	
	public List<EMEntity> getEntities() {
		return entities;
	}
}
