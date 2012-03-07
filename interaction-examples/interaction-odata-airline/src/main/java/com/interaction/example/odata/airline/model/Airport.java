package com.interaction.example.odata.airline.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Airport {

	@Id
	@Basic(optional = false)
	private String code;
	
			private String name;
			private String country;
		
	public Airport() {}
}