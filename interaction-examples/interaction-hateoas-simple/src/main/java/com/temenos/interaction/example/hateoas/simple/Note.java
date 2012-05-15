package com.temenos.interaction.example.hateoas.simple;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.odata4j.core.OEntity;

@Entity(name = "note")
@Table(name="NOTE")
@XmlRootElement(name = "note")
@XmlAccessorType(XmlAccessType.FIELD)
public class Note {

	@Id
	@Basic(optional = false)
	private Long noteID;
    @XmlElement(name = "body")
    private String body;
    
    /* Hibernate & JAXB */
    public Note() {}
    
    public Note(OEntity oEntity) {
    	this.body = (String) oEntity.getProperty("body").getValue();
    }
    
	public Long getNoteID() {
		return noteID;
	}

    public Note(String body) {
    	this.body = body;
    }
    
    public String getBody() {
    	return body;
    }
}
