package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * An error from Hypertext Transfer Protocol -- HTTP/1.1
 * RFC 2616 Fielding, et al.
 * 
 * 10.4.6 405 Method Not Allowed
 * 
 * The method specified in the Request-Line is not allowed for the resource identified 
 * by the Request-URI. The response MUST include an Allow header containing a list of 
 * valid methods for the requested resource.
 */
public final class MethodNotAllowedCommand implements ResourceStatusCommand {

	public final static String HTTP_STATUS_METHOD_NOT_ALLOWED_MSG = "Method Not Allowed";
	public final static StatusType HTTP_STATUS_METHOD_NOT_ALLOWED = new StatusType() {

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
	
	@Override
	public StatusType getStatus() {
		return HTTP_STATUS_METHOD_NOT_ALLOWED;
	}
	
}
