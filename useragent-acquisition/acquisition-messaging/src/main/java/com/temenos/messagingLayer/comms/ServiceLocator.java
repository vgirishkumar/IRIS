package com.temenos.messagingLayer.comms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jbase.jremote.JConnectionFactory;
import com.temenos.tocf.t24ra.T24ConnectionFactory;

public class ServiceLocator {
	public final static String T24CONNECTION_FACTORY_JNDI_NAME = "java:comp/env/jca/t24ConnectionFactory";
	public final static String JCONNECTION_FACTORY_JNDI_NAME = "java:comp/env/jca/t24ConnectionFactory";
	public final static String JMS_CONNECTION_FACTORY_JNDI_NAME = "java:comp/env/jms/jmsConnectionFactory";
	public final static String OFS_DESTINATION_JNDI_NAME = "java:comp/env/queue/t24OFSQueue";
	public final static String OFS_REPLY_DESTINATION_JNDI_NAME = "java:comp/env/queue/t24OFSReplyQueue";

	private static ServiceLocator _instance = null;

	private InitialContext _context = null;

	private Map<String, T24ConnectionFactory> _cacheT24ConnectionFactory = null;
	private Map<String, JConnectionFactory> _cacheJConnectionFactory = null;
	private Map<String, ConnectionFactory> _cacheJMSConnectionFactory = null;
	private Map<String, Destination> _cacheDestination = null;

	private Properties _cacheT24ConnectionProperties = null; // Cached T24 RA connection properties

	public ServiceLocator() {
		try {
			_context = new InitialContext();
		} catch (NamingException e) {
			throw new RuntimeException("Configuration error - cannot create initial context");
		}
		_cacheT24ConnectionFactory = Collections.synchronizedMap(new HashMap<String, T24ConnectionFactory>());
		_cacheJConnectionFactory = Collections.synchronizedMap(new HashMap<String, JConnectionFactory>());
		_cacheJMSConnectionFactory = Collections.synchronizedMap(new HashMap<String, ConnectionFactory>());
		_cacheDestination = Collections.synchronizedMap(new HashMap<String, Destination>());
	}

	public static synchronized ServiceLocator getInstance() {
		if (_instance == null) {
			_instance = new ServiceLocator();
		}
		return _instance;
	}

	public JConnectionFactory getJConnectionFactory(String name) {
		JConnectionFactory cxFactory = null;
		try {
			if (_cacheJConnectionFactory.containsKey(JCONNECTION_FACTORY_JNDI_NAME)) {
				cxFactory = _cacheJConnectionFactory.get(JCONNECTION_FACTORY_JNDI_NAME);
			} else {
				Object factoryObj = _context.lookup(JCONNECTION_FACTORY_JNDI_NAME);
				cxFactory = (JConnectionFactory) factoryObj;
				_cacheJConnectionFactory.put(JCONNECTION_FACTORY_JNDI_NAME, cxFactory);
			}
		} catch (NamingException e) {
			// LOGGER.fatal("Configuration error " + e.getMessage(), e);
			throw new RuntimeException("Configuration error [" + JCONNECTION_FACTORY_JNDI_NAME + "] " + e.getMessage());
		}
		return cxFactory;
	}

	public JConnectionFactory lookupJConnectionFactory() {
		JConnectionFactory cxFactory = null;
		cxFactory = getJConnectionFactory(JCONNECTION_FACTORY_JNDI_NAME);
		return cxFactory;
	}

	public void removeJConnectionFactory() {
		_cacheJConnectionFactory.remove(JCONNECTION_FACTORY_JNDI_NAME);
	}

	public ConnectionFactory getJMSConnectionFactory(String name) {
		ConnectionFactory cxFactory = null;
		try {
			if (_cacheJMSConnectionFactory.containsKey(name)) {
				cxFactory = _cacheJMSConnectionFactory.get(name);
			} else {
				Object factoryObj = _context.lookup(name);
				cxFactory = (ConnectionFactory) factoryObj;
				_cacheJMSConnectionFactory.put(name, cxFactory);
			}
		} catch (NamingException e) {
			// LOGGER.fatal("Configuration error " + e.getMessage(), e);
			throw new RuntimeException("Configuration error [" + name + "] " + e.getMessage());
		}

		return cxFactory;
	}

	public ConnectionFactory lookupJMSConnectionFactory() {
		ConnectionFactory cxFactory = null;
		cxFactory = getJMSConnectionFactory(JMS_CONNECTION_FACTORY_JNDI_NAME);
		return cxFactory;
	}

	public void removeJMSConnectionFactory() {
		_cacheJMSConnectionFactory.remove(JMS_CONNECTION_FACTORY_JNDI_NAME);
	}

	public Destination lookupDestination(String name) {
		Destination destination = null;
		try {
			if (_cacheDestination.containsKey(name)) {
				destination = _cacheDestination.get(name);
			} else {
				Object obj = _context.lookup(name);
				destination = (Destination) obj;
				_cacheDestination.put(name, destination);
			}
		} catch (NamingException e) {
			// LOGGER.fatal("Configuration error " + e.getMessage(), e);
			throw new RuntimeException("Configuration error [" + name + "] " + e.getMessage());
		}

		return destination;
	}

	public void removeOFSDestination(String name) {
		_cacheDestination.remove(name);
	}

	public Destination lookupOFSDestination() {
		Destination destination = lookupDestination(OFS_DESTINATION_JNDI_NAME);
		return destination;
	}

	public Destination lookupOFSReplyDestination() {
		Destination destination = lookupDestination(OFS_REPLY_DESTINATION_JNDI_NAME);
		return destination;
	}

	public void removeOFSReplyDestination() {
		_cacheDestination.remove(OFS_REPLY_DESTINATION_JNDI_NAME);
	}

	public T24ConnectionFactory getT24ConnectionFactory(String name) {
		T24ConnectionFactory cxFactory = null;
		try {
			if (_cacheT24ConnectionFactory.containsKey(name)) {
				cxFactory = _cacheT24ConnectionFactory.get(name);
			} else {
				Object factoryObj = _context.lookup(name);
				cxFactory = (T24ConnectionFactory) factoryObj;
				_cacheT24ConnectionFactory.put(name, cxFactory);
			}
		} catch (NamingException e) {
			// LOGGER.fatal("Configuration error " + e.getMessage(), e);
			throw new RuntimeException("Configuration error [" + name + "] " + e.getMessage());
		}
		return cxFactory;
	}

	public T24ConnectionFactory lookupT24ConnectionFactory() {
		T24ConnectionFactory cxFactory = getT24ConnectionFactory(T24CONNECTION_FACTORY_JNDI_NAME);
		return cxFactory;
	}

	public void removeT24ConnectionFactory() {
		_cacheT24ConnectionFactory.remove(T24CONNECTION_FACTORY_JNDI_NAME);
		_cacheT24ConnectionProperties = null;
	}

	public Properties getT24ConnectionProperties() {
		if (_cacheT24ConnectionProperties == null) {
			try {
				Object factoryObj = _context.lookup(T24CONNECTION_FACTORY_JNDI_NAME);
				JConnectionFactory cxFactory = (JConnectionFactory) factoryObj;
				if (cxFactory != null) {
					_cacheT24ConnectionProperties = cxFactory.getConnectionProperties();
				}
			} catch (NamingException e) {
				throw new RuntimeException("Configuration error [" + T24CONNECTION_FACTORY_JNDI_NAME + "] "
						+ e.getMessage());
			}
		}
		return _cacheT24ConnectionProperties;
	}
}