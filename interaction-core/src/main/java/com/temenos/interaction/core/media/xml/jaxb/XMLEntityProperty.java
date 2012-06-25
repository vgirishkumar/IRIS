package com.temenos.interaction.core.media.xml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class XMLEntityProperty {

	@XmlAttribute
	public String name; 
	 
	@XmlValue
	public String value;
}
