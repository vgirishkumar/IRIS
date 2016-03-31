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

import com.temenos.useragent.generic.http.HttpClient;
import com.temenos.useragent.generic.http.HttpHeader;

/**
 * Defines the context which runs through the interaction session.
 * 
 * @author ssethupathi
 *
 */
public interface SessionContext {

	/**
	 * Returns the http header to use in the request.
	 * 
	 * @return http header
	 */
	HttpHeader getRequestHeader();

	/**
	 * Returns the http client to be used for executing the request.
	 * 
	 * @return http client
	 */
	HttpClient getHttpClient();

	/**
	 * Returns the entity to use in the request or null of no entity to be used
	 * in the request.
	 * 
	 * @return entity
	 */
	EntityWrapper getRequestEntity();

	/**
	 * Sets the response data after having got the request executed.
	 * 
	 * @param response
	 */
	void setResponse(ResponseData response);

	/**
	 * Returns the response or null if no response available.
	 * 
	 * @return response.
	 */
	ResponseData getResponse();

}
