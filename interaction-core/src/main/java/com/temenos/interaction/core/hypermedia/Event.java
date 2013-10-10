package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
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


import javax.ws.rs.HttpMethod;

/**
 * An Event has a name and it mapped to an http method that can be used to apply
 * the event to a {@link ResourceState}
 * @author aphethean
 */
public class Event {

	private final String name;
	private final String method;
	
	public Event(String name, String method) {
		this.name = name;
		this.method = method;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMethod() {
		return method;
	}

	public String toString() {
		return "Event(name=\"" + name + "\", method=\"" + method + "\")";
	}

	public boolean isSafe() {
		return getMethod().equals(HttpMethod.GET) || getMethod().equals(HttpMethod.OPTIONS) || getMethod().equals(HttpMethod.HEAD);
	}

	public boolean isUnSafe() {
		return !isSafe();
	}

}
