package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.Status.Family;

/**
 * Define the HTTP status types not defined by jax-rs.
 * @author aphethean
 */
public interface HttpStatusTypes {

	/**
	 * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
	 * <b>10.2.6 205 Reset Content</b>
	 * <p>
	 * The server has fulfilled the request and the user agent SHOULD reset the document 
	 * view which caused the request to be sent. This response is primarily intended to 
	 * allow input for actions to take place via user input, followed by a clearing of the 
	 * form in which the input is given so that the user can easily initiate another input 
	 * action. The response MUST NOT include an entity.
	 * </p>
	 */
	public final static String HTTP_STATUS_RESET_CONTENT_MSG = "Reset Content";
	public final static StatusType HTTP_STATUS_RESET_CONTENT = new StatusType() {

		public int getStatusCode() {
			return 205;
		}

		public Family getFamily() {
			return Response.Status.Family.SUCCESSFUL;
		}

		public String getReasonPhrase() {
			return "Reset Content";
		}
		
	};

}
