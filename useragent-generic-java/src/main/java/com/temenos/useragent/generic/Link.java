package com.temenos.useragent.generic;

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


import com.temenos.useragent.generic.internal.Payload;

/**
 * Defines a hypermedia link.
 * 
 * @author ssethupathi
 *
 */
public interface Link {

	/**
	 * Returns the title attribute value of this link.
	 * 
	 * @return title
	 */
	String title();

	/**
	 * Returns the href attribute value of this link.
	 * 
	 * @return href
	 */
	String href();

	/**
	 * Returns the rel attribute value of this link.
	 * 
	 * @return rel
	 */
	String rel();

	/**
	 * Returns the base url for this link.
	 * 
	 * @return base url
	 */
	String baseUrl();

	/**
	 * Returns the id of this link.
	 * 
	 * @return id
	 */
	String id();

	/**
	 * Returns whether or not this link has embedded payload.
	 * 
	 * @return true if this link has embedded payload, false otherwise
	 */
	boolean hasEmbeddedPayload();

	/**
	 * Returns the embedded payload.
	 * 
	 * @return embedded payload or null if this link has no embedded payload
	 */
	Payload embedded();

}
