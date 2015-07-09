package com.temenos.interaction.core.entity;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
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
import java.io.InputStream;

import javax.ws.rs.core.MultivaluedMap;


public class StreamingInput {
	private String entityName;
	private InputStream input;
	private MultivaluedMap<String, String> headers;

	/**
	 * @param entityName
	 * @param headers 
	 * @param parts 
	 */
	public StreamingInput(String entityName, InputStream input, MultivaluedMap<String, String> headers) {
		this.entityName = entityName;
		this.input = input;
		this.headers = headers;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @return the input
	 */
	public InputStream getInput() {
		return input;
	}

	/**
	 * @return the headers
	 */
	public MultivaluedMap<String, String> getHeaders() {
		return headers;
	}
}
