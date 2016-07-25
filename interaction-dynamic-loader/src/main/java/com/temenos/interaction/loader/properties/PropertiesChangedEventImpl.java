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

import com.temenos.interaction.core.loader.PropertiesChangedEvent;
import com.temenos.interaction.core.loader.PropertiesEvent;
import com.temenos.interaction.core.loader.PropertiesEventVisitor;

public class PropertiesChangedEventImpl implements PropertiesEvent<Resource>, PropertiesChangedEvent<Resource> {

	final ReloadableProperties<Resource> target;
	final Resource resource;
	final Properties newProperties;
	
	public PropertiesChangedEventImpl(ReloadableProperties<Resource> target, Resource resource, Properties newProperties) {
		this.target = target;
		this.resource = resource;
		this.newProperties = newProperties; 
	}

	/* (non-Javadoc)
	 * @see com.temenos.interaction.loader.properties.PropertiesChangedEvent#getResource()
	 */
	@Override
	public Resource getResource() {
		return resource;
	}

	public ReloadableProperties<Resource> getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see com.temenos.interaction.loader.properties.PropertiesChangedEvent#getNewProperties()
	 */
	@Override
	public Properties getNewProperties() {
		return newProperties;
	}

	/* (non-Javadoc)
	 * @see com.temenos.interaction.loader.properties.PropertiesChangedEvent#accept(com.temenos.interaction.core.loader.PropertiesEventVisitor)
	 */
	@Override
	public void accept(PropertiesEventVisitor<Resource> visitor) {
		visitor.visit(this);		
	}
	
}
