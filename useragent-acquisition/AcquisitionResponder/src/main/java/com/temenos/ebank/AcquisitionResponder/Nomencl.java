package com.temenos.ebank.AcquisitionResponder;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Nomencl {

	@Id
	@Basic(optional = false)
	private Long id;
	
			private String language;
			private String groupCode;
			private String code;
			private String label;
			private Integer sortOrder;
		
	public Nomencl() {}
}