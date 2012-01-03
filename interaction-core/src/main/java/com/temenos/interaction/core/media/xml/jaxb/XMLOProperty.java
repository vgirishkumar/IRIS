package com.temenos.interaction.core.media.xml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class XMLOProperty {

	@XmlAttribute
	public String key; 
	 
	@XmlValue
	public String value;
}
