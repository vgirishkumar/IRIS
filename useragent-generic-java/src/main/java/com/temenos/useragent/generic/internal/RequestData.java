package com.temenos.useragent.generic.internal;

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

import com.temenos.useragent.generic.http.HttpHeader;

/**
 * Defines the data part of the http request.
 * 
 * @author ssethupathi
 *
 */
public interface RequestData {

	/**
	 * Returns the header part of the request.
	 * 
	 * @return header
	 */
	HttpHeader header();

	/**
	 * Returns the payload part of the request which is an entity.
	 * 
	 * @return payload entity
	 */
	EntityWrapper entity();
}
