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
 * An Entity property class can hold either simple or complex types.    
 */
public class EntityProperty {

	private final String name;
	private final Object value;
	
	public EntityProperty(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the name of this property.
	 * @return property name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the value of this property.
	 * @return property value
	 */
	public Object getValue() {
		return value;
	}
}
