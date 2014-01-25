package com.temenos.ebank.domain;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class for converting site parameters stored as String to their Object values (and the other way around).
 * <p>
 * Supported types are {@link Boolean}, {@link Integer}, {@link BigDecimal} and {@link String}. The latter is the
 * default when none of the previous types is specified.
 * 
 * @author acirlomanu
 * 
 */
public class ConfigParamHelper {

	static final String TYPE_PARAM_INTEGER = "integer";

	static final String TYPE_PARAM_DECIMAL = "decimal";

	static final String TYPE_PARAM_BOOLEAN = "boolean";

	static final String TYPE_PARAM_STRING = "string";

	private static final Log logger = LogFactory.getLog(ConfigParamTable.class);

	/**
	 * Reads the String value of a {@link ConfigParam} and converts it to to the specified type.
	 * 
	 * @return the converted {@link Object} value or null
	 * @throws NullPointerException
	 */
	public static Object parse(ConfigParam param, String type) {
		if (!param.isParsedValue()) {
			// parse the value
			if (TYPE_PARAM_BOOLEAN.equals(type)) {
				updateValueBoolean(param);
			} else if (TYPE_PARAM_INTEGER.equals(type)) {
				updateValueInteger(param);
			} else if (TYPE_PARAM_DECIMAL.equals(type)) {
				updateValueDecimal(param);
			} else {
				// assume String
				updateValueString(param);
			}
		}

		return param.getValue();
	}

	/**
	 * Converts the String value of the {@link ConfigParam} to {@link Boolean}. The rule is Boolean.TRUE for "1",
	 * Boolean.FALSE for any other value, <code>null</code> if the {@link String} value is <code>null</code>.
	 * 
	 * The converted value is saved as {@link ConfigParam#setValue(Object)}.
	 */
	static void updateValueBoolean(ConfigParam param) {
		if (param.getValueParam() != null) {
			param.setValue(Boolean.valueOf("1".equals(param.getValueParam()) || "true".equals(param.getValueParam())));
		} else {
			param.setValue(null);
		}
	}

	/**
	 * Converts the String value of the {@link ConfigParam} to {@link BigDecimal}. If there are conversion errors, it
	 * stores <code>null</code>. Stores <code>null</code> if the {@link String} value is <code>null</code>.
	 * 
	 * The converted value is saved as {@link ConfigParam#setValue(Object)}.
	 */
	static void updateValueDecimal(ConfigParam param) {
		if (param.getValueParam() != null) {
			try {
				param.setValue(new BigDecimal(param.getValueParam()));
			} catch (Exception e) {
				logger.warn("Error converting to BigDecimal the String : " + param.getValueParam());
				param.setValue(null);
			}
		} else {
			param.setValue(null);
		}
	}

	/**
	 * Converts the String value of the {@link ConfigParam} to {@link Integer}. If there are conversion errors, it
	 * stores <code>null</code>. Stores <code>null</code> if the {@link String} value is <code>null</code>.
	 * 
	 * The converted value is saved as {@link ConfigParam#setValue(Object)}.
	 */
	static void updateValueInteger(ConfigParam param) {
		if (param.getValueParam() != null) {
			try {
				param.setValue(Integer.valueOf(param.getValueParam()));
			} catch (NumberFormatException e) {
				logger.warn("Error converting to Integer the String : " + param.getValueParam());
				param.setValue(null);
			}
		} else {
			param.setValue(null);
		}
	}

	/**
	 * Sets the {@link Object} value of the {@link ConfigParam} exactly as its {@link String} value, without any
	 * conversion.
	 */
	static void updateValueString(ConfigParam param) {
		param.setValue(param.getValueParam());
	}

	/**
	 * Converts the given {@link Object} value to {@link String}. {@link Boolean}s are converted to either "1" or "0",
	 * anything else is stored by applying its <code>toString</code> method. Stores <code>null</code> if the
	 * {@link Object} value is <code>null</code>.<br/>
	 * 
	 * The converted {@link String} value is saved as {@link ConfigParam#setValueParam(String)}.<br/>
	 * The {@link Object} value is saved as {@link ConfigParam#setValue(Object)}.
	 * 
	 * @return the converted String value
	 */
	public static String format(ConfigParam param, Object value) {
		// update the parsed value
		param.setValue(value);

		// update the String value
		String valeurParam = null;
		if (value != null) {
			if (value instanceof Boolean) {
				valeurParam = ((Boolean) value).booleanValue() ? "1" : "0";
			} else {
				// for Integer, BigDecimal and String I just use their toString representations.
				valeurParam = value.toString();
			}
		}
		param.setValueParam(valeurParam);

		return valeurParam;
	}

}
