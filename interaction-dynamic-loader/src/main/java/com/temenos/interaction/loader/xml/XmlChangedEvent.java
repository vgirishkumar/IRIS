package com.temenos.interaction.loader.xml;

/*
 * #%L
 * interaction-springdsl
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import org.springframework.core.io.Resource;

import com.temenos.interaction.loader.FileEvent;

public class XmlChangedEvent implements FileEvent {
	private final Resource resource;
		
	/**
	 * @param resource
	 */
	public XmlChangedEvent(Resource resource) {
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}
}
