package com.interaction.example.odata.airline;

/*
 * #%L
 * interaction-example-odata-airline
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.jersey.consumer.behaviors.JerseyClientBehavior;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.Filterable;

/**
 * Extended OClientBehaviour to enable us to handle headers such as E-Tags.
 */
public class ExtendedOClientBehaviour implements JerseyClientBehavior {

	//Request headers
	private String ifNoneMatch;
	private String ifMatch;
	
	//Response headers
	private String etag;
	
	@Override
	public ODataClientRequest transform(ODataClientRequest request) {
		if (ifNoneMatch != null) {
			request.header(HttpHeaders.IF_NONE_MATCH, ifNoneMatch);
		}
		if (ifMatch != null) {
			request.header(HttpHeaders.IF_MATCH, ifMatch);
		}
		return request;
	}

	@Override
	public void modify(ClientConfig clientConfig) {
	}

	@Override
	public void modifyWebResourceFilters(Filterable filterable) {
	}

	@Override
	public void modifyClientFilters(Filterable client) {
		client.addFilter(new ClientFilter() {
			@Override
			public ClientResponse handle(ClientRequest clientRequest) throws ClientHandlerException {
				ClientResponse response = getNext().handle(clientRequest);
				MultivaluedMap<String, String> responseHeaders = response.getHeaders();
				etag = responseHeaders.getFirst(HttpHeaders.ETAG);
				return response;
			}
		});
	}
	
	public void setIfNoneMatch(String ifNoneMatch) {
		this.ifNoneMatch = ifNoneMatch;
	}

	public void setIfMatch(String ifMatch) {
		this.ifMatch = ifMatch;
	}
	
	public String getEtag() {
		return etag;
	}
}
