package com.temenos.ebank.exceptions;

public class EbankValidationException extends EbankException {
	private static final long serialVersionUID = 1L;

	public final static String BIC_INVALID = "bic.invalid";
	public final static String IBAN_INVALID = "iban.invalid";
	public final static String PHONE_NO_INVALID = "phoneNo.invalid";
	public final static String SORT_CODE_INVALID = "sortCode.invalid";
	public final static String ACCOUNT_NUMBER_INVALID = "accountNumber.invalid";

	private String resourceId;

	public EbankValidationException(String resourceId) {
		super();
		this.resourceId = resourceId;
	}

	public String getResourceId() {
		return resourceId;
	}

}
