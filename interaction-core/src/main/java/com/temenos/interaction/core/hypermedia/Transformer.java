package com.temenos.interaction.core.hypermedia;

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


import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

/**
 * A transformer is used to map an entity to a form usable by
 * a {@link UriBuilder} or an interaction {@link Provider}
 * @author aphethean
 */
public interface Transformer {

	/**
	 * Transform an entity to a map where the key is the target
	 * element name and the value is the source entity element.
	 * @param entity
	 * @return
	 */
	public Map<String, Object> transform(Object entity);
	
	/**
	 * Used to check whether this object can handle the transformation of the supplied
	 * entity to a Map<String, Object>
	 * @param entity
	 * @return
	 */
	public boolean canTransform(Object entity);
}
