/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
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

package com.temenos.interaction.core.command;

/**
 * This enum defines InteractionContext attributes which are not related to a specific package.
 * 
 * Attributes related to individual packages are described in similar enums within the packages.
 */

public enum CommonAttributes implements InteractionAttribute {

	// Currently there are no common attributes. When there are implement as, of example, ...
	TEST("tests", Boolean.class);
	
	private String name;
	private Class<?> type;
	
	CommonAttributes(String name, Class<?> type) {
		init(name, type);
	}
	
	public void init(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
