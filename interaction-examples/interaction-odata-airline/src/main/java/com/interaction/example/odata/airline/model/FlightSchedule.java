package com.interaction.example.odata.airline.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class FlightSchedule {

	@Id
	@Basic(optional = false)
	private Long flightScheduleID;
	
			private String arrivalAirportCode;
			@Temporal(TemporalType.TIME)
		private java.util.Date arrivalTime;
			private String flightNo;
			@Temporal(TemporalType.TIMESTAMP)
		private java.util.Date firstDeparture;
			@Temporal(TemporalType.TIME)
		private java.util.Date departureTime;
			private String departureAirportCode;
			@Temporal(TemporalType.TIMESTAMP)
		private java.util.Date lastDeparture;
		
	public FlightSchedule() {}
}