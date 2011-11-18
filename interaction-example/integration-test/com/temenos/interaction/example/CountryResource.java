package com.temenos.interaction.example;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.temenos.interaction.example.country.Country;

/**
 * This object represents the client view of the country resource.
 * @author aphethean
 */
@XmlRootElement(name = "resource")
public class CountryResource {
	
	private Country country;
	
	@XmlElement
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country c) {
		country = c;
	}
}
