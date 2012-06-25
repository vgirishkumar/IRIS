package com.temenos.interaction.core.media.xml.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XMLEntity {

	@XmlAttribute
	public String name; 

	@XmlElement
	public Map<String, XMLEntityProperty> properties = new HashMap<String, XMLEntityProperty>();
}
