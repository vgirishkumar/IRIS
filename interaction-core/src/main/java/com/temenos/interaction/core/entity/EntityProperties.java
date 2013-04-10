package com.temenos.interaction.core.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an unordered map of entity properties 
 */
public class EntityProperties  {
	
	private Map<String, EntityProperty> properties = new HashMap<String, EntityProperty>();

	/**
	 * Gets the specified entity property.
	 * @param name Entity property name
	 * @return Entity property
	 */
	public EntityProperty getProperty(String name) {
		return properties.get(name);
	}
	
	/**
	 * Returns a list of entity properties
	 * @return
	 */
	public Map<String, EntityProperty> getProperties() {
		return properties;
	}
	
	/**
	 * Puts the specified entity property.
	 * @param property Entity property
	 */
	public void setProperty(EntityProperty property) {
		properties.put(property.getName(), property);
	}
	
	/**
	 * Return a comma-separated list of property-value pairs
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(String prop : properties.keySet()) {
			if(str.length() > 0) {
				str.append(", ");
			}
			str.append(prop + " = " + properties.get(prop));
		}
		return str.toString();
	}
}
