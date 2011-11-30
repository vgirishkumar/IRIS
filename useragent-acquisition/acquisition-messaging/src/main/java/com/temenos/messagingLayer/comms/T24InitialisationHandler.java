/**
 * 
 */
package com.temenos.messagingLayer.comms;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jbase.jremote.JConnection;
import com.jbase.jremote.JConnectionCallbackHandler;
import com.jbase.jremote.JRemoteException;
import com.jbase.jremote.JSubroutineNotFoundException;

/**
 * @author vsangeetha1
 * 
 */
public class T24InitialisationHandler implements JConnectionCallbackHandler {

	private static final String SERVER_INITIALISE_ROUTINE = "JF.INITIALISE.CONNECTION";

	/** The Constant LOGGER. */
	private static final Log LOGGER = LogFactory.getLog(T24InitialisationHandler.class);

	/**
	 * <p>
	 * Initialise a connection for the supplied Subject.
	 * </p>
	 * <p>
	 * This method is called when the T24 connection (jBASE process) is first created. We can initialise common
	 * variables and setup connection variables that are based on the subject. If this same subject makes another
	 * request to T24 - JRemote is able to reuse the connection and will call T24InitialisationHandler#reuse() before
	 * doing so.
	 * </p>
	 */
	public boolean initialise(Subject subject, JConnection connection) {
		try {
			connection.call(SERVER_INITIALISE_ROUTINE, null);
			/*
			 * We could also sign the Subject in properly. However, in
			 * most cases the web app security realm is not used
			 */
			/*
			 * JSubroutineParameters params = new JSubroutineParameters();
			 * params.add(new JDynArray(userName));
			 * params.add(new JDynArray(password));
			 * JDynArray errorReturn = new JDynArray();
			 * params.add(errorReturn);
			 * params = connection.call("JF.VALIDATE.SIGN.ON", params);
			 * String error = params.get(2).get(1);
			 * if (error != null && error.length() > 0) {
			 * LOGGER.info("*** error calling JF.VALIDATE.SIGN.ON: " + error);
			 * return false;
			 * }
			 */
			return true;
		} catch (JSubroutineNotFoundException e) {
			LOGGER.error("Error calling JF.INITIALISE.CONNECTION "+e.getMessage());
		} catch (JRemoteException e) {
			LOGGER.error("Error talking to t24 server "+e.getMessage());
		}
		return false;
	}

	/**
	 * <p>
	 * The Subject intends to use a connection that was previously initialised for another Subject.
	 * </p>
	 */
	public boolean reinitialise(Subject subject, JConnection connection) {
		// we could switch the user here
		return false;
	}

	/**
	 * <p>
	 * The Subject is reusing the supplied connection.
	 * </p>
	 */
	public boolean reuse(Subject subject, JConnection connection) {
		// reset common variables?
		return true;
	}
}
