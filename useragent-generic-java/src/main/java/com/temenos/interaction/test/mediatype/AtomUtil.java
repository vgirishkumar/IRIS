package com.temenos.interaction.test.mediatype;

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


import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.apache.abdera.model.Element;

public class AtomUtil {

	public static final String MEDIA_TYPE = "application/atom+xml";
	public final static String NS_ATOM = "http://www.w3.org/2005/Atom";
	public final static String NS_ODATA = "http://schemas.microsoft.com/ado/2007/08/dataservices";
	public final static String NS_ODATA_METADATA = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";
	public final static String NS_ODATA_RELATED = "http://schemas.microsoft.com/ado/2007/08/dataservices/related";

	public final static String REGX_VALID_ELEMENT = "[a-zA-Z0-9_]+(\\(\\d+\\))*";
	public final static String REGX_ELEMENT_WITH_INDEX = "[a-zA-Z0-9_]+(\\(\\d+\\))+";

	public static String extractRel(String relAttributeValue) {
		int spaceIndex = relAttributeValue.indexOf(" ");
		if (spaceIndex > 0) {
			return relAttributeValue.substring(spaceIndex).trim();
		} else {
			return relAttributeValue;
		}
	}
	
	public static String getBaseUrl(Element element) {
		try {
			return element.getBaseUri().toURL().toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
