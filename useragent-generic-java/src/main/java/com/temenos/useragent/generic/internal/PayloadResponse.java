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


import com.temenos.useragent.generic.Result;
import com.temenos.useragent.generic.http.HttpHeader;

public class PayloadResponse implements ResponseData {

	private HttpHeader header;
	private Result result;
	private Payload body;

	private PayloadResponse(Builder builder) {
		this.header = builder.header;
		this.result = builder.result;
		this.body = builder.body;
	}

	@Override
	public HttpHeader header() {
		return header;
	}

	@Override
	public Result result() {
		return result;
	}

	@Override
	public Payload body() {
		return body;
	}

	public static class Builder {
		private HttpHeader header;
		private Result result;
		private Payload body;

		public Builder(Result result) {
			this.result = result;
		}

		public Builder header(HttpHeader header) {
			this.header = header;
			return this;
		}

		public Builder body(Payload payload) {
			this.body = payload;
			return this;
		}

		public ResponseData build() {
			// TODO validate data?
			return new PayloadResponse(this);
		}
	}
}
