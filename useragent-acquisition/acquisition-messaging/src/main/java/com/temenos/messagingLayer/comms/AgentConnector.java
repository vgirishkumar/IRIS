package com.temenos.messagingLayer.comms;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

import com.jbase.jremote.JRemoteException;
import com.temenos.messagingLayer.beans.ConnectionBean;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.tocf.t24ra.T24ConnectionFactory;
import com.temenos.tocf.t24ra.T24Exception;

/**
 * <p>
 * Connector which uses the T24 resource adapter to send OFS request messages to T24.
 * </p>
 * 
 * @author
 */
public class AgentConnector implements ConnectionBean, Serializable {
	/** The Constant LOGGER. */

	private static final long serialVersionUID = 1L;

	/** 'ConnectionTimeout' */
	public static final String PARAM_CONNECT_TIMEOUT = "ConnectionTimeout";

	/** The service locator */
	// TODO : see how to add this as a bean or as a constructor argument
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();

	/**
	 * Instantiates a new instance connector.
	 */
	public AgentConnector() {
		// serviceLocator = ServiceLocator.getInstance();
	}

	public Response setupServer(String serverIPAddress, int serverPortNo) throws JRemoteException {
		throw new UnsupportedOperationException("Not implemented");
	}

	/*
	 * Default implementation - clientIP discarded
	 * 
	 * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,java.lang.String)
	 */
	public Response talkToServer(String xmlString, String clientIP) {
		return sendMessage(xmlString, "");
	}

	/*
	 * Method not required - principal available via container security
	 * 
	 * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,
	 * java.lang.String, java.security.Principal)
	 */
	public Response talkToServer(String xmlString, String clientIP, Principal principal) {
		return sendMessage(xmlString, "");
	}

	/*
	 * Default implementation - clientIP discarded
	 * 
	 * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,java.lang.String)
	 */
	public Response talkToServerOfs(String ofs, String clientIP) {
		return sendMessage(ofs, "");
	}

	/*
	 * Method not required - principal available via container security
	 * 
	 * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,
	 * java.lang.String, java.security.Principal)
	 */
	public Response talkToServerOfs(String ofs, String clientIP, Principal principal) {
		return sendMessage(ofs, "");
	}

	/**
	 * Get the timeout value specifying the number of seconds
	 * to wait for a response message.
	 * 
	 * @return timeout in seconds
	 */

	/**
	 * Send the OFS request message.
	 * 
	 * @param msg
	 *            OFS request
	 * @param header
	 *            OFS request header
	 * @param principal
	 *            Principal associated to this request
	 * @return the browser response
	 */
	private Response sendMessage(String msg, String header) {
		Response myResponse = new Response();
		String strOFSRequest = header + msg;
		String strOFSMLResponse = null;
		T24ConnectionFactory cxf = null;
		// Process OFS request
		try {
			// long started = System.currentTimeMillis();
			cxf = serviceLocator.lookupT24ConnectionFactory();
			try {
				strOFSMLResponse = cxf.processOFSMLRequest(strOFSRequest);
			} catch (T24Exception e) {
				if (e.getMessage() != null && e.getMessage().indexOf("ManagedConnectionFactory is null") > 0) {
					/*
					 * datasource settings have changed - Note this message string is a JBoss message,
					 * but this resetting of the connection factory would only be used in development anyway
					 */
					serviceLocator.removeT24ConnectionFactory();
					cxf = serviceLocator.lookupT24ConnectionFactory();
					strOFSMLResponse = cxf.processOFSMLRequest(strOFSRequest);
				} else {
					throw e;
				}
			}

			// Format response

			// set the OFS response
			myResponse.setMsg(strOFSMLResponse);
			// long elapsed = System.currentTimeMillis() - started;

		} catch (T24Exception e) {
			throw new RuntimeException("T24 exception", e);
		}
		return myResponse;
	}

	/**
	 * Get the list of available channels. If channels are not supported, throws
	 * an UnsupportedOperationException.
	 * 
	 * @return a Set of channel names.
	 * 
	 * @throws UnsupportedOperationException
	 *             the unsupported operation exception
	 */
	public Set<?> getChannels() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("AgentConnector does not support multiple channels");
	}

}
