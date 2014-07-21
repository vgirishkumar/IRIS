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


import java.util.Properties;

public class Action {

	public enum TYPE {
		VIEW,
		ENTRY
	}
	
	private final String name;
	private final TYPE type;
	private Properties properties;
	private String method;
	
	public Action(String name, TYPE type) {
		this.name = name;
		this.type = type;
	}

	public Action(String name, TYPE type, Properties props) {
		this.name = name;
		this.type = type;
		this.properties = props;
	}

	public Action(String name, TYPE type, Properties props, String method) {
		this.name = name;
		this.type = type;
		this.properties = props;
		this.method = method;
	}

	public String getName() {
		return name;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public String getMethod() {
		return method;
	}
	
	public String toString() {
		return "Action(name=\"" + name + "\", type=\"" + type + "\", method=\"" + method + "\")";
	}
}
