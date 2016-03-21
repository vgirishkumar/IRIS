package com.temenos.interaction.test.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.temenos.interaction.test.Entities;
import com.temenos.interaction.test.Entity;
import com.temenos.interaction.test.Link;
import com.temenos.interaction.test.Links;
import com.temenos.interaction.test.PayloadHandler;
import com.temenos.interaction.test.Result;
import com.temenos.interaction.test.Session;
import com.temenos.interaction.test.Url;
import com.temenos.interaction.test.context.ConnectionConfig;
import com.temenos.interaction.test.context.ContextFactory;
import com.temenos.interaction.test.http.HttpHeader;

public class SessionWrapper implements Session {

	private HttpHeader header;
	private Map<String, String> properties;
	private EntityWrapper entity;
	private SessionCallbackImpl callback;

	@Override
	public Url url(String url) {
		return new UrlWrapper(url, callback);
	}

	@Override
	public Url url() {
		return new UrlWrapper(callback);
	}

	@Override
	public Session registerHandler(String contentType,
			Class<? extends PayloadHandler> handler) {
		ContextFactory.get().getContext().entityHandlersRegistry()
				.registerForPayload(contentType, handler);
		return this;
	}

	@Override
	public Session header(String name, String... values) {
		header.set(name, values[0]);
		return this;
	}

	@Override
	public Session set(String propertyName, String propertyValue) {
		properties.put(propertyName, propertyValue);
		return this;
	}

	@Override
	public Entity entity() {
		return entity;
	}

	@Override
	public Entities entities() {
		return new Entities(callback.getResponse().body().entities());
	}

	@Override
	public Session reuse() {
		// TODO deep copy of sort
		entity = callback.getResponse().body().entity();
		entity.setSessionCallback(callback);
		return this;
	}

	@Override
	public Session clear() {
		initialiseToDefaults();
		return this;
	}

	@Override
	public Result result() {
		return callback.getResponse().result();
	}

	@Override
	public String header(String name) {
		return callback.getResponse().header().get(name);
	}

	@Override
	public Links links() {
		return Links.create(payloadLinks(), callback);
	}

	@Override
	public Session basicAuthUser(String username) {
		ContextFactory.get().setConnectionProperty(ConnectionConfig.USER_NAME,
				username);
		return this;
	}

	@Override
	public Session basicAuthPassword(String password) {
		ContextFactory.get().setConnectionProperty(ConnectionConfig.PASSWORD,
				password);
		return this;
	}

	@Override
	public Session use(EntityWrapper entity) {
		this.entity = entity;
		this.entity.setSessionCallback(callback);
		return this;
	}

	public static Session newSession() {
		return new SessionWrapper();
	}

	private SessionWrapper() {
		initialiseToDefaults();
	}

	private List<Link> payloadLinks() {
		if (callback.getResponse().body().isCollection()) {
			return callback.getResponse().body().links();
		} else {
			return entity.links().all();
		}
	}

	private void initialiseToDefaults() {
		header = new HttpHeader();
		properties = new HashMap<String, String>();
		entity = new NullEntityWrapper();
		this.callback = new SessionCallbackImpl(this);
	}

	private static class SessionCallbackImpl implements SessionCallback {

		private SessionWrapper parent;
		private ResponseData output;

		private SessionCallbackImpl(SessionWrapper parent) {
			this.parent = parent;
		}

		@Override
		public void setResponse(ResponseData output) {
			this.output = output;
		}

		public ResponseData getResponse() {
			if (output != null) {
				return output;
			} else {
				throw new IllegalStateException(
						"No response for any interactions found");
			}
		}

		@Override
		public HttpHeader header() {
			return parent.header;
		}

		@Override
		public EntityWrapper entity() {
			// TODO build/modify the entity and return
			EntityWrapper wrapper = parent.entity;
			for (String key : parent.properties.keySet()) {
				wrapper.setValue(key, parent.properties.get(key));
			}
			return wrapper;
		}
	}
}
