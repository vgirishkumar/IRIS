package com.temenos.interaction.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Test")
@XmlAccessorType(XmlAccessType.FIELD)
public class NestedObject {

	@XmlElement
	private String name;
	
	public NestedObject() {}
	
	public String getName() {
		return name;
	}
}
