package com.temenos.ebank.wicketmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactDetailsModelObject implements Serializable {

	private static final long serialVersionUID = -822295960743608230L;

	public static final Integer MINIMUM_DURATION = 36;

	// properties passed to BeanUtils.copyProperties()
	private String homePhone;
	private String workPhone;
	private String mobilePhone;
	private String faxNumber;
	private String emailAddress;
	private String emailConf;
	private String preferredContactMethod;
	private String residentialStatus;
	private String otherResidentialStatus;
	private Integer residentialAdrPeriod;

	// address objects - converted individually for not upsetting hibernate
	private AddressWicketModelObject address = new AddressWicketModelObject();
	private List<PreviousAddressWicketModelObject> previousAddresses = new ArrayList<PreviousAddressWicketModelObject>();
	private AddressWicketModelObject correspondenceAddress = new AddressWicketModelObject();

	// flags for setting radio buttons in the form and hiding/showing address forms
	private Boolean isCorrespondenceAddressSameAsResidential = true;
	
	// getters/setters
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailConf(String emailConf) {
		this.emailConf = emailConf;
	}

	public String getEmailConf() {
		return emailConf;
	}

	public void setPreferredContactMethod(String preferredContactMethod) {
		this.preferredContactMethod = preferredContactMethod;
	}

	public String getPreferredContactMethod() {
		return preferredContactMethod;
	}

	public void setAddress(AddressWicketModelObject address) {
		this.address = address;
	}

	public AddressWicketModelObject getAddress() {
		return address;
	}

	public void setResidentialStatus(String residentialStatus) {
		this.residentialStatus = residentialStatus;
	}

	public String getResidentialStatus() {
		return residentialStatus;
	}

	public void setOtherResidentialStatus(String otherResidentialStatus) {
		this.otherResidentialStatus = otherResidentialStatus;
	}

	public String getOtherResidentialStatus() {
		return otherResidentialStatus;
	}

	public void setCorrespondenceAddress(AddressWicketModelObject correspondenceAddress) {
		this.correspondenceAddress = correspondenceAddress;
	}

	public AddressWicketModelObject getCorrespondenceAddress() {
		return correspondenceAddress;
	}

	public void setResidentialAdrPeriod(Integer residentialAdrPeriod) {
		this.residentialAdrPeriod = residentialAdrPeriod;
	}

	public Integer getResidentialAdrPeriod() {
		return residentialAdrPeriod;
	}

	public void setPreviousAddresses(List<PreviousAddressWicketModelObject> previousAddresses) {
		this.previousAddresses = previousAddresses;
	}

	public List<PreviousAddressWicketModelObject> getPreviousAddresses() {
		return previousAddresses;
	}

	public void setIsCorrespondenceAddressSameAsResidential(Boolean isCorrespondenceAddressSameAsResidential) {
		this.isCorrespondenceAddressSameAsResidential = isCorrespondenceAddressSameAsResidential;
	}

	public Boolean getIsCorrespondenceAddressSameAsResidential() {
		return isCorrespondenceAddressSameAsResidential;
	}
	
	public ContactDetailsModelObject() {

	}

	public Integer getTotalAddressesDuration() {
		int total = residentialAdrPeriod;
		for (PreviousAddressWicketModelObject previous : previousAddresses) {
			// duration in model is null until a value is selected
			if (previous.getDuration() != null) {
				total += previous.getDuration();
			}
		}
		return total;
	}

	public Boolean isAnotherPreviousAddressNeeded() {
		boolean needed = false;
		if (getTotalAddressesDuration() < MINIMUM_DURATION) {
			needed = true;
		}
		return needed;
	}

	public Boolean isPreviousAddressesListNeeded() {
		boolean needed = false;
		if (residentialAdrPeriod != null && residentialAdrPeriod < MINIMUM_DURATION) {
			needed = true;
		}
		return needed;
	}

}
