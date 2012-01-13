package com.temenos.interaction.example.note.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.temenos.interaction.core.media.hal.Representation;

@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class NewNoteRepresentation extends Representation {

	public static final String RELATIONS_URI = "http://relations.interactionexample.com/";

	@XmlElement(name = "ID")
    private DomainObjectID id;

    /**
     * For JAXB :-(
     */
    NewNoteRepresentation() {}
    
    public DomainObjectID getID() {
    	return id;
    }

}
