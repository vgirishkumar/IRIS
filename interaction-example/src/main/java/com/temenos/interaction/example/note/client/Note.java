package com.temenos.interaction.example.note.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "note")
@XmlAccessorType(XmlAccessType.FIELD)
public class Note {

    @XmlElement(name = "body")
    private String body;

    public Note() {}
    
    public Note(String body) {
    	this.body = body;
    }
    
    public String getBody() {
    	return body;
    }
    
}
