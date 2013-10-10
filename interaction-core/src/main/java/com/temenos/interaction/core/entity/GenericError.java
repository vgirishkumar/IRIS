package com.temenos.interaction.core.entity;

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


/**
 * This class describes a generic error.
 */
public class GenericError {

	private final String code;
	private final String message;
	private final String type;
	
	/**
	 * Create a basic error instance
	 * @param code error code
	 * @param message error message
	 */
	public GenericError(String code, String message) {
		this(code, message, null);
	}
	
	/**
	 * Create an error with optional error type and inner error information.
	 * @param code error code
	 * @param message error message
	 * @param type error type
	 */
	public GenericError(String code, String message, String type) {
		this.code = code;
		this.message = message;
		this.type = type;
	}

	/**
	 * Returns the error code
	 * @return error code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Returns the error message
	 * @return error message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the error type
	 * @return error type
	 */
	public String getType() {
		return type;
	}
}
