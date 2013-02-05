package com.interaction.example.odata.airline.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("unused")
@Entity
public class Passenger {

	@Id
	@Basic(optional = false)
	private Long passengerNo;
	@Basic(optional = false)
	private Long flightID;
	private String name;
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional = false)
	private java.util.Date dateOfBirth;
	
	@JoinColumn(name = "flightID", referencedColumnName = "flightID",insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private Flight flight;
	
	public Passenger() {}
}