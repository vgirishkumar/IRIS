package com.temenos.interaction.example.note;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.temenos.interaction.core.resource.EntityResource;

@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringResource extends EntityResource<String> {

}
