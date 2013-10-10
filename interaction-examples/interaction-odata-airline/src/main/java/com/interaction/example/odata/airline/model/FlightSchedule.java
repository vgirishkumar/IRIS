package com.interaction.example.odata.airline.model;

/*
 * #%L
 * interaction-example-odata-airline
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("unused")
@Entity
public class FlightSchedule {

	@Id
	/* Added this GeneratedValue annotation to auto create ids, otherwise this class in generated from the EDMX */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
		
		@JoinColumn(name = "departureAirportCode", referencedColumnName = "code",
				insertable = false, updatable = false)
		@ManyToOne(optional = false)
		private Airport departureAirport;
		@JoinColumn(name = "arrivalAirportCode", referencedColumnName = "code",
				insertable = false, updatable = false)
		@ManyToOne(optional = false)
		private Airport arrivalAirport;
			
	public FlightSchedule() {}
}