package com.temenos.interaction.core.entity;

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
