package com.temenos.interaction.core.media.xml.jaxb;

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

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperty;

public class EntityAdapter extends XmlAdapter<XMLEntity, Entity>{

	@Override
	public XMLEntity marshal(Entity entity) throws Exception {
		XMLEntity xmlEntity = new XMLEntity();
		xmlEntity.name = entity.getName();
		Map<String, EntityProperty> properties = entity.getProperties().getProperties();
		for (String propertyName : properties.keySet()) {
			EntityProperty property = properties.get(propertyName);
			XMLEntityProperty xmlEntityProperty = new XMLEntityProperty();
			xmlEntityProperty.name = property.getName();
			xmlEntity.properties.put(xmlEntityProperty.name, xmlEntityProperty);
		}
		return xmlEntity;
	}

	@Override
	public Entity unmarshal(XMLEntity entity) throws Exception {
		return null;
	}

}
