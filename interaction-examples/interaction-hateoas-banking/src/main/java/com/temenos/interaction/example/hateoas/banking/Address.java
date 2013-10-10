package com.temenos.interaction.example.hateoas.banking;

/*
 * #%L
 * interaction-example-hateoas-banking
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


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "Address")
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Address {

	@Id
	@Column(name="Id") 
	@Basic(optional = false)
	private String id;
	
    @XmlElement
	@Column(name="PostCode") 
    private String postcode;

    @XmlElement
	@Column(name="HouseNumber") 
    private Long houseNumber;
    
    public Address() {
    }
    
    public Address(String id) {
    	this.id = id;
    }

    public Address(String id, String postcode, Long houseNumber) {
    	this.id = id;
    	this.postcode = postcode;
    	this.houseNumber = houseNumber;
    }
    
	public String getId() {
		return id;
	}
    
    public String getPostcode() {
    	return postcode;
    }
    
    public Long getHouseNumber() {
    	return houseNumber;
    }
}
