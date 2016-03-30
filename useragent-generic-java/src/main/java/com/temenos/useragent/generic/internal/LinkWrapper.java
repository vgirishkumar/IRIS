package com.temenos.useragent.generic.internal;

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


import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.Url;

public class LinkWrapper implements ActionableLink {

	private Link link;
	private SessionContext sessionCallback;

	public LinkWrapper(Link link, SessionContext sessionCallback) {
		this.link = link;
		this.sessionCallback = sessionCallback;
	}

	@Override
	public String title() {
		return link.title();
	}

	@Override
	public String href() {
		return link.href();
	}

	@Override
	public String rel() {
		return link.rel();
	}

	@Override
	public String id() {
		return link.id();
	}

	@Override
	public boolean hasEmbeddedPayload() {
		return link.hasEmbeddedPayload();
	}

	@Override
	public Payload embedded() {
		return link.embedded();
	}

	@Override
	public String baseUrl() {
		return link.baseUrl();
	}

	@Override
	public Url url() {
		return new UrlWrapper(baseUrl() + href(), sessionCallback);
	}
}
