package com.temenos.interaction.example.hateoas.dynamic.model;

/*
 * #%L
 * interaction-example-hateoas-simple
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name = "note")
@Table(name="NOTE")
@XmlRootElement(name = "note")
@XmlAccessorType(XmlAccessType.FIELD)
public class Note {

	@Id
	@Basic(optional = false)
	private Long noteID;
    @XmlElement(name = "body")
    private String body;
    private String reference;
    
    /* Hibernate & JAXB */
    public Note() {}
    
    public Note(Long id, String body, String reference) {
    	this.noteID = id;
    	this.body = body;
    	this.reference = reference;
    }
    
	public Long getNoteID() {
		return noteID;
	}
    
    public String getBody() {
    	return body;
    }
    
    public String getReference() {
    	return reference;
    }
}
