package com.temenos.interaction.example.note;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ID")
public class ID {

	@Id
	@Basic(optional = false)
    private String domainObjectName;
	@Column(name="LastId")
	private Long lastId;

    /* Hibernate */
    public ID() {}
    
	public Long getID() {
		return lastId;
	}

    public String getDomainObjectName() {
    	return domainObjectName;
    }
}
