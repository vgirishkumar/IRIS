package org.odata4j.producer.resources;

/*
 * #%L
 * interaction-odata4j-ext
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.ExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Document me!
 *
 * @author mjangid
 *
 */
public class OptionsQueryParserExt extends OptionsQueryParser {
    public static BoolCommonExpression parseFilter(String filter) {
		if(filter == null || filter.length() == 0) {
			return null;
		}
		CommonExpression ce = ExpressionParser.parse(getFilterString(filter));
		if (!(ce instanceof BoolCommonExpression)) {
		    throw new RuntimeException("Bad filter");
		}
		return (BoolCommonExpression) ce;
	}
    
	private static String getFilterString(String filterString) {
		filterString = getDecodedString(filterString);
		String tokens[] = filterString.split("\\s+");
		String newString = "";
		boolean isCharFound = false;
		for(String token: tokens) {
			int idx = token.indexOf('\'', 1);
			int len = token.length() - 1;
			if( (token.charAt(0) ==  token.charAt(len)) && (idx > 1 && idx < len)) {
				isCharFound = true;
				String subString = token.substring(1, len);
				subString = getEncodedString(subString);
				token = token.charAt(0) + subString + token.charAt(len);
			}
			newString += token + " ";
		}
		
		if(!isCharFound && filterString.contains("%B") ) {
			isCharFound = true;
			newString = filterString.replace("%B", "%25B");
		}
		
		return isCharFound ? newString : filterString;
	}
	
	private static String getDecodedString(String filterString) {
		if(filterString == null || filterString.length() == 0) {
			return filterString;
		}
		try {
			return URLDecoder.decode(filterString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param filterString
	 * @return encode string
	 */
	private static String getEncodedString(String filterString) {
		if(filterString == null || filterString.length() == 0) {
			return filterString;
		}
		try {
			return URLEncoder.encode(filterString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
