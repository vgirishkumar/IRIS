package com.temenos.interaction.example.mashup.twitter.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "user")
@Table(name="USER")
public class User {

	@Id
	@Basic(optional = false)
	private Long userID;
	@Column(name = "FullName")
	private String fullName;
    @Column(name = "THandle")
    private String twitterHandle;
    
    /* Hibernate */
    public User() {}
    
    public User(Long id, String fullName, String handle) {
    	this.userID = id;
    	this.fullName = fullName;
    	this.twitterHandle = handle;
    }
    
	public Long getUserID() {
		return userID;
	}
    
    public String getFullName() {
    	return fullName;
    }

    public String getTwitterHandle() {
    	return twitterHandle;
    }
}
