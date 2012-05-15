package com.temenos.interaction.example.hateoas.simple;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Preferences {

	@Id
	@Basic(optional = false)
	private String userID;
	private String currency;
	private String language;
		
	public Preferences() {}
	public Preferences(String userID, String currency, String language) {
		this.userID = userID;
		this.currency = currency;
		this.language = language;
	}

	public String getUserID() {
		return userID;
	}
	public String getCurrency() {
		return currency;
	}
	public String getLanguage() {
		return language;
	}

}