package com.temenos.interaction.core.command;

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

	
	@Override
	public StatusType getStatus() {
		return HttpStatusTypes.METHOD_NOT_ALLOWED;
	}
	
}
