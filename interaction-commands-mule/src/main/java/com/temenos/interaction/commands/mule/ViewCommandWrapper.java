package com.temenos.interaction.commands.mule;

/*
 * #%L
 * interaction-commands-mule
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


import javax.ws.rs.core.MultivaluedMap;

public class ViewCommandWrapper {

	private final MultivaluedMap<String, String> pathParams;
	private final MultivaluedMap<String, String> queryParams;
	
	public ViewCommandWrapper(MultivaluedMap<String, String> pathParams, MultivaluedMap<String, String> queryParams) {
		this.pathParams = pathParams;
		this.queryParams = queryParams;
	}
	
	public MultivaluedMap<String, String> getPathParameters() {
		return pathParams;
	}
	
	public MultivaluedMap<String, String> getQueryParameters() {
		return queryParams;
	}
}
