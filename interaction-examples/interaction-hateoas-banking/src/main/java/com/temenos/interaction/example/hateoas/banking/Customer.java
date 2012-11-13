package com.temenos.interaction.example.hateoas.banking;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Customer")
@Access(AccessType.FIELD)
public class Customer {

	@Id
	@Column(name="Name") 
	@Basic(optional = false)
	private String name;

    @OneToOne
    @JoinColumn(name="Address")
    private Address address;
	
	@Column(name="DateOfBirth") 
	@Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    
    public Customer() {}
    
    public Customer(String name) {
    	this.name = name;
    }

    public Customer(String name, String postCode, Long houseNumber, Date dateOfBirth) {
    	this.name = name;
    	this.address = new Address(name, postCode, houseNumber);
    	this.dateOfBirth = dateOfBirth;
    }
    
	public String getName() {
		return name;
	}

    public Address getAddress() {
    	return address;
    }
	
    public Date getDateOfBirth() {
    	return dateOfBirth;
    }
}
