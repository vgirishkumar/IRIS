//
// This file was com.temenos.messagingLayer.languagemappingpojo by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.temenos.messagingLayer.languagemappingpojo on: 2011.07.29 at 04:44:54 PM IST 
//

package com.temenos.messagingLayer.languagemappingpojo;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>
 * Java class for language element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="language">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}webLangCode"/>
 *           &lt;element ref="{}t24LangCode"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "webLangCode", "t24LangCode" })
@XmlRootElement(name = "language")
public class Language {

	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String webLangCode;
	protected BigInteger t24LangCode;

	/**
	 * Gets the value of the webLangCode property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getWebLangCode() {
		return webLangCode;
	}

	/**
	 * Sets the value of the webLangCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setWebLangCode(String value) {
		this.webLangCode = value;
	}

	/**
	 * Gets the value of the t24LangCode property.
	 * 
	 * @return
	 *         possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getT24LangCode() {
		return t24LangCode;
	}

	/**
	 * Sets the value of the t24LangCode property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setT24LangCode(BigInteger value) {
		this.t24LangCode = value;
	}

}
