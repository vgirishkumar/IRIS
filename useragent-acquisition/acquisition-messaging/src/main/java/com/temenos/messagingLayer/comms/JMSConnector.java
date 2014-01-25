package com.temenos.messagingLayer.comms;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.temenos.messagingLayer.beans.ConnectionBean;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.messagingLayer.exceptions.ConnectionException;

/**
 * <p>
 * This class looks up the OFS JMS queue, sends OFS messages to T24 via this queue and waits for the reply on a
 * temporary queue.
 * </p>
 */
public class JMSConnector implements ConnectionBean, Serializable {
	private static final long serialVersionUID = 1L;

	/** The Constant LOGGER. */
	private static final Log LOGGER = LogFactory.getLog(JMSConnector.class);

	private final static String JMS_TYPE = "BROWSER.XML";

	/** 'ConnectionTimeout'. */
	public static final String PARAM_CONNECT_TIMEOUT = "ConnectionTimeout";

	long connectionTimeout = 60000L; // Default timeout is 60 seconds

	private int retryInterval = 2000; // Default retry interval is 2 seconds

	// Default Constructor
	/**
	 * Instantiates a new instance connector.
	 */
	public JMSConnector() {
	}

	/**
	 * Instantiates a new instance connector.
	 * 
	 * @param context
	 *            the context
	 * @param ivParameters
	 *            the iv parameters
	 */
	

