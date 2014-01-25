package com.temenos.ebank.AcquisitionResponder;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Country {

	@Id
	@Basic(optional = false)
	private String code;
	
			private String languageCode;
			private String label;
		
	public Country() {}
}