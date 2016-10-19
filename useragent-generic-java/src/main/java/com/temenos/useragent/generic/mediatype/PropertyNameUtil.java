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


/**
 * Utility class for supporting property name based operations within all media
 * type handlers.
 * 
 * @author ssethupathi
 *
 */
public class PropertyNameUtil {

	public final static String PROPERTY_NAME_WITH_INDEX = ".+(\\(\\d+\\))+";

	/**
	 * Extracts index from the property name part with index.
	 * <p>
	 * For example, property name part <i>foo(2)</i> where the name of the part
	 * is <i>foo</i> with index <i>2</i> would return <i>2</i>.
	 * </p>
	 * <p>
	 * If the property name part is not with index then <i>0</i> is returned.
	 * </p>
	 * 
	 * @param propertyName
	 * @return index
	 */
	public static int extractIndex(String propertyName) {
		if (propertyName == null || propertyName.isEmpty()) {
			throw new IllegalArgumentException("Invalid property name part '"
					+ propertyName + "'");
		}
		if (propertyName.matches(PROPERTY_NAME_WITH_INDEX)) {
			String indexStr = propertyName.substring(
					propertyName.indexOf("(") + 1, propertyName.indexOf(")"));
			return Integer.parseInt(indexStr);
		}
		return 0;
	}

	/**
	 * Extracts name from the property name part with index.
	 * <p>
	 * For example, property name part <i>foo(2)</i> where the name of the part
	 * is <i>foo</i> with index <i>2</i> would return <i>foo</i>.
	 * </p>
	 * 
	 * @param propertyName
	 * @return index
	 */
	public static String extractPropertyName(String propertyName) {
		if (propertyName == null) {
			throw new IllegalArgumentException("Invalid property name part '"
					+ propertyName + "'");
		}

		if (propertyName.matches(PROPERTY_NAME_WITH_INDEX)) {
			return propertyName.substring(0, propertyName.indexOf("("));
		}
		return propertyName;
	}

	/**
	 * Flattens the fully qualified property name parts into an array.
	 * 
	 * <p>
	 * For example, fully qualified property name <i>foo(0)/bar(1)/blah</i>
	 * would be flattened into an array with <i>3</i> elements as <i>foo(0),
	 * bar(1)</i> and <i>blah</i>.
	 * </p>
	 * 
	 * @param fqPropertyName
	 * @return array of property name parts
	 */
	public static String[] flattenPropertyName(String fqPropertyName) {
		if (fqPropertyName == null || fqPropertyName.isEmpty()) {
			throw new IllegalArgumentException("Invalid property name '"
					+ fqPropertyName + "'");
		}
		return fqPropertyName.split("/");
	}

	/**
	 * Identifies whether or not the given property name is with index.
	 * 
	 * @param propertyName
	 * @return trur if with index, false otherwise.
	 */
	public static boolean isPropertyNameWithIndex(String propertyName) {
		if (propertyName != null) {
			return propertyName.matches(PROPERTY_NAME_WITH_INDEX);
		}
		return false;
	}
}
