package com.temenos.ebank.domain.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Mock DTO for checking if a person is an existing customer. This bean
 * is used to invoke T24
 * 
 * @author vionescu
 * 
 */
public class CheckExistingCustomerDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private Date dateOfBirth;
	private String mobilePhone;
	private boolean existingCustomer;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobileNumber) {
		this.mobilePhone = mobileNumber;
	}

	public boolean isExistingCustomer() {
		return existingCustomer;
	}

	public void setExistingCustomer(boolean existingCustomer) {
		this.existingCustomer = existingCustomer;
	}
}
