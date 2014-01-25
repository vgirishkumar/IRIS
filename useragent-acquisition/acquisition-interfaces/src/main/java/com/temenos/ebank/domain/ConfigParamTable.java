package com.temenos.ebank.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * More than a domain object, it assembles all the {@link ConfigParam} parameters, each one
 * accessible by the getter method that corresponds to its type. Whenever a new parameter is added to the database,
 * it must also be added under the corresponding <code>enum</code>, with exactly the same name.
 * <p>
 * We chose this approach so that the parameter names (inside the code) are gathered in one place (this class).
 * 
 * @author acirlomanu
 * 
 */
@SuppressWarnings( { "unchecked", "rawtypes" })
public class ConfigParamTable implements Serializable {
	/**
	 * Lists all Boolean parameters.
	 * 
	 * @author gcristescu
	 */
	public static enum BOOLEAN {
		ANALYTICS_ENABLED,
		ANALYTICS_MANUAL
	}

	/**
	 * Lists all Integer parameters.
	 * 
	 * @author gcristescu
	 */
	public static enum INTEGER {
		MIN_BIRTH_DATE_YEAR,
		/**
		 * Max number of accounts the user can select for products that allow to subscribe
		 * to multiple accounts (like international) 
		 */
		MAX_NO_ACCOUNTS,
		/**
		 * On how many miliseconds left before session expires do we notify the user that 
		 * he's been inactive for too long
		 */
		SESSION_INACTIVE_WARNING_TIME
	}

	/**
	 * Lists all BigDecimal parameters.
	 * 
	 * @author gcristescu
	 */
	public static enum BIGDECIMAL {
	}

	/**
	 * Lists all String parameters.
	 * 
	 * @author gcristescu
	 */
	public static enum STRING {
		/*
		 * country to duplicate in countries and nationalities dropdowns
		 */
		LOCAL_COUNTRY, PAF_POSTCODE_MANDATORY_COUNTRY
	}

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(ConfigParamTable.class);

	/**
	 * Holds the actual parameters. The code is the codeParam property of {@link ConfigParam}, the value is the
	 * {@link ConfigParam} itself.
	 */
	private Map params;

	/**
	 * Full constructor.
	 * 
	 * @param params
	 *            Collection of config items
	 */
	public ConfigParamTable(Collection params) {
		if (params != null) {
			this.params = new LinkedHashMap(params.size());
			for (Iterator it = params.iterator(); it.hasNext();) {
				// convert the list to a Map for easy access
				this.add((ConfigParam) it.next());
			}
		}
	}

	/* For now, there is no setter for params in order not to allow to directly pass a parameters Map. */

	/**
	 * @return a {@link Collection} of {@link ConfigParam}.
	 */
	public Collection getConfigParamItems() {
		if (params != null) {
			return params.values();
		} else {
			return new ArrayList();
		}
	}

	/**
	 * Adds the given {@link ConfigParam} to this object's internal {@link Map} for easy access later on, using just
	 * the code of the parameter for lookup.
	 */

	private void add(ConfigParam param) {
		if (param == null) {
			logger.debug("Received a null ConfigParam in the list.");
			return;
		}

		params.put(param.getCodeParam(), param);

	}

	/* value parsers : String to Object */
	/**
	 * Reads the String value of a {@link ConfigParam} and converts it to to the specified type. In case the parameter
	 * is not found in the Map, it returns null.
	 * 
	 * @return the converted {@link Object} value or null
	 */
	private Object parse(String code, String type) {
		ConfigParam param = (ConfigParam) params.get(code);
		if (param == null) {
			// requested parameter was not found
			logger.warn("ConfigParam not found for code : " + code
					+ ". Please check the spelling and the presence of the parameter in the database");
			return null;
		}
		return ConfigParamHelper.parse(param, type);
	}

	/**
	 * Reads the String value of a {@link ConfigParam} and returns it.
	 * 
	 * @see #parse(String, String)
	 */
	private String parseString(String code) {
		return (String) parse(code, ConfigParamHelper.TYPE_PARAM_STRING);
	}

	/**
	 * Reads the String value of a {@link ConfigParam} and converts it to {@link Integer}
	 * 
	 * @see #parse(String, String)
	 */
	private Integer parseInteger(String code) {
		return (Integer) parse(code, ConfigParamHelper.TYPE_PARAM_INTEGER);
	}

	/**
	 * Reads the String value of a {@link ConfigParam} and converts it to {@link BigDecimal}
	 * 
	 * @see #parse(String, String)
	 */
	private BigDecimal parseBigDecimal(String code) {
		return (BigDecimal) parse(code, ConfigParamHelper.TYPE_PARAM_DECIMAL);
	}

	/**
	 * Reads the String value of a {@link ConfigParam} and converts it to {@link Boolean}
	 * 
	 * @see #parse(String, String)
	 */
	private Boolean parseBoolean(String code) {
		return (Boolean) parse(code, ConfigParamHelper.TYPE_PARAM_BOOLEAN);
	}

	/* value formatters : Object to String */
	/**
	 * Reads the {@link Object} value of a {@link ConfigParam} and converts it to {@link String}. In case the
	 * parameter is not present in the Map, it creates one.
	 * 
	 * @see ConfigParamHelper#format(ConfigParam, Object)
	 */
	private String format(String code, Object value) {
		ConfigParam param = (ConfigParam) params.get(code);
		if (param == null) {
			param = new ConfigParam(code, null, null);
			params.put(code, param);
		}

		return ConfigParamHelper.format(param, value);
	}

	/**
	 * Getter for Boolean parameters.
	 * 
	 * @param param
	 * @return
	 *         parameter value, converted to Boolean
	 */
	public Boolean get(BOOLEAN param) {
		return parseBoolean(param.toString());
	}

	/**
	 * Getter for Integer parameters.
	 * 
	 * @param param
	 * @return
	 *         parameter value, converted to Integer
	 */
	public Integer get(INTEGER param) {
		return parseInteger(param.toString());
	}

	/**
	 * Getter for BigDecimal parameters.
	 * 
	 * @param param
	 * @return
	 *         parameter value, converted to BigDecimal
	 */
	public BigDecimal get(BIGDECIMAL param) {
		return parseBigDecimal(param.toString());
	}

	/**
	 * Getter for String parameters.
	 * 
	 * @param param
	 * @return
	 *         parameter value, converted to String
	 */
	public String get(STRING param) {
		return parseString(param.toString());
	}

	/**
	 * Generic setter method, for all types of parameters.
	 * 
	 * @param key
	 * @param value
	 */
	public void set(Object key, Object value) {
		format(key.toString(), value);
	}

	/* getters / setters for easy access to known parameters */

	// public int getMaxBirthdateYear() {
	// return parseInteger("MAX_BIRTH_DATE_YEAR");
	// }
	//	
	// public void setMaxBirthdateYear(Integer year) {
	// format("MAX_BIRTH_DATE_YEAR", year);
	// }
}