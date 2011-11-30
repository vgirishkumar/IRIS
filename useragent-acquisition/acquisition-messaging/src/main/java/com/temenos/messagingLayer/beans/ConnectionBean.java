/**
 * This is a connection bean interface to connect to Agent connector
 */
package com.temenos.messagingLayer.beans;

import java.security.Principal;
import java.util.Set;

import com.jbase.jremote.JRemoteException;
import com.temenos.messagingLayer.exceptions.ConnectionException;

/**
 * @author vsangeetha1
 * 
 */
public interface ConnectionBean {
	// Sets-up a connection to the server
	public Response setupServer(String serverIPAddress, int serverPortNo) throws JRemoteException;

	// Sends an XML request to the server from the specified client IP and returns the reply
	public Response talkToServer(String xmlRequest, String clientIP) throws JRemoteException;

	// Sends an XML request to the server from the specified client IP in secure mode and returns the reply
	public Response talkToServer(String xmlRequest, String clientIP, Principal principal) throws JRemoteException;

	// Sends an XML request to the server from the specified client IP and returns the reply
	public Response talkToServerOfs(String xmlRequest, String clientIP) throws JRemoteException;

	// Sends an XML request to the server from the specified client IP in secure mode and returns the reply
	public Response talkToServerOfs(String xmlRequest, String clientIP, Principal principal) throws ConnectionException;

	/**
	 * Get the list of available channels.
	 * If channels are not supported, throws an UnsupportedOperationException.
	 * 
	 * @return a Set of channel names.
	 */
	public Set getChannels() throws UnsupportedOperationException;

}
