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

public class LinkImpl implements Link {

	private String baseUrl;
	private String rel;
	private String href;
	private String title;
	private String id;
	private boolean hasEmbeddedPayload;
	private Payload embeddedPayload;

	private LinkImpl(Builder builder) {
		this.baseUrl = builder.baseUrl == null ? "" : builder.baseUrl;
		this.rel = builder.rel == null ? "" : builder.rel;
		this.href = builder.href == null ? "" : builder.href;
		this.title = builder.title == null ? "" : builder.title;
		this.id = builder.id == null ? "" : builder.id;
		this.embeddedPayload = builder.embeddedPayload;
		this.hasEmbeddedPayload = this.embeddedPayload == null ? false : true;
	}

	@Override
	public String href() {
		return href;
	}

	@Override
	public boolean hasEmbeddedPayload() {
		return hasEmbeddedPayload;
	}

	@Override
	public Payload embedded() {
		return embeddedPayload;
	}

	@Override
	public String rel() {
		return rel;
	}

	@Override
	public String baseUrl() {
		return baseUrl;
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String toString() {
		return "LinkImpl [rel=" + rel + ", href=" + href + ", title=" + title
				+ ", baseUrl=" + baseUrl + ", hasEmbeddedPayload="
				+ hasEmbeddedPayload + "]";
	}

	public static class Builder {
		private String id = "";
		private String rel = "";
		private String title = "";
		private String href = "";
		private String baseUrl = "";
		private Payload embeddedPayload;

		public Builder(String href) {
			this.href = href;
		}

		public Builder rel(String rel) {
			this.rel = rel;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder payload(Payload payload) {
			this.embeddedPayload = payload;
			return this;
		}

		public Link build() {
			return new LinkImpl(this);
		}
	}
}
