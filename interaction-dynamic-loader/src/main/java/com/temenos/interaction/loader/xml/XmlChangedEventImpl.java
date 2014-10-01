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

import com.temenos.interaction.core.loader.FileEvent;
import com.temenos.interaction.core.loader.XmlChangedEvent;

public class XmlChangedEventImpl implements FileEvent<Resource>, XmlChangedEvent {
	private final Resource resource;
		
	/**
	 * @param resource
	 */
	public XmlChangedEventImpl(Resource resource) {
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see com.temenos.interaction.loader.xml.XmlChangedEvent#getResource()
	 */
	@Override
	public Resource getResource() {
		return resource;
	}
}
