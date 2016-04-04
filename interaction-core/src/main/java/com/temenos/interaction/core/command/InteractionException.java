package com.temenos.interaction.core.command;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import javax.ws.rs.core.Response.StatusType;

/**
 * Interaction exception.
 * This exception enables IRIS commands to raise interaction exceptions.  
 */
public class InteractionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final StatusType httpStatus;

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
	 * Construct a new exception
	 * @param httpStatus HTTP status code
	 * @param message error message
	 * @param cause	the cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public InteractionException(StatusType httpStatus, String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}


	/**
	 * Construct a new exception
	 * @param httpStatus HTTP status code
	 * @param cause	the cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public InteractionException(StatusType httpStatus, Throwable cause) {
		super(cause);
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
