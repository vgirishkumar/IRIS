package com.temenos.interaction.example.hateoas.simple;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Profile {

	@Id
	@Basic(optional = false)
	private String userID;
	
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date DOB;
	
	private String location;
	
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date pictureTaken;
		
	public Profile() {}

	public String getUserID() {
		return userID;
	}

	public String getName() {
		return name;
	}

	public java.util.Date getDOB() {
		return DOB;
	}

	public String getLocation() {
		return location;
	}

	public java.util.Date getPictureTaken() {
		return pictureTaken;
	}
}