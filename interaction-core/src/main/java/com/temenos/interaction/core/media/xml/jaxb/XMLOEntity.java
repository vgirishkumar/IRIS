package com.temenos.interaction.core.media.xml.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XMLOEntity {

	@XmlAttribute
	public String entitySetName; 

	@XmlElement
	public List<XMLOProperty> entry = new ArrayList<XMLOProperty>();
}
