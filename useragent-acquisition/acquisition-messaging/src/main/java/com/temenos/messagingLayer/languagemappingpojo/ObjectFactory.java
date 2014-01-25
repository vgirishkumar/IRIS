//
// This file was com.temenos.messagingLayer.languagemappingpojo by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.temenos.messagingLayer.languagemappingpojo on: 2011.07.29 at 04:44:54 PM IST 
//

package com.temenos.messagingLayer.languagemappingpojo;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * com.temenos.messagingLayer.languagemappingpojo in the com.temenos.messagingLayer.languagemappingpojo package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _T24LangCode_QNAME = new QName("", "t24LangCode");
	private final static QName _WebLangCode_QNAME = new QName("", "webLangCode");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
	 * com.temenos.messagingLayer.languagemappingpojo
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Language }
	 * 
	 */
	public Language createLanguage() {
		return new Language();
	}

	/**
	 * Create an instance of {@link T24LanguageMapping }
	 * 
	 */
	public T24LanguageMapping createT24LanguageMapping() {
		return new T24LanguageMapping();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "t24LangCode")
	public JAXBElement<BigInteger> createT24LangCode(BigInteger value) {
		return new JAXBElement<BigInteger>(_T24LangCode_QNAME, BigInteger.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "webLangCode")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	public JAXBElement<String> createWebLangCode(String value) {
		return new JAXBElement<String>(_WebLangCode_QNAME, String.class, null, value);
	}

}
