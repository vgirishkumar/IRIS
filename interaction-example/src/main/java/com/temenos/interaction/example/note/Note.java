package com.temenos.interaction.example.note;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="NOTE")
public class Note {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long noteID;
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
