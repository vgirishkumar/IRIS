package com.temenos.interaction.example.hateoas.dynamic.model;

/*
 * #%L
 * interaction-example-hateoas-simple
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
import javax.persistence.Id;

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