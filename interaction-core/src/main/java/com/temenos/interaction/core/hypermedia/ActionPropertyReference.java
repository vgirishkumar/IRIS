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


import java.util.HashMap;
import java.util.Map;

public class ActionPropertyReference {

	private final String key;
	private Map<String, String> properties = new HashMap<String, String>();
	
	public ActionPropertyReference(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	public void addProperty(String name, String value) {
		properties.put(name, value);
	}
}
