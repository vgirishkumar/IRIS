package com.temenos.interaction.test.context;

import com.temenos.interaction.test.mediatype.AtomFeedHandler;
import com.temenos.interaction.test.mediatype.PlainTextHandler;

/**
 * Factory for accessing the {@link Context context}.
 * 
 * @author ssethupathi
 *
 */
public class ContextFactory {

	private static ThreadLocal<ContextFactory> currentContextFactory = new ThreadLocal<ContextFactory>() {
		@Override
		protected ContextFactory initialValue() {
			return new ContextFactory();
		}
	};

	private ContextImpl context;

	private ContextFactory() {
		BaseConnectionConfig baseConnConfig = new BaseConnectionConfig();
		SystemConnectionConfig sysConnConfig = new SystemConnectionConfig(
				baseConnConfig);
		context = new ContextImpl(sysConnConfig);
	}

	/**
	 * Returns the {@link ContextFactory context factory} associated to the
	 * current thread.
	 * 
	 * @return context factory
	 */
	public static ContextFactory get() {
		return currentContextFactory.get();
	}

	/**
	 * Sets the connection property for the session.
	 * 
	 * @param name
	 * @param value
	 */
	public void setConnectionProperty(String name, String value) {
		context.setSessionProperty(name, value);
	}

	/**
	 * Returns the execution {@link Context context}.
	 * 
	 * @return context
	 */
	public Context getContext() {
		return context;
	}

	public static class ContextImpl implements Context {
		private SessionConnectionConfig connectionConfig;

		private ContextImpl(ConnectionConfig connConfig) {
			this.connectionConfig = new SessionConnectionConfig(connConfig);
		}

		@Override
		public ConnectionConfig connectionCongfig() {
			return connectionConfig;
		}

		@Override
		public ContentTypeHandlers entityHandlersRegistry() {
			ContentTypeHandlers registry = new ContentTypeHandlers();
			registry.registerForPayload("application/atom+xml",
					AtomFeedHandler.class);
			registry.registerForPayload("text/plain", PlainTextHandler.class);
			registry.registerForPayload("text/html", PlainTextHandler.class);
			return registry;
		}

		private void setSessionProperty(String name, String value) {
			connectionConfig.setValue(name, value);
		}
	}
}
