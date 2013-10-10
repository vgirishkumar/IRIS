package com.temenos.interaction.core.entity;

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


/**
 * An Entity contains a set of properties.  
 */
public class Entity {

	private final String name;
	private final EntityProperties properties;
	
	public Entity(String name, EntityProperties properties) {
		this.name = name;
		this.properties = properties;
	}
	
	/**
	 * Returns the name of this entity.
	 * @return Entity name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the properties of this entity.
	 * @return Entity properties
	 */
	public EntityProperties getProperties() {
		return properties;
	}
}
