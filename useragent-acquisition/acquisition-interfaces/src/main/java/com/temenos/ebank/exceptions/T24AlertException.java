package com.temenos.ebank.exceptions;



/**
 * Encapsulates a T24 alert
 * @author vionescu
 *
 */
public class T24AlertException extends Exception {
//TODO: this class should be deleted
	//private AcquisitionResponse aquisitionResponse;
	
	private ResponseCode responseCode;
	
	
	/**
	 * Returns the response code associated with this exception
	 * @return
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructs an exception for an alert code
	 * @param alertCode Alert code in string form
	 */
	public T24AlertException(ResponseCode responseCode) {
		super();
		this.responseCode = responseCode;
	}


	 /*	 
	//**
	 * Constructs an exception for an alert code
	 * @param alertCode Alert code in string form
	 * @param success Whether this is an error condition or simply an informational alert
	 *//*
	public T24AlertException(String alertCode, boolean success) {
		super();
		this.alertInfo  = new T24AlertInfo(alertCode, success);
	}
	
	public T24AlertException(T24AlertInfo alertInfo) {
		super();
		this.alertInfo = alertInfo;
	}
*/
/*	*//**
	 * Returns the alert information asscociated with this error condition
	 * @return
	 *//*
	public T24AlertInfo getAlertInfo() {
		return alertInfo;
	}
*/
}
