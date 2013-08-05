package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

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
	public final static String RESET_CONTENT_MSG = "Reset Content";
	public final static StatusType RESET_CONTENT = new StatusType() {
		
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

	/**
	 * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
	 * 10.4.6 405 Method Not Allowed
	 * 
	 * The method specified in the Request-Line is not allowed for the resource identified 
	 * by the Request-URI. The response MUST include an Allow header containing a list of 
	 * valid methods for the requested resource.
	 */
	public final static String METHOD_NOT_ALLOWED_MSG = "Method Not Allowed";
	public final static StatusType METHOD_NOT_ALLOWED = new StatusType() {

		public int getStatusCode() {
			return 405;
		}

		public Family getFamily() {
			return Response.Status.Family.SERVER_ERROR;
		}

		public String getReasonPhrase() {
			return "Method Not Allowed";
		}
		
	};

	/**
	 * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.5
	 * 10.5.5 504 Gateway Timeout
	 * 
	 * The server, while acting as a gateway or proxy, did not receive a timely 
	 * response from the upstream server specified by the URI (e.g. HTTP, FTP, LDAP) 
	 * or some other auxiliary server (e.g. DNS) it needed to access in attempting to 
	 * complete the request.
	 */
	public final static String GATEWAY_TIMEOUT_MSG = "Gateway Timeout";
	public final static StatusType GATEWAY_TIMEOUT = new StatusType() {

		public int getStatusCode() {
			return 504;
		}

		public Family getFamily() {
			return Response.Status.Family.SERVER_ERROR;
		}

		public String getReasonPhrase() {
			return GATEWAY_TIMEOUT_MSG;
		}
		
	};
}
