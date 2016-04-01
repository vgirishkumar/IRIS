package com.temenos.useragent.generic;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.temenos.useragent.generic.context.ConnectionConfig;
import com.temenos.useragent.generic.context.ContextFactory;
import com.temenos.useragent.generic.http.HttpClient;
import com.temenos.useragent.generic.http.HttpClientFactory;
import com.temenos.useragent.generic.http.HttpHeader;
import com.temenos.useragent.generic.internal.EntityWrapper;
import com.temenos.useragent.generic.internal.NullEntityWrapper;
import com.temenos.useragent.generic.internal.Payload;
import com.temenos.useragent.generic.internal.ResponseData;
import com.temenos.useragent.generic.internal.SessionContext;
import com.temenos.useragent.generic.internal.UrlWrapper;

public class DefaultInteractionSession implements InteractionSession {

	private HttpHeader header;
	private Map<String, String> properties;
	private EntityWrapper entity;
	private SessionContextImpl callback;
	private HttpClient httpClient;

	@Override
	public Url url(String url) {
		return new UrlWrapper(url, callback);
	}

	@Override
	public Url url() {
		return new UrlWrapper(callback);
	}

	@Override
	public InteractionSession registerHandler(String contentType,
			Class<? extends PayloadHandler> handler) {
		ContextFactory.get().getContext().entityHandlersRegistry()
				.registerForPayload(contentType, handler);
		return this;
	}

	@Override
	public InteractionSession header(String name, String... values) {
		header.set(name, values[0]);
		return this;
	}

	@Override
	public InteractionSession set(String propertyName, String propertyValue) {
		properties.put(propertyName, propertyValue);
		return this;
	}

	@Override
	public InteractionSession unset(String propertyName) {
		properties.remove(propertyName);
		return this;
	}

	@Override
	public Entities entities() {
		Payload response = callback.getResponse().body();
		if (response.isCollection()) {
			return new Entities(response.entities());
		} else {
			return new Entities(response.entity());
		}
	}

	@Override
	public InteractionSession reuse() {
		// TODO deep copy of sort
		entity = callback.getResponse().body().entity();
		entity.setSessionCallback(callback);
		return this;
	}

	@Override
	public InteractionSession clear() {
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
	public InteractionSession basicAuth(String username, String password) {
		ContextFactory factory = ContextFactory.get();
		factory.setConnectionProperty(ConnectionConfig.USER_NAME, username);
		factory.setConnectionProperty(ConnectionConfig.PASSWORD, password);
		return this;
	}

	@Override
	public InteractionSession use(EntityWrapper entity) {
		this.entity = entity;
		this.entity.setSessionCallback(callback);
		return this;
	}

	public static InteractionSession newSession() {
		return new DefaultInteractionSession();
	}

	private DefaultInteractionSession() {
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
		callback = new SessionContextImpl(this);
		httpClient = HttpClientFactory.newClient();
	}

	private static class SessionContextImpl implements SessionContext {

		private DefaultInteractionSession parent;
		private ResponseData output;

		private SessionContextImpl(DefaultInteractionSession parent) {
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
		public HttpHeader getRequestHeader() {
			return parent.header;
		}

		@Override
		public EntityWrapper getRequestEntity() {
			// TODO build/modify the entity and return
			EntityWrapper wrapper = parent.entity;
			for (String key : parent.properties.keySet()) {
				wrapper.setValue(key, parent.properties.get(key));
			}
			return wrapper;
		}

		@Override
		public HttpClient getHttpClient() {
			return parent.httpClient;
		}
	}

	@Override
	public void useHttpClient(HttpClient httpClient) {
		if (httpClient == null) {
			throw new IllegalArgumentException("HttpClient is null");
		}
		this.httpClient = httpClient;
	}
}
