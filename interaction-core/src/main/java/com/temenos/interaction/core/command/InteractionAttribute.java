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
 * This interface for enums that describe attributes passed in the
 * InteractionContext.
 * 
 * Ideally we would like to declare the method bodies here. However enums cannot
 * extend from classes. So the code is duplicated in each enum that implements this interface.
 */

public interface InteractionAttribute {

	/**
	 * Common constructor
	 */
	public void init(String name, Class<?> type);

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @return the type
	 */
	public Class<?> getType();
}