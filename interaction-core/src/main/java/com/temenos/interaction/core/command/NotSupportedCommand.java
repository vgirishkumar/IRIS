package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * An error from Hypertext Transfer Protocol -- HTTP/1.1
 * RFC 2616 Fielding, et al.
 * 
 * 10.5.2 501 Not Implemented
 * 
 * The server does not support the functionality required to fulfill the request. This 
 * is the appropriate response when the server does not recognize the request method 
 * and is not capable of supporting it for any resource.
 */
public final class NotSupportedCommand implements ResourceStatusCommand {

	public final static String HTTP_STATUS_NOT_IMPLEMENTED_MSG = "Not Implemented";
	public final static StatusType HTTP_STATUS_NOT_IMPLEMENTED = new StatusType() {

		public int getStatusCode() {
			return 501;
		}

		public Family getFamily() {
			return Response.Status.Family.CLIENT_ERROR;
		}

		public String getReasonPhrase() {
			return "Not Implemented";
		}
		
	};
	
	@Override
	public StatusType getStatus() {
		return HTTP_STATUS_NOT_IMPLEMENTED;
	}
	
}
