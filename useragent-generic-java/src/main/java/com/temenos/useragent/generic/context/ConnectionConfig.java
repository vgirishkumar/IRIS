package com.temenos.useragent.generic.context;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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
 * Defines configuration required for the connection used in the interaction.
 * 
 * @author ssethupathi
 *
 */
public interface ConnectionConfig {

	public static final String ENDPOINT_URI = "URI";
	public static final String USER_NAME = "USERNAME";
	public static final String PASS_WORD = "PASSWORD";
	public static final String SERVICE_ROOT = "COMPANY";

	/**
	 * Gets the value for a given property name.
	 * 
	 * @param propertyName
	 * @return value
	 */
	public String getValue(String propertyName);
}