	// Sets-up a connection to the server
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.t24browser.comms.ConnectionBean#setupServer(java.lang.String
	 * ,int)
	 */
	public Response setupServer(String sInstanceName, int timeOutSecs) {
		Response myResponse = new Response();
		myResponse.setMsg("");
		return myResponse;
	}

	/*
	 * Default implementation - clientIP discarded
	 * 
	 * @see
	 * com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String
	 * ,java.lang.String)
	 */
	public Response talkToServer(String xmlString, String clientIP) throws ConnectionException {
		return sendMessage(xmlString, "", null);
	}

	/*
	 * Method not required - principal available via container security
	 * 
	 * @see
	 * com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String
	 * , java.lang.String, java.security.Principal)
	 */
	public Response talkToServer(String xmlString, String clientIP, Principal principal) throws ConnectionException {
		return sendMessage(xmlString, "", principal);
	}

	/*
	 * Default implementation - clientIP discarded
	 * 
	 * @see
	 * com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String
	 * ,java.lang.String)
	 */
	public Response talkToServerOfs(String ofs, String clientIP) throws ConnectionException {
		return sendMessage(ofs, "", null);
	}

	/*
	 * Method not required - principal available via container security
	 * 
	 * @see
	 * com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String
	 * , java.lang.String, java.security.Principal)
	 */
	public Response talkToServerOfs(String ofs, String clientIP, Principal principal) throws ConnectionException {
		return sendMessage(ofs, "", principal);
	}

	/**
	 * Send message.
	 * 
	 * @param msg
	 *            the msg
	 * @return the browser response
	 */
	private Response sendMessage(String msg, String header, Principal principal) throws ConnectionException {
		Response myResponse = new Response();
		String strOFSRequest = header + msg;
		ServiceLocator serviceLocator = null;
		ConnectionFactory cxf = null;
		Connection connection = null;
		Session session = null;

		// Obtain the username from the principal
		String t24principal = "";
		if (principal != null) {
			t24principal = principal.getName();
		}

		// JMS connection
		MessageConsumer receiver = null;
		MessageProducer producer = null;
		try {
			// lookup JMS factory and create connection to queue
			serviceLocator = ServiceLocator.getInstance();
			cxf = serviceLocator.lookupJMSConnectionFactory();
			connection = cxf.createConnection();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();

			Destination replyQueue = null;
			// getting this system property is for debugging only
			if (System.getProperty("use.jms.temp.queue") != null) {
				/*
				 * Create a queue for reply messages, this is destroyed when we
				 * close the session
				 */
				replyQueue = session.createTemporaryQueue();
				receiver = session.createConsumer(replyQueue);
			} else {
				replyQueue = serviceLocator.lookupOFSReplyDestination();
			}

			/*
			 * Lookup our BrowserML queue, create producer
			 */
			Destination destination = null;
			destination = serviceLocator.lookupOFSDestination();
			producer = session.createProducer(destination);

			/*
			 * Send our OFS request to the destination
			 */
			Message jmsMsg = session.createTextMessage(strOFSRequest);
			jmsMsg.setJMSReplyTo(replyQueue);
			// make sure messages get an id
			producer.setDisableMessageID(false);
			/*
			 * WARNING - The mechanism of simply letting the consumer set the
			 * correlation id to the value of the message id is not portable. We
			 * must generate and set our own unique correlation id. The JMS api
			 * states that the message id is an implementation specific detail
			 * that may change. A quick search of the web shows it does change
			 * in Weblogic depending on the state of the message.
			 */
			String correlationId = getUniqueCorrID();
			jmsMsg.setJMSCorrelationID(correlationId);

			// Set the user ID
			jmsMsg.setStringProperty("T24_PRINCIPAL", t24principal);
			jmsMsg.setStringProperty("FORMAT", "OFSML");
			/*
			 * Set a message type - this may give the consumer the opportunity
			 * to decide whether it wants to consume this type of message.
			 */
			jmsMsg.setJMSType(JMS_TYPE);
			producer.send(jmsMsg);

			// Wait for the response message
			long timeInMillis = System.currentTimeMillis();
			int retries = 0;
			boolean done = false;
			Message reply = null;
			while (!done && (System.currentTimeMillis() - timeInMillis < connectionTimeout)) {
				try {
					/*
					 * Create a receiver that knows the consumer will reply with
					 * a correlation ID that is equal to our message ID. A JMS
					 * connection error will close this consumer
					 */
					receiver = session.createConsumer(replyQueue, "JMSCorrelationID='" + correlationId + "'");

					// Wait for a response message to arrive
					reply = receiver.receive(connectionTimeout);
					done = true;
				} catch (JMSException je) {
					retries++;
					LOGGER.warn("JMS connection has been lost due to [" + retries + "# re-connection attempt]: "
							+ je.getMessage());
					if (retries > 1) {
						try { // Sleep some time to give other requests a chance to get processed
							Thread.sleep(retryInterval);
						} catch (InterruptedException e2) {
							LOGGER.error("Unable to pause execution after connection error: " + e2.getMessage());
						}
					}

					// Reconnect
					try {
						try {
							connection.close();
						} catch (JMSException je2) {
							LOGGER.info("Consumer may have already be closed at this point "+ je2.getMessage());
							// Ignore this since consumer may already be closed
							// at this point
						}
						cxf = serviceLocator.lookupJMSConnectionFactory();
						connection = cxf.createConnection();
						session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
						connection.start();
						replyQueue = serviceLocator.lookupOFSReplyDestination();
						LOGGER.info("A new JMS connection has been created. Resuming message consumption from reply queue.");
					} catch (JMSException je2) {
						LOGGER.error("Unable to re-establish a JMS connection [" + retries
								+ "# re-connection attempt]\n" + je2.getMessage());
					}
				}
			} // while

			if (reply != null && reply instanceof TextMessage) {
				myResponse.setMsg(((TextMessage) reply).getText());
			} else {
				String errMsg = "Connection timeout or invalid message type returned from JMS queue.";
				LOGGER.error(errMsg);
				myResponse.setErrorText(errMsg);
				myResponse.setMsg(strOFSRequest);
			}

			// Close JMS connection
			closeResource(session);
			closeResource(connection);
		} catch (JMSException e) {
			closeResource(session);
			closeResource(connection);
			// Throw connection exception to trigger re-sending the OFS request
			throw new ConnectionException(e.getMessage());
		}

		return myResponse;
	}

	protected String getUniqueCorrID() {
		/*
		 * The problem: Random seeded with System.currentTimeMillis will generate the same IDs for all calls generated
		 * during the same millisecond (or even during a range of ms, if the System.currentTimeMillis resolution is
		 * bigger than 1 ms).
		 * 
		 * The solution (until proven otherwise): use UUID, available natively in Java 5, which has close to nil
		 * collision probability.
		 * 
		 * Concatenating the session id to the returned result would limit the scope of collision within the current
		 * user's context, but the HTTP request/session do not belong in this layer, so we cannot use them.
		 */
		return UUID.randomUUID().toString();
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
		throw new UnsupportedOperationException("JMSConnector does not support multiple channels");
	}

	private void closeResource(Connection cx) {
		try {
			if (cx != null) {
				cx.close();
			}
		} catch (JMSException e) {
			
			LOGGER.info("Connection already closed "+e.getMessage());
			/*
			 * ignore an exception on close, we do not want to hide the real
			 * exception
			 */
		}
	}

	private void closeResource(Session s) {
		try {
			if (s != null) {
				s.close();
			}
		} catch (JMSException e) {
			LOGGER.info("Connection already closed "+e.getMessage());
			/*
			 * ignore an exception on close, we do not want to hide the real
			 * exception
			 */
		}
	}

	/* Spring setters */
	/**
	 * Set the JMS connection timeout (in milliseconds) 
	 */
	public void setConnectionTimeout(long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * Set the JMS message retry interval (in milliseconds) 
	 */
	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}
	/* end Spring setters */
}
