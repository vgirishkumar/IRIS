package com.interaction.example.odata.linkid.model;

/*
 * #%L
 * interaction-example-odata-linkid
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SuppressWarnings("unused")
public class FlightSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long flightScheduleID;

	private String flightNo;
	private String arrivalAirportCode;
	@Temporal(TemporalType.TIME)
	private java.util.Date arrivalTime;
	@Temporal(TemporalType.TIME)
	private java.util.Date departureTime;
	private String departureAirportCode;
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date firstDeparture;
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date lastDeparture;
	@JoinColumn(name = "departureAirportCode", referencedColumnName = "code", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private Airport departureAirport;
	@JoinColumn(name = "arrivalAirportCode", referencedColumnName = "code", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private Airport arrivalAirport;

    @OneToMany(mappedBy="flightScheduleNum")
    private Collection<Flight> flights;

	public FlightSchedule() {
	}
}