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

package com.temenos.interaction.authorization.command;

import com.temenos.interaction.core.command.InteractionAttribute;

/**
 * This enum defines InteractionContext attributes used by the authorization package.
 */
public enum AuthorizationAttributes implements InteractionAttribute {

	FILTER_DONE_ATTRIBUTE("filterDone", Boolean.class), 
	SELECT_DONE_ATTRIBUTE("selectDone", Boolean.class);

	private String name;
	private Class<?> type;

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	AuthorizationAttributes(String name, Class<?> type) {
		init(name, type);
	}

	public void init(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
}
