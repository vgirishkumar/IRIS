package com.temenos.interaction.test.http;

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


public class HttpRequestImpl implements HttpRequest {

	private HttpHeader header;
	private String payload;

	public HttpRequestImpl(HttpHeader header, String payload) {
		this.header = header;
		this.payload = payload;
	}

	public HttpRequestImpl(HttpHeader header) {
		this.header = header;
		this.payload = "";
	}

	@Override
	public HttpHeader headers() {
		return header;
	}

	@Override
	public String payload() {
		return payload;
	}

	@Override
	public String toString() {
		return "PayloadRequest [header=" + header + ", payload=" + payload
				+ "]";
	}
}
