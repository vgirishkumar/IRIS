package com.interaction.example.odata.airline.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("unused")
@Entity
public class Flight {

	@Id
	@Basic(optional = false)
	private Long flightID;
	
			@Temporal(TemporalType.TIMESTAMP)
		private java.util.Date takeoffTime;
		
	public Flight() {}
}