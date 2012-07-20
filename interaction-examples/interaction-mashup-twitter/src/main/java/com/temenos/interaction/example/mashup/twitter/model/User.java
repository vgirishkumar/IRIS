package com.temenos.interaction.example.mashup.twitter.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name = "user")
@Table(name="USER")
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

	@Id
	@Basic(optional = false)
	private Long userID;
    @XmlElement(name = "handle")
    private String handle;
    
    /* Hibernate & JAXB */
    public User() {}
    
    public User(Long id, String handle) {
    	this.userID = id;
    	this.handle = handle;
    }
    
	public Long getUserID() {
		return userID;
	}
    
    public String getHandle() {
    	return handle;
    }
}
