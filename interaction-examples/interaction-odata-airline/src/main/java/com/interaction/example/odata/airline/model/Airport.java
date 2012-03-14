package com.interaction.example.odata.airline.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

@SuppressWarnings("unused")
@Entity
public class Airport {

	@Id
	@Basic(optional = false)
	private String code;

	private String name;
	private String country;
		
	public Airport() {}
}