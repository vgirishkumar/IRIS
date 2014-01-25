/**
 * 
 */
package com.temenos.ebank.wicketmodel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.temenos.ebank.common.ComposedFieldHelper;

/**
 * Wicket model object for representing a client acquitision application form
 * @author vionescu
 * 
 */
public class ApplicationWicketModelObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long appId;
	private String appRef;
	private String productRef;
	private String ftdTerm;
	private BigDecimal ftdInterestRate;	
	private Boolean isSole = true;
	private Long applicantId;
	private Long secondApplicantId;
	private String annualIncome;
	private BigDecimal depositAmount;
	private String estimatedDepositAmount;
	private String accEstablishReason;
	private String accUsageReason;
	private String annualDeposit;
	private String countryOriginMoney;
	private Boolean flagDataPrivacy = false;
	private Boolean flagTcs = false;
	private Boolean flagJointApp = false;
	private String feedbackHear;
	private String promoCode;
	private Boolean flagPostDocuments = false;
	private String documentsAddressType;
	private Date creationDate;
	private Boolean flagMailSent = false;
	private Integer resumeStep;

	private CustomerWicketModel customer;
	private CustomerWicketModel secondCustomer;

	private ContactDetailsModelObject contactDetails = new ContactDetailsModelObject();
	private ContactDetailsModelObject jointContactDetails = new ContactDetailsModelObject();

	private String otherEstablishReason;
	private String otherUsageReason;
	private String otherActivityOrigin;
	private String otherActivityWealth;
	private String interestPayment;
	private Boolean offshoreOpenedAccount = true;
	private Boolean openingSavingsAccount = true;
	private Boolean openingCurrentAccount = true;

	private List<String> activitiesOriginDeposit;

	private List<String> activitiesWealth;

	private List<String> accountCurrencies;

	private Boolean secondCorrespondenceSameAsFirst = true;
	private Boolean secondResidentialSameAsFirstResidential = true;

	public ApplicationWicketModelObject() {
	}

	public ApplicationWicketModelObject(Long appId, String appRef, String productRef) {
		this.appId = appId;
		this.appRef = appRef;
		this.productRef = productRef;
	}

	public Long getAppId() {
		return this.appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getAppRef() {
		return this.appRef;
	}

	public void setAppRef(String appRef) {
		this.appRef = appRef;
	}

	public String getProductRef() {
		return this.productRef;
	}

	public void setProductRef(String productRef) {
		this.productRef = productRef;
	}

	
	public String getAccountCurrency() {
		return ComposedFieldHelper.getFieldWithSeparators(accountCurrencies, ";");
	}

	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrencies = new ArrayList<String>();
		if (StringUtils.isNotBlank(accountCurrency)) {
			this.accountCurrencies = ComposedFieldHelper.splitField(accountCurrency, ";");
		}

	}

	public List<String> getAccountCurrencies() {
		return accountCurrencies;
	}

	public void setAccountCurrencies(List<String> accountCurrencies) {
		this.accountCurrencies = accountCurrencies;
	}

	public Boolean getIsSole() {
		return this.isSole;
	}

	public void setIsSole(Boolean isSole) {
		this.isSole = isSole;
	}

	// TODO: fa model separat pt Wicket, ca asa nu merge
	public String getSoleOrJointAsText() {
		return this.isSole ? "sole" : "joint";
	}

	public Long getApplicantId() {
		return this.applicantId;
	}

	public void setApplicantId(Long applicantId) {
		this.applicantId = applicantId;
	}

	public Long getSecondApplicantId() {
		return this.secondApplicantId;
	}

	public void setSecondApplicantId(Long secondApplicantId) {
		this.secondApplicantId = secondApplicantId;
	}

	public String getAnnualIncome() {
		return this.annualIncome;
	}

	public void setAnnualIncome(String annualIncome) {
		this.annualIncome = annualIncome;
	}

	public BigDecimal getDepositAmount() {
		return this.depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public void setEstimatedDepositAmount(String estimatedDepositAmount) {
		this.estimatedDepositAmount = estimatedDepositAmount;
	}

	public String getEstimatedDepositAmount() {
		return estimatedDepositAmount;
	}

	public String getAccEstablishReason() {
		return this.accEstablishReason;
	}

	public void setAccEstablishReason(String accEstablishReason) {
		this.accEstablishReason = accEstablishReason;
	}

	public String getAccUsageReason() {
		return this.accUsageReason;
	}

	public void setAccUsageReason(String accUsageReason) {
		this.accUsageReason = accUsageReason;
	}

	public String getAnnualDeposit() {
		return this.annualDeposit;
	}

	public void setAnnualDeposit(String annualDeposit) {
		this.annualDeposit = annualDeposit;
	}

	public String getActivityOriginDeposit() {
		return ComposedFieldHelper.getFieldWithSeparators(activitiesOriginDeposit, ";");
	}

	public void setActivityOriginDeposit(String activityOriginDeposit) {
		this.activitiesOriginDeposit = new ArrayList<String>();
		if ((activityOriginDeposit != null) && (activityOriginDeposit.length() > 0)) {
			this.activitiesOriginDeposit = ComposedFieldHelper.splitField(activityOriginDeposit, ";");
		}
	}
	
	public List<String> getActivitiesOriginDeposit() {
		return activitiesOriginDeposit;
	}

	public void setActivitiesOriginDeposit(List<String> activitiesOriginDeposit) {
		this.activitiesOriginDeposit = activitiesOriginDeposit;
	}
	
	public String getOtherEstablishReason() {
		return otherEstablishReason;
	}

	public void setOtherEstablishReason(String otherEstablishReason) {
		this.otherEstablishReason = otherEstablishReason;
	}

	public String getOtherUsageReason() {
		return otherUsageReason;
	}

	public void setOtherUsageReason(String otherUsageReason) {
		this.otherUsageReason = otherUsageReason;
	}

	public String getOtherActivityOrigin() {
		return otherActivityOrigin;
	}

	public void setOtherActivityOrigin(String otherActivityOrigin) {
		this.otherActivityOrigin = otherActivityOrigin;
	}

	public String getOtherActivityWealth() {
		return otherActivityWealth;
	}

	public void setOtherActivityWealth(String otherActivityWealth) {
		this.otherActivityWealth = otherActivityWealth;
	}

	public String getInterestPayment() {
		return interestPayment;
	}

	public void setInterestPayment(String interestPayment) {
		this.interestPayment = interestPayment;
	}

	public Boolean isOffshoreOpenedAccount() {
		return offshoreOpenedAccount;
	}

	public void setOffshoreOpenedAccount(Boolean offshoreOpenedAccount) {
		this.offshoreOpenedAccount = offshoreOpenedAccount;
	}

	public Boolean getOpeningSavingsAccount() {
		return openingSavingsAccount;
	}

	public Boolean getOpeningCurrentAccount() {
		return openingCurrentAccount;
	}

	public String getCountryOriginMoney() {
		return this.countryOriginMoney;
	}

	public void setCountryOriginMoney(String countryOriginMoney) {
		this.countryOriginMoney = countryOriginMoney;
	}

	public String getActivityWealth() {
		return ComposedFieldHelper.getFieldWithSeparators(activitiesWealth, ";");
	}

	public void setActivityWealth(String activityWealth) {
		this.activitiesWealth = new ArrayList<String>();
		if ((activityWealth != null) && (activityWealth.length() > 0)) {
			this.activitiesWealth = ComposedFieldHelper.splitField(activityWealth, ";");
		}
	}

	public List<String> getActivitiesWealth() {
		return activitiesWealth;
	}

	public void setActivitiesWealth(List<String> activitiesWealth) {
		this.activitiesWealth = activitiesWealth;
	}

	public Boolean getFlagDataPrivacy() {
		return this.flagDataPrivacy;
	}

	public void setFlagDataPrivacy(Boolean flagDataPrivacy) {
		this.flagDataPrivacy = flagDataPrivacy;
	}

	public Boolean getFlagTcs() {
		return this.flagTcs;
	}

	public void setFlagTcs(Boolean flagTcs) {
		this.flagTcs = flagTcs;
	}

	public Boolean getFlagJointApp() {
		return this.flagJointApp;
	}

	public void setFlagJointApp(Boolean flagJointApp) {
		this.flagJointApp = flagJointApp;
	}

	public String getFeedbackHear() {
		return this.feedbackHear;
	}

	public void setFeedbackHear(String feedbackHear) {
		this.feedbackHear = feedbackHear;
	}

	public String getPromoCode() {
		return this.promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public Boolean getFlagPostDocuments() {
		return this.flagPostDocuments;
	}

	public void setFlagPostDocuments(Boolean flagPostDocuments) {
		this.flagPostDocuments = flagPostDocuments;
	}

	public String getDocumentsAddressType() {
		return this.documentsAddressType;
	}

	public void setDocumentsAddressType(String documentsAddressType) {
		this.documentsAddressType = documentsAddressType;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean getFlagMailSent() {
		return this.flagMailSent;
	}

	public void setFlagMailSent(Boolean flagMailSent) {
		this.flagMailSent = flagMailSent;
	}

	public Integer getResumeStep() {
		return this.resumeStep;
	}

	public void setResumeStep(Integer resumeStep) {
		this.resumeStep = resumeStep;
	}

	public CustomerWicketModel getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerWicketModel customer) {
		this.customer = customer;
	}

	public CustomerWicketModel getSecondCustomer() {
		return secondCustomer;
	}

	public void setSecondCustomer(CustomerWicketModel secondCustomer) {
		this.secondCustomer = secondCustomer;
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

	public Boolean isOpeningSavingsAccount() {
		return openingSavingsAccount;
	}

	public Boolean isOpeningCurrentAccount() {
		return openingCurrentAccount;
	}

	public void setOpeningSavingsAccount(Boolean openingSavingsAccount) {
		this.openingSavingsAccount = openingSavingsAccount;
	}

	public void setOpeningCurrentAccount(Boolean openingCurrentAccount) {
		this.openingCurrentAccount = openingCurrentAccount;
	}

	public BigDecimal getFtdInterestRate() {
		return ftdInterestRate;
	}

	public void setFtdInterestRate(BigDecimal ftdInterestRate) {
		this.ftdInterestRate = ftdInterestRate;
	}

	public String getFtdTerm() {
		return ftdTerm;
	}

	public void setFtdTerm(String ftdTerm) {
		this.ftdTerm = ftdTerm;
	}	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		//important: leave equals this way (reference equality), because otherwise the wicket model would not get
		//updated in some usage scenarios, leading to errors.
		return this == obj;
	}

	public void setSecondCorrespondenceSameAsFirst(Boolean secondCorrespondenceSameAsFirst) {
		this.secondCorrespondenceSameAsFirst = secondCorrespondenceSameAsFirst;
	}

	public Boolean getSecondCorrespondenceSameAsFirst() {
		return secondCorrespondenceSameAsFirst;
	}

	public void setSecondResidentialSameAsFirstResidential(Boolean secondResidentialSameAsFirstResidential) {
		this.secondResidentialSameAsFirstResidential = secondResidentialSameAsFirstResidential;
	}

	public Boolean getSecondResidentialSameAsFirstResidential() {
		return secondResidentialSameAsFirstResidential;
	}
}
