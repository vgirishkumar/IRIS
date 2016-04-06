package com.temenos.useragent.generic.http;

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


/**
 * Defines the Http client used for the interactions.
 * 
 * @author ssethupathi
 *
 */
public interface HttpClient {

	/**
	 * Http GET method executes {@link HttpRequest request}.
	 * 
	 * @param url
	 * @param request
	 * @return response
	 */
	HttpResponse get(String url, HttpRequest request);

	/**
	 * Http POST method executes {@link HttpRequest request}.
	 * 
	 * @param url
	 * @param request
	 * @return response
	 */

	HttpResponse post(String url, HttpRequest request);

	/**
	 * Http PUT method executes {@link HttpRequest request}.
	 * 
	 * @param url
	 * @param request
	 * @return response
	 */
	HttpResponse put(String url, HttpRequest request);
}
