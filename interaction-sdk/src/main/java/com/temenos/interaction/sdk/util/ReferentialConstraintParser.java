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
	public final static String DEPENDENT = "Dependent";
	public final static String PRINCIPAL = "Principal";
	
	String linkProperty = null;
	boolean foundAssociation = false;
	boolean found = false;
	String associationName;
	String searchConstraint;

	public ReferentialConstraintParser(String associationName, String searchConstraint) {
		this.associationName = associationName;
		this.searchConstraint = searchConstraint;
	}

	public static String getDependent(final String associationName, String edmxFile) {
		try {
			String searchConstraint = DEPENDENT;
			return getProperty(associationName,  new FileInputStream(edmxFile), searchConstraint);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDependent(final String associationName, InputStream isEdmx) {
		try {
			String searchConstraint = DEPENDENT;
			return getProperty(associationName,  isEdmx, searchConstraint);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getPrincipal(final String associationName, String edmxFile) {
		try {
			String searchConstraint = PRINCIPAL;
			return getProperty(associationName,  new FileInputStream(edmxFile), searchConstraint);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getPrincipal(final String associationName, InputStream isEdmx) {
		try {
			String searchConstraint = PRINCIPAL;
			return getProperty(associationName,  isEdmx, searchConstraint);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getProperty(final String associationName, InputStream isEdmx, String searchConstraint) {
	    //Parse the edmx file
		ReferentialConstraintParser handler = new ReferentialConstraintParser(associationName, searchConstraint);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(isEdmx, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return handler.getProperty();
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		if (qName.equals("Association") && attributes.getValue("Name").equals(associationName)) {
			foundAssociation = true;
		}
		else if(foundAssociation && qName.equals(searchConstraint)) {
			found = true;
		}
		else if(found && qName.equals("PropertyRef")) {
			linkProperty = attributes.getValue("Name");
			foundAssociation = false;
			found = false;			
		}
	}
 
	public String getProperty() {
		return linkProperty;
	}
}
