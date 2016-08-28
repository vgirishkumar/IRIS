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

import java.util.List;

import com.temenos.useragent.generic.Link;

/**
 * Defines the payload which is received as in the body after a Http method
 * execution.
 * 
 * @author ssethupathi
 *
 */
public interface Payload {

	/**
	 * Defines whether or no this payload is a collection of items.
	 * 
	 * @return true if it's collection, false for single item
	 */
	boolean isCollection();

	/**
	 * Returns the links available in the payload
	 * 
	 * @return links
	 */
	List<Link> links();

	/**
	 * Returns the entity representing the single item in the payload. If this
	 * payload represents a collection then this method would return null.
	 * 
	 * @see #isCollection()
	 * @see #entities()
	 * 
	 * @return single item in the payload
	 */
	EntityWrapper entity();

	/**
	 * Returns the entities representing the collection of items in the payload.
	 * If this payload represents an item then this method would return an empty
	 * list.
	 * 
	 * @see #isCollection()
	 * @see #entity()
	 * 
	 * @return all items in the payload
	 */
	List<EntityWrapper> entities();
}
