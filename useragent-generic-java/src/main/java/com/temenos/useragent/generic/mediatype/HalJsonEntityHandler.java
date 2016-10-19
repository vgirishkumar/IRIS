package com.temenos.useragent.generic.mediatype;

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


import static com.temenos.useragent.generic.mediatype.HalJsonUtil.initRepresentationFactory;
import static com.temenos.useragent.generic.mediatype.PropertyNameUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.internal.EntityHandler;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

/**
 * An {@link EntityHandler entity handler} implementation for
 * <i>application/hal+json</i> media type.
 * <p>
 * 
 * @see <a href="https://tools.ietf.org/html/draft-kelly-json-hal-08"</a> JSON
 *      Hypertext Application Language
 *      </p>
 * @author ssethupathi
 *
 */

public class HalJsonEntityHandler implements EntityHandler {

	private ReadableRepresentation representation;
	private JSONObject jsonObject;

	@Override
	public String getId() {
		return "";
	}

	@Override
	public List<Link> getLinks() {
		return HalJsonUtil.extractLinks(representation);
	}

	@Override
	public String getValue(String fqPropertyName) {
		String[] pathParts = flattenPropertyName(fqPropertyName);
		JSONObject parent = identifyParentProperty(fqPropertyName, pathParts);
		if (parent != null) {
			String parentPropertyName = pathParts[pathParts.length - 1];
			return parent.optString(parentPropertyName, null);
		}
		return null;
	}

	@Override
	public void setValue(String fqPropertyName, String value) {
		String[] pathParts = flattenPropertyName(fqPropertyName);
		JSONObject parent = checkAndCreateParent(fqPropertyName, pathParts);
		if (parent != null) {
			String parentPropertyName = pathParts[pathParts.length - 1];
			parent.put(parentPropertyName, value);
		}
	}

	@Override
	public int getCount(String fqPropertyName) {
		String[] pathParts = flattenPropertyName(fqPropertyName);
		JSONObject parent = identifyParentProperty(fqPropertyName, pathParts);
		if (parent != null) {
			String parentPropertyName = pathParts[pathParts.length - 1];
			if (parent.optJSONArray(parentPropertyName) != null) {
				return parent.optJSONArray(parentPropertyName).length();
			}
		}
		return 0;
	}

	@Override
	public void remove(String fqPropertyName) {
		String[] pathParts = flattenPropertyName(fqPropertyName);
		JSONObject parent = identifyParentProperty(fqPropertyName, pathParts);
		if (parent == null) {
			return;
		}
		String childName = pathParts[pathParts.length - 1];
		if (isPropertyNameWithIndex(childName)) {
			JSONArray jsonArr = parent
					.optJSONArray(extractPropertyName(childName));
			if (jsonArr != null) {
				jsonArr.remove(extractIndex(childName));
			}
		} else {
			parent.remove(childName);
		}
	}

	@Override
	public void setContent(InputStream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("Input stream is null");
		}
		String content = null;
		try {
			content = IOUtils.toString(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		RepresentationFactory factory = initRepresentationFactory();
		representation = factory.readRepresentation(
				RepresentationFactory.HAL_JSON, new StringReader(content));
		jsonObject = new JSONObject(content);
	}

	@Override
	public InputStream getContent() {
		return IOUtils.toInputStream(representation.toString(
				RepresentationFactory.HAL_JSON,
				RepresentationFactory.PRETTY_PRINT));
	}

	// identify the existing last parent in the path
	private JSONObject identifyParentProperty(String fqPropertyName,
			String[] pathParts) {
		int pathIndex = 0;
		JSONObject parent = jsonObject;
		while (pathIndex < (pathParts.length - 1)) {
			String pathPart = pathParts[pathIndex];
			ensurePropertyNameIsWithIndex(pathPart, fqPropertyName);
			parent = identifySpecificChild(parent,
					extractPropertyName(pathPart), extractIndex(pathPart));
			pathIndex++;
		}
		return parent;
	}

	// identify the existing specific/indexed child
	private JSONObject identifySpecificChild(Object parent,
			String propertyName, int index) {
		if (parent instanceof JSONArray) {
			return ((JSONArray) parent).optJSONObject(index);
		} else if (parent instanceof JSONObject) {
			JSONObject parentJsonObj = (JSONObject) parent;
			JSONObject jsonObj = parentJsonObj.optJSONObject(propertyName);
			if (jsonObj == null) {
				if (parentJsonObj.optJSONArray(propertyName) != null) {
					return parentJsonObj.optJSONArray(propertyName)
							.optJSONObject(index);
				}
			}
			return jsonObj;
		} else if (parent == null) {
			return null;
		}
		throw new RuntimeException("Unexpected type "
				+ parent.getClass().getName());
	}

	// identify the existing last parent or create new in the path
	private JSONObject checkAndCreateParent(String fqPropertyName,
			String[] pathParts) {
		int pathIndex = 0;
		JSONObject parent = jsonObject;
		while (pathIndex < (pathParts.length - 1)) {
			String pathPart = pathParts[pathIndex];
			ensurePropertyNameIsWithIndex(pathPart, fqPropertyName);
			parent = checkAndCreateChild(parent, extractPropertyName(pathPart),
					extractIndex(pathPart));
			pathIndex++;
		}
		return parent;
	}

	// identify the existing specific/indexed child or create new
	private JSONObject checkAndCreateChild(JSONObject parent,
			String propertyName, int index) {
		JSONArray jsonArr = parent.getJSONArray(propertyName);
		if (index > jsonArr.length()) {
			throw new IllegalArgumentException(
					"Invalid index '"
							+ index
							+ "' to set value to. Only supported indexs are between '0' and '"
							+ jsonArr.length() + "'");
		} else if (index == jsonArr.length()) {
			jsonArr = HalJsonUtil.cloneLastChild(jsonArr);
		}
		return jsonArr.getJSONObject(index);
	}

	private void ensurePropertyNameIsWithIndex(String elementName,
			String fqPropertyName) {
		if (!isPropertyNameWithIndex(elementName)) {
			throw new IllegalArgumentException("Invalid part '" + elementName
					+ "' in fully qualified property name '" + fqPropertyName
					+ "'");
		}
	}
}
