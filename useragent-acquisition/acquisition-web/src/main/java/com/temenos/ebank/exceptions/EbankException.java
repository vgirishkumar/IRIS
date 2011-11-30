package com.temenos.ebank.exceptions;

/**
 * Base exception for Aquisition checked exceptions
 * @author vionescu
 */
public class EbankException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EbankException() {
		super();
	}

	public EbankException(String message, Throwable cause) {
		super(message, cause);
	}

	public EbankException(String message) {
		super(message);
	}

}
