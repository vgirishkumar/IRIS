package com.temenos.ebank.common.wicket.choiceRenderers;

public enum Choices {
	COUNTRY,
	NATIONALITY,
	employmentStatus,
	jointRelationship,
	title,
	maritalStatus,
	annualIncome,
	estimatedDepositAmount,
	contactChoice,
	residentialStatus,
	establishmentReason,
	usageReason,
	annualDeposit,
	activityOrigin,
	activityWealth,
	marketingContactChoice,
	hearSource,
	interestPayment,
	ftdTerm,
	CA_accountCurrency("CA.accountCurrency"),
	RS_accountCurrency("RS.accountCurrency"),
	FTD_accountCurrency("FTD.accountCurrency"),
	IASA_accountCurrency("IASA.accountCurrency"),
	IBSA_accountCurrency("IBSA.accountCurrency"),
	RASA_accountCurrency("RASA.accountCurrency");
	
	private String choice = null;

	Choices() {
	}

	Choices(String choice) {
		this.choice = choice;
	}

	@Override
	public String toString() {
		return choice == null ? super.toString() : choice;
	}
}
