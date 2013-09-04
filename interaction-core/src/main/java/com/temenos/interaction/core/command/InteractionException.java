package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response.StatusType;

/**
 * Interaction exception.
 * This exception enables IRIS commands to raise interaction exceptions.  
 */
public class InteractionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private StatusType httpStatus;

	/**
	 * Construct a new exception
	 * @param httpStatus HTTP status code
	 */
	public InteractionException(StatusType httpStatus) {
		this.httpStatus = httpStatus;
	}

	/**
	 * Construct a new exception
	 * @param httpStatus HTTP status code
	 * @param message error message
	 */
	public InteractionException(StatusType httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	/**
	 * Return the HTTP status code
	 * @return http status code
	 */
	public StatusType getHttpStatus() {
		return httpStatus;
	}
}
