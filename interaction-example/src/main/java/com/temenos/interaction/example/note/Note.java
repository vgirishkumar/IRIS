package com.temenos.interaction.example.note;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="NOTE")
@XmlRootElement(name = "Note")
@XmlAccessorType(XmlAccessType.FIELD)
public class Note {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long noteID;
    @XmlElement(name = "body")
    private String body;

    /* Hibernate */
    public Note() {}
    
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
