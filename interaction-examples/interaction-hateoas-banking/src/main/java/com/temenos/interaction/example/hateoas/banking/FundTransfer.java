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
@Table(name = "FundTransfer")
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FundTransfer {

	@Id
	@Column(name="Id") 
	@Basic(optional = false)
	private Long id;
	
    @XmlElement
	@Column(name="Body") 
    private String body;
    
    /* Hibernate & JAXB */
    public FundTransfer() {}
    
    public FundTransfer(Long id) {
    	this.id = id;
    }

    public FundTransfer(Long id, String body) {
    	this.id = id;
    	this.body = body;
    }
    
	public Long getId() {
		return id;
	}
    
    public String getBody() {
    	return body;
    }
}
