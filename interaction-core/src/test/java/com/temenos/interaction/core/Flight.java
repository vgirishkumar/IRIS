package com.temenos.interaction.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Flight")
@XmlAccessorType(XmlAccessType.FIELD)
public class Flight {

	@XmlElement
	private int id;
	
	@XmlElement
	private String flight;
	
	public Flight() {}
	
	public int getId() {
		return id;
	}
	
	public String getFlight() {
		return flight;
	}
}
