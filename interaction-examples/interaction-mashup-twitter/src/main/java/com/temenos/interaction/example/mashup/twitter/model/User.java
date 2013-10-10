package com.temenos.interaction.example.mashup.twitter.model;

/*
 * #%L
 * interaction-example-mashup-twitter
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "user")
@Table(name="USER")
public class User {

	@Id
	@Basic(optional = false)
	private Long userID;
	@Column(name = "FullName")
	private String fullName;
    @Column(name = "THandle")
    private String twitterHandle;
    
    /* Hibernate */
    public User() {}
    
    public User(Long id, String fullName, String handle) {
    	this.userID = id;
    	this.fullName = fullName;
    	this.twitterHandle = handle;
    }
    
	public Long getUserID() {
		return userID;
	}
    
    public String getFullName() {
    	return fullName;
    }

    public String getTwitterHandle() {
    	return twitterHandle;
    }
}
