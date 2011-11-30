package com.temenos.messagingLayer.exceptions;

import com.jbase.jremote.JRemoteException;

/**
 * General exception class for connection errors
 */
public class ConnectionException extends JRemoteException {

	public ConnectionException() {
		super();
	}

	public ConnectionException(String msg) {
		super(msg);
	}
}
