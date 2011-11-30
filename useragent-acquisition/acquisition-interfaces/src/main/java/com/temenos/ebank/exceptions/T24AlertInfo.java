package com.temenos.ebank.exceptions;

import java.io.Serializable;

/**
 * Object encapsulating the info returned by a T24 check or a T24 service call.
 * @author vionescu

 */

public class T24AlertInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean success;
	private String alertCode;
	private Serializable additionalInfo;

	public boolean isSuccess() {
		return success;
	}

	public T24AlertInfo(String alertCode, boolean success) {
		super();
		this.success = success;
		this.alertCode = alertCode;
	}

	public T24AlertInfo(String alertCode, boolean success, Serializable additionalInfo) {
		super();
		this.success = success;
		this.alertCode = alertCode;
		this.additionalInfo = additionalInfo;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(String alertCode) {
		this.alertCode = alertCode;
	}

	public Serializable getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Serializable additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
}
