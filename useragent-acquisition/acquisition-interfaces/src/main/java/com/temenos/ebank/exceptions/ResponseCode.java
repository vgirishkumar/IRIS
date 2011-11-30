package com.temenos.ebank.exceptions;

/**
 * Convenience enumeration for describing all the alert codes.
 * Could become obsolete soon though, depending on what
 * T24 will serve.
 * 
 * @author vionescu
 * 
 */
public enum ResponseCode {
	EVERYTHING_OK("1111", true),
	INCORRECT_EMAIL_OR_REFNO("0000"), SAVE_ERROR("0001"), VALIDATION_ERROR("0002"), FIRST_HOLDER_INELIGIBLE("0003"), SECOND_HOLDER_INELIGIBLE(
			"0004"), BOTH_HOLDERS_INELIGIBLE("0005"), ALTERNATE_OPTIONS_INELIGIBLE("0006"), ELIGIBLE_OK("0007", true), SAVE_OK(
			"0008", true), TECHNICAL_ERROR("0009"), APPLICATION_OK("0010", true), NO_ADDRESS_IN_PAF("0011"), REFNO_EXPIRED("0012"), REFNO_UNAVAILABLE(
			"0013"), INVALID_CHARACTERS("0014");

	private final String code;
	private boolean isOk = false;
	
	
	/**
	 * Check whether this response code indicates an error condition
	 * @return
	 */
	public boolean isOk() {
		return isOk;
	}

	/**
	 * Checks whether this response code stands for a technical error 
	 * @return
	 */
	public boolean isTechnicalError() {
		return this == TECHNICAL_ERROR;
	}
	/**
	 * Returns the alert code
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Constructs an enum element
	 * 
	 * @param code
	 */
	private ResponseCode(String code) {
		this.code = code;
	}

	/**
	 * Constructs an enum element
	 * 
	 * @param code
	 * @param isOk whether this enum element indicates an error condition
	 */
	private ResponseCode(String code, boolean isOk) {
		this.code = code;
		this.isOk = isOk;
	}
	
}
