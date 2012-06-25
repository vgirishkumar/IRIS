package com.temenos.interaction.core.media.xml.jaxb;

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
