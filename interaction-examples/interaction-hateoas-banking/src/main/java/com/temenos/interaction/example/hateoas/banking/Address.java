package com.temenos.interaction.example.hateoas.banking;

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
