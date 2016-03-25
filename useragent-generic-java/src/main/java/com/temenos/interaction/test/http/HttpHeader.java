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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {

	private Map<String, List<String>> headers = new HashMap<String, List<String>>();

	public Collection<String> names() {
		return headers.keySet();
	}

	public void set(String name, String value) {
		List<String> values = headers.get(name);
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(value);
		headers.put(name, values);
	}

	public String get(String name) {
		if (headers.containsKey(name)) {
			List<String> values = headers.get(name);
			if (values.size() > 0) {
				return values.get(0);
			}
		}
		return "";
	}

	public String toString() {
		return headers.toString();
	}
}
