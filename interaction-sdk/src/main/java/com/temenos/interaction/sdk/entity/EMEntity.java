package com.temenos.interaction.sdk.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information about an entity
 */
public class EMEntity {
	private String name;
	private List<EMProperty> properties = new ArrayList<EMProperty>();

	public EMEntity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<EMProperty> getProperties() {
		return properties;
	}

	public void addProperty(EMProperty property) {
		properties.add(property);
	}
}
