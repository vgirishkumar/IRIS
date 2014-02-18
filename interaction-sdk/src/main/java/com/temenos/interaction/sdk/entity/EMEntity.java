package com.temenos.interaction.sdk.entity;

/*
 * #%L
 * interaction-sdk
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds information about an entity
 */
public class EMEntity {
	private String name;
	private Map<String, EMProperty> properties = new HashMap<String, EMProperty>();

	public EMEntity(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this entity.
	 * @return entity name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns all the properties belong to this entity.
	 * @return properties
	 */
	public Collection<EMProperty> getProperties() {
		return properties.values();
	}

	/**
	 * Adds a property to this entity.
	 * @param property
	 */
	public void addProperty(EMProperty property) {
		properties.put(property.getName(), property);
	}

	/**
	 * Returns <i>true</i> if this entity contains a property with the property
	 * name.
	 * 
	 * @param propertyName
	 * @return true if this entity contains the property, false otherwise
	 */
	public boolean contains(String propertyName) {
		return properties.containsKey(propertyName);
	}

	/**
	 * Returns the {@link EMProperty} instance for the property name. If this entity does not
	 * contain a property for the property name then <i>null</i> is returned.
	 * 
	 * @param propertyName
	 * @return property
	 */
	public EMProperty getProperty(String propertyName) {
		return properties.get(propertyName);
	}
}
