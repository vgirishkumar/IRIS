package com.temenos.interaction.sdk.util;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is used to parse the ReferentialConstraint elements
 * of an edmx file in order to be able to obtain the entity property
 * that defines the value used to follow a link.
 * [odata4j does not parse ReferentialConstraint elements] 
 */
public class ReferentialConstraintParser extends DefaultHandler {
	String linkProperty = null;
	boolean foundAssociation = false;
	boolean foundDependent = false;
	String associationName;

	public ReferentialConstraintParser(String associationName) {
		this.associationName = associationName;
	}

	public static String getLinkProperty(final String associationName, String edmxFile) {
		try {
			return getLinkProperty(associationName,  new FileInputStream(edmxFile));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLinkProperty(final String associationName, InputStream isEdmx) {
	    //Parse the edmx file
		ReferentialConstraintParser handler = new ReferentialConstraintParser(associationName);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(isEdmx, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return handler.getLinkProperty();
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		if (qName.equals("Association") && attributes.getValue("Name").equals(associationName)) {
			foundAssociation = true;
		}
		else if(foundAssociation && qName.equals("Dependent")) {
			foundDependent = true;
		}
		else if(foundDependent && qName.equals("PropertyRef")) {
			linkProperty = attributes.getValue("Name");
			foundAssociation = false;
			foundDependent = false;			
		}
	}
 
	public String getLinkProperty() {
		return linkProperty;
	}
}
