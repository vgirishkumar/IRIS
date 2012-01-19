package com.temenos.interaction.example.note.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ID")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainObjectID {

    @XmlElement(name = "LastId")
    private Long id;
    @XmlElement(name = "domainObjectName")
    private String name;

    public DomainObjectID() {}
    
    public Long getID() {
    	return id;
    }

    public String getName() {
    	return name;
    }

}
