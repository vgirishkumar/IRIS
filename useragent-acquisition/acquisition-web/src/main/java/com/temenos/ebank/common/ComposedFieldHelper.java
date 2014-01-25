package com.temenos.ebank.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Helper for managing String fields composed of multiple values separated by a separator
 * 
 * @author ajurubita
 */
public class ComposedFieldHelper {
	/**
	 * Composes multiple value field as a list of fields separated by a separator
	 * 
	 * @param <T>
	 * @param fields
	 * @param separator
	 * @return
	 */
	public static <T extends Object> String getFieldWithSeparators(List<T> fields, String separator) {
		if (fields == null) {
			return null;
		}
		StringBuffer formattedString = new StringBuffer();
		for (T f : fields) {
			formattedString.append(f.toString()).append(separator);
		}
		String toReturn = formattedString.toString();
		return StringUtils.isEmpty(toReturn) ? toReturn : toReturn.substring(0, toReturn.length() - 1);
	}

	/**
	 * Splits a multiple-value composed field into a list of String values
	 * 
	 * @param fieldWithSeparator
	 * @param separator
	 * @return
	 */
	public static List<String> splitField(String fieldWithSeparator, String separator) {
		List<String> fieldsList = new ArrayList<String>();
		if (fieldWithSeparator == null) {
			return fieldsList;
		}

		String[] fields = fieldWithSeparator.split(separator);
		fieldsList = new ArrayList<String>(Arrays.asList(fields));

		return fieldsList;
	}
}
