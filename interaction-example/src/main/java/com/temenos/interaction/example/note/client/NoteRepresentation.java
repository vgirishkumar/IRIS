package com.temenos.interaction.example.note.client;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.temenos.interaction.core.media.hal.Link;
import com.temenos.interaction.core.media.hal.Representation;

@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class NoteRepresentation extends Representation {

	public static final String RELATIONS_URI = "http://relations.interactionexample.com/";

    @XmlElement
    private Note note;

    /**
     * For JAXB :-(
     */
    NoteRepresentation() {}
    
    public NoteRepresentation(String note) {
    	this.note = new Note(note);
    }

    
    public static NoteRepresentation fromXmlString(String xmlRepresentation) {
        try {
            JAXBContext context = JAXBContext.newInstance(NoteRepresentation.class, Note.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (NoteRepresentation) unmarshaller.unmarshal(new ByteArrayInputStream(xmlRepresentation.getBytes()));
        } catch (Exception e) {
            throw new InvalidNoteException(e);
        }
    }
    

    public String toString() {
        try {
            JAXBContext context = JAXBContext.newInstance(NoteRepresentation.class);
            Marshaller marshaller = context.createMarshaller();

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(this, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Link getDeleteLink() {
        return getLinkByName(RELATIONS_URI + "delete");
    }

    public Link getSelfLink() {
        return getLinkByName("self");
    }
    
    public String getBody() {
        return note.getBody();
    }
}
