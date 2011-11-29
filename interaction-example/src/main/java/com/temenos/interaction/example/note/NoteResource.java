package com.temenos.interaction.example.note;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;

import com.temenos.interaction.core.EntityResource;

@XmlRootElement(name = "resource")
public class NoteResource implements EntityResource {

    @XmlElement(name = "body")
    private String body;

	private OEntity entity;
	private Set<OLink> links;
	
    /* Keep jaxb happy */
    public NoteResource() {}
	
	public NoteResource(OEntity entity, Set<OLink> links) {
		this.entity = entity;
		this.links = links;
	}
	
	public Note getNote() {
		return new Note(body);
	}
	
	public OEntity getEntity() {
		return entity;
	}

	public Set<OLink> getLinks() {
		return links;
	}

}
