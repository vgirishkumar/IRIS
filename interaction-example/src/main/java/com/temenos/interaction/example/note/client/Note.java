package com.temenos.interaction.example.note.client;

import javax.xml.bind.annotation.XmlElement;

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
