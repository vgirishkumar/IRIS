package com.temenos.interaction.test.context;

/**
 * Defines the interaction execution context.
 * 
 * @author ssethupathi
 *
 */
public interface Context {

	/**
	 * Returns the connection configuration.
	 * 
	 * @return connection configuration
	 */
	ConnectionConfig connectionCongfig();

	/**
	 * Returns the registry for content handlers.
	 * 
	 * @return content handlers.
	 */
	ContentTypeHandlers entityHandlersRegistry();
}
