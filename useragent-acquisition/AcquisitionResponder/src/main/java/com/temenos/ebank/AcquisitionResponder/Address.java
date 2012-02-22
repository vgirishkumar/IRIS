package com.temenos.ebank.AcquisitionResponder;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Address {

	@Id
	@Basic(optional = false)
	private Long id;
	
			private String country;
			private String line1;
			private String line2;
			private String county;
			private String district;
			private String town;
			private String postcode;
		
	public Address() {}
}