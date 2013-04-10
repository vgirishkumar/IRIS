package com.temenos.interaction.commands.mule;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;

public class EntityXMLReader {
	private final static Logger logger = LoggerFactory.getLogger(EntityXMLReader.class);

	private XMLInputFactory inputFactory = XMLInputFactory.newInstance();

	Entity toEntity(InputStream in) {
		Entity entity = null;
		try {
			XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
			String entityName = null;
			EntityProperties properties = null;
			while (streamReader.hasNext()) {
				streamReader.next();

				if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
					String elementName = streamReader.getLocalName();
					entityName = elementName;
					properties = parseProperties(streamReader, elementName);
				}
			}

			if (entityName != null) {
				entity = new Entity(entityName, properties);
			}
		} catch (XMLStreamException e) {
			logger.error("An unexpected error occurred while parsing Entity xml", e);
		}
		return entity;
	}

	private EntityProperties parseProperties(XMLStreamReader streamReader,
			String parentElement) throws XMLStreamException {
		EntityProperties properties = new EntityProperties();
		while (streamReader.hasNext()) {
			streamReader.next();

			if (streamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(parentElement)) {
					break;
				}
			} else if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
				String elementName = streamReader.getLocalName();
				EntityProperty property = new EntityProperty(elementName, streamReader.getElementText());
				properties.setProperty(property);
			}

		}
		return properties;
	}

}
