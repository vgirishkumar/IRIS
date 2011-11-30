/**
 * 
 */
package com.temenos.ebank.wicketmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.temenos.ebank.common.ComposedFieldHelper;

/**
 * @author vionescu
 * 
 */
public class CustomerWicketModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long custId;
	private Long appId;
	private String title;
	private String firstName;
	private String lastName;
	private String previousName;
	private String motherMaidenName;
	private Date dateOfBirth;
	private String gender;
	private String maritalStatus;
	private String nationality;
	private String townOfBirth;
	private String countryOfBirth;
	private String mobilePhone;
	private String faxNumber;
	private String emailAddress;
	private String jointRelationship;
	private Boolean isExistingCustomer;
	private String existingAccNumber;
	private String existingIban;
	private String existingBic;
	private String existingSortCode;
	private String countryResidence;
	private String countryMoving;
	private String preferredContactMethod;
	private String adrTypeForDebitCard;
	private String employmentStatus;
	//TODO separate occupation from otherEmploymentStatus?
	private String occupation;
	private String employerName;
	private Long employerAdrId;
	private Integer employmentLastDuration;
	private Boolean flagReceiveMarketing = false;
	private Boolean flagDiscloseIdentity = false;

	private ContactDetailsModelObject contactDetails = new ContactDetailsModelObject();
	private ContactDetailsModelObject jointContactDetails = new ContactDetailsModelObject();

	private AddressWicketModelObject employerAddress;

	private List<String> marketingContactMethods;

	public CustomerWicketModel() {
	}

	public CustomerWicketModel(Long custId, Long appId) {
		this.custId = custId;
		this.appId = appId;
	}

	/**
	 * Checks if the object is "empty" or not, i.e. if it was populated by the application or not
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		if (custId != null) {
			return false;
		}
		if (StringUtils.isNotBlank(firstName) || StringUtils.isNotBlank(title)) {
			return false;
		}
		return true;
	}

	public Long getCustId() {
		return this.custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public Long getAppId() {
		return this.appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPreviousName() {
		return this.previousName;
	}

	public void setPreviousName(String previousName) {
		this.previousName = previousName;
	}

	public String getMotherMaidenName() {
		return this.motherMaidenName;
	}

	public void setMotherMaidenName(String motherMaidenName) {
		this.motherMaidenName = motherMaidenName;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalStatus() {
		return this.maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getNationality() {
		return this.nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getTownOfBirth() {
		return this.townOfBirth;
	}

	public void setTownOfBirth(String townOfBirth) {
		this.townOfBirth = townOfBirth;
	}

	public String getCountryOfBirth() {
		return this.countryOfBirth;
	}

	public void setCountryOfBirth(String countryOfBirth) {
		this.countryOfBirth = countryOfBirth;
	}

	public String getMobilePhone() {
		return this.mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getFaxNumber() {
		return this.faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getJointRelationship() {
		return this.jointRelationship;
	}

	public void setJointRelationship(String jointRelationship) {
		this.jointRelationship = jointRelationship;
	}

	public Boolean getIsExistingCustomer() {
		return this.isExistingCustomer;
	}

	public void setIsExistingCustomer(Boolean isExistingCustomer) {
		this.isExistingCustomer = isExistingCustomer;
	}

	public String getExistingAccNumber() {
		return this.existingAccNumber;
	}

	public void setExistingAccNumber(String existingAccNumber) {
		this.existingAccNumber = existingAccNumber;
	}

	public String getExistingIban() {
		return this.existingIban;
	}

	public void setExistingIban(String existingIban) {
		this.existingIban = existingIban;
	}

	public String getExistingBic() {
		return existingBic;
	}

	public void setExistingBic(String existingBic) {
		this.existingBic = existingBic;
	}

	public String getExistingSortCode() {
		return this.existingSortCode;
	}

	public void setExistingSortCode(String existingSortCode) {
		this.existingSortCode = existingSortCode;
	}

	public String getCountryResidence() {
		return this.countryResidence;
	}

	public void setCountryResidence(String countryResidence) {
		this.countryResidence = countryResidence;
	}

	public String getCountryMoving() {
		return this.countryMoving;
	}

	public void setCountryMoving(String countryMoving) {
		this.countryMoving = countryMoving;
	}

	public String getPreferredContactMethod() {
		return this.preferredContactMethod;
	}

	public void setPreferredContactMethod(String preferredContactMethod) {
		this.preferredContactMethod = preferredContactMethod;
	}

	public String getAdrTypeForDebitCard() {
		return this.adrTypeForDebitCard;
	}

	public void setAdrTypeForDebitCard(String adrTypeForDebitCard) {
		this.adrTypeForDebitCard = adrTypeForDebitCard;
	}

	public String getEmploymentStatus() {
		return this.employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	public String getOccupation() {
		return this.occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getEmployerName() {
		return this.employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	public Long getEmployerAdrId() {
		return this.employerAdrId;
	}

	public void setEmployerAdrId(Long employerAdrId) {
		this.employerAdrId = employerAdrId;
	}

	public Integer getEmploymentLastDuration() {
		return this.employmentLastDuration;
	}

	public void setEmploymentLastDuration(Integer employmentLastDuration) {
		this.employmentLastDuration = employmentLastDuration;
	}

	public Boolean getFlagReceiveMarketing() {
		return this.flagReceiveMarketing;
	}

	public void setFlagReceiveMarketing(Boolean flagReceiveMarketing) {
		this.flagReceiveMarketing = flagReceiveMarketing;
	}

	public String getMarketingContactMethod() {
		return ComposedFieldHelper.getFieldWithSeparators(marketingContactMethods, ";");
	}

	public void setMarketingContactMethod(String marketingContactMethod) {
		this.marketingContactMethods = new ArrayList<String>();
		if (StringUtils.isNotBlank(marketingContactMethod)) {
			this.marketingContactMethods = ComposedFieldHelper.splitField(marketingContactMethod, ";");
		}
	}

	public List<String> getMarketingContactMethods() {
		return marketingContactMethods;
	}

	public void setMarketingContactMethods(List<String> marketingContactMethods) {
		this.marketingContactMethods = marketingContactMethods;
	}
	
	public AddressWicketModelObject getEmployerAddress() {
		return employerAddress;
	}

	public void setEmployerAddress(AddressWicketModelObject employmentAddress) {
		this.employerAddress = employmentAddress;
	}

	public void setContactDetails(ContactDetailsModelObject contactDetails) {
		this.contactDetails = contactDetails;
	}

	public ContactDetailsModelObject getContactDetails() {
		return contactDetails;
	}

	public void setJointContactDetails(ContactDetailsModelObject jointContactDetails) {
		this.jointContactDetails = jointContactDetails;
	}

	public ContactDetailsModelObject getJointContactDetails() {
		return jointContactDetails;
	}

	public Boolean getFlagDiscloseIdentity() {
		return flagDiscloseIdentity;
	}

	public void setFlagDiscloseIdentity(Boolean flagDiscloseIdentity) {
		this.flagDiscloseIdentity = flagDiscloseIdentity;
	}

	public String getOtherEmploymentStatus() {
		return getOccupation();
	}
	
	public void setOtherEmploymentStatus(String otherEmploymentStatus) {
		setOccupation(otherEmploymentStatus);
	}
}
