package com.temenos.interaction.example.hateoas.simple;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
}