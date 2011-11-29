package com.temenos.interaction.example.note;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ID")
public class ID {

	@Id
	@Basic(optional = false)
    private String domainObjectName;
	private Long ID;

    /* Hibernate */
    public ID() {}
    
	public Long getID() {
		return ID;
	}

    public String getDomainObjectName() {
    	return domainObjectName;
    }
}
