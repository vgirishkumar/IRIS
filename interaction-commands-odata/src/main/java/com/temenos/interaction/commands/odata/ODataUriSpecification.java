package com.temenos.interaction.commands.odata;

/*
 * #%L
 * interaction-commands-odata
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


import com.temenos.interaction.core.hypermedia.UriSpecification;

public class ODataUriSpecification {

	public final static String NAME = "ODataUriSpecification";
	
	public final static String ENTITY_URI_TYPE = "Entity";
	public final static String ENTITYSET_URI_TYPE = "EntitySet";
	public final static String NAVPROPERTY_URI_TYPE = "NavProperty";
	
	public UriSpecification getTemplate(String resourcePath) {
		return new UriSpecification(resourcePath, resourcePath);
	}
	
	public UriSpecification getTemplate(String resourcePathPrefix, String type) {
		assert(type != null);  // would be a bug in IRIS core
		if (type.equals(ENTITY_URI_TYPE)) {
			return new UriSpecification(resourcePathPrefix, resourcePathPrefix + "({id})");
		} else if (type.equals(ENTITYSET_URI_TYPE)) {
			return new UriSpecification(resourcePathPrefix, resourcePathPrefix);
		} else if (type.equals(NAVPROPERTY_URI_TYPE)) {
			return new UriSpecification(resourcePathPrefix, resourcePathPrefix + "({id})/{navproperty}");
		}
		throw new IllegalArgumentException("Could not produce template for supplied URI type [" + type + "]");
	}

	public String toString() {
		return NAME;
	}
	
}
