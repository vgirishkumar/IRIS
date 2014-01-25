//
// This file was com.temenos.messagingLayer.lookuppojo by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.temenos.messagingLayer.lookuppojo on: 2011.07.15 at 02:46:10 PM IST 
//

package com.temenos.messagingLayer.lookuppojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for lookupMapping element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="lookupMapping">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}groupName"/>
 *           &lt;element ref="{}enquiryName"/>
 *           &lt;element ref="{}selectionCriteria" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "groupName", "enquiryName", "selectionCriteria" })
@XmlRootElement(name = "lookupMapping")
public class LookupMapping {

	protected String groupName;
	protected String enquiryName;
	protected SelectionCriteria selectionCriteria;

	/**
	 * Gets the value of the groupName property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the value of the groupName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGroupName(String value) {
		this.groupName = value;
	}

	/**
	 * Gets the value of the enquiryName property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getEnquiryName() {
		return enquiryName;
	}

	/**
	 * Sets the value of the enquiryName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEnquiryName(String value) {
		this.enquiryName = value;
	}

	/**
	 * Gets the value of the selectionCriteria property.
	 * 
	 * @return
	 *         possible object is {@link SelectionCriteria }
	 * 
	 */
	public SelectionCriteria getSelectionCriteria() {
		return selectionCriteria;
	}

	/**
	 * Sets the value of the selectionCriteria property.
	 * 
	 * @param value
	 *            allowed object is {@link SelectionCriteria }
	 * 
	 */
	public void setSelectionCriteria(SelectionCriteria value) {
		this.selectionCriteria = value;
	}

}
