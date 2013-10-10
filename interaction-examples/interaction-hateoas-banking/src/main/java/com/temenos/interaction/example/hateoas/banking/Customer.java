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
