package com.temenos.interaction.example.note;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.temenos.interaction.core.decorator.hal.Link;
import com.temenos.interaction.core.decorator.hal.Representation;

@XmlRootElement(name = "resource")
public class NoteRepresentation extends Representation {

	public static final String RELATIONS_URI = "http://relations.interactionexample.com/";

    @XmlElement(name = "body")
    private String body;

    /**
     * For JAXB :-(
     */
    NoteRepresentation() {}
    
    public NoteRepresentation(String body) {
    	this.body = body;
    }

    
    public static NoteRepresentation fromXmlString(String xmlRepresentation) {
        try {
            JAXBContext context = JAXBContext.newInstance(NoteRepresentation.class);
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
        return body;
    }
}
