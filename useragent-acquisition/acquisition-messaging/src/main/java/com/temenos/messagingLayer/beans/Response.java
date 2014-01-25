/**
 * 
 */
package com.temenos.messagingLayer.beans;

/**
 * @author vsangeetha1
 *
 */
// TODO: Auto-generated Javadoc

/**
 * Simple class to hold the status of a response from the server
 * such that we can determine if the message got through or hit a
 * technical error. Also stores the time spent in OFS - if this info is there!
 */
public class Response {

	/** The iv msg. */
	private String ivMsg = "";

	/** The iv error. */
	private String ivErrorText = "";

	/**
	 * Instantiates a new browser response.
	 * 
	 * @param ivParameters
	 *            the iv parameters
	 */

	/**
	 * Gets the msg.
	 * 
	 * @return the msg
	 */
	public String getMsg() {
		return ivMsg;
	}

	/**
	 * Gets the error.
	 * 
	 * @return the error
	 */
	public String getErrorText() {

		return ivErrorText;
	}

	/**
	 * Sets the msg.
	 * 
	 * @param myMsg
	 *            the new msg
	 */
	public void setMsg(String myMsg) {
		// Strip out any 'evil' HTML tags from the response, as a crude first defence against XSS.
		// todo: Validate the XML with a Schema, for better XSS protection
		// todo: get the scripts tag removed on the server
		ivMsg = myMsg;
	}

	/**
	 * Sets the error.
	 * 
	 * @param myError
	 *            the new error
	 */
	public void setErrorText(String myError) {

		ivErrorText = myError;
	}

	/**
	 * Checks if is valid.
	 * 
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return (ivErrorText.equalsIgnoreCase(""));
	}
}
