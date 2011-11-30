package com.temenos.messagingLayer.talkToServer;

import com.temenos.messagingLayer.beans.ConnectionBean;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.messagingLayer.securityContext.SecurityContext;

public class TalkToServer {
	protected ConnectionBean t24ConnectionBean;
	private SecurityContext securityContext;

	/**
	 * Injects a connection
	 * 
	 * @param t24ConnectionBean
	 */
	public void setT24ConnectionBean(ConnectionBean t24ConnectionBean) {
		this.t24ConnectionBean = t24ConnectionBean;
	}

	public void setSecurityContext(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	public SecurityContext getSecurityContext() {
		return securityContext;
	}

//	HttpServletRequest requestObj = null; // not used; there is a TODO in the JMSConnector; we'll see how that is solved and then decide if we revive the HttpRequest.

	public Response sendOfsRequestToServer(String ofsRequest) {
		// Send OFS Request to the server
		Response ofsReply = null;
		try {
			ofsReply = t24ConnectionBean.talkToServer(ofsRequest, null);
			return (ofsReply);
		} catch (Exception e) {
			throw new RuntimeException("Error talking to server", e);
		}
	}
}