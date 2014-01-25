//
// This file was com.temenos.messagingLayer.lookuppojo by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.temenos.messagingLayer.lookuppojo on: 2011.07.15 at 02:46:10 PM IST 
//

package com.temenos.messagingLayer.lookuppojo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * com.temenos.messagingLayer.lookuppojo in the com.temenos.messagingLayer.lookuppojo package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _GroupName_QNAME = new QName("", "groupName");
	private final static QName _EnquiryName_QNAME = new QName("", "enquiryName");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
	 * com.temenos.messagingLayer.lookuppojo
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link SelectionCriteria }
	 * 
	 */
	public SelectionCriteria createSelectionCriteria() {
		return new SelectionCriteria();
	}

	/**
	 * Create an instance of {@link LookupMapping }
	 * 
	 */
	public LookupMapping createLookupMapping() {
		return new LookupMapping();
	}

	/**
	 * Create an instance of {@link Lookup }
	 * 
	 */
	public Lookup createLookup() {
		return new Lookup();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "groupName")
	public JAXBElement<String> createGroupName(String value) {
		return new JAXBElement<String>(_GroupName_QNAME, String.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "enquiryName")
	public JAXBElement<String> createEnquiryName(String value) {
		return new JAXBElement<String>(_EnquiryName_QNAME, String.class, null, value);
	}

}
