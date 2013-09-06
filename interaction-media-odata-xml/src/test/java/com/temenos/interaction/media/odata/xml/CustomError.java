package com.temenos.interaction.media.odata.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CustomError")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomError {

	@XmlElement
	private String mycustomerror;
	
	public CustomError(String mycustomerror) {
		this.mycustomerror = mycustomerror;
	}
	
	public String getMyCustomError() {
		return mycustomerror;
	}
}
