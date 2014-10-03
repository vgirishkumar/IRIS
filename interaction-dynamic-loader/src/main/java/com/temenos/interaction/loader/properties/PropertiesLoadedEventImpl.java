package com.temenos.interaction.loader.properties;

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

import java.util.Properties;

import org.springframework.core.io.Resource;

import com.temenos.interaction.core.loader.PropertiesEvent;
import com.temenos.interaction.core.loader.PropertiesEventVisitor;
import com.temenos.interaction.core.loader.PropertiesLoadedEvent;

public class PropertiesLoadedEventImpl implements PropertiesEvent, PropertiesLoadedEvent {

	private final ReloadableProperties target;
	private final Resource resource;
	private final Properties newProperties;
	
	public PropertiesLoadedEventImpl(ReloadableProperties target, Resource resource, Properties newProperties) {
		this.target = target;
		this.resource = resource;
		this.newProperties = newProperties;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	public ReloadableProperties getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see com.temenos.interaction.loader.properties.PropertiesLoadedEvent#accept(com.temenos.interaction.core.loader.PropertiesEventVisitor)
	 */
	@Override
	public void accept(PropertiesEventVisitor visitor) {
		visitor.visit(this);		
	}

	/* (non-Javadoc)
	 * @see com.temenos.interaction.loader.properties.PropertiesLoadedEvent#getNewProperties()
	 */
	@Override
	public Properties getNewProperties() {
		return newProperties;
	}
}
