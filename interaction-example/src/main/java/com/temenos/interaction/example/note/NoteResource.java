package com.temenos.interaction.example.note;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.odata4j.core.OEntity;

import com.temenos.interaction.core.EntityResource;

@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class NoteResource extends EntityResource {

    @XmlElement(name = "Note")
    private Note note;
    @XmlTransient
	private OEntity entity;
	
    /* Keep jaxb happy */
    public NoteResource() {}
	
	public NoteResource(OEntity entity) {
		this.entity = entity;
	}
	
	public Note getNote() {
		return note;
	}
	
	public OEntity getOEntity() {
		return entity;
	}

}
