package com.temenos.ebank.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Customization table
 * @author acirlomanu
 */
public class ConfigParam implements Serializable {
	private static final long serialVersionUID = 1L;

	private String codeParam;
	private String valueParam;
	private String typeParam;
	
	/* This field is not mapped. It should contain the parsed value of the valeurParam field, with the type specified by the typeParam field. */
	private Object value;
	/* This field is not mapped. Indicates if the 'value' field above was set, even with null. */
	private boolean parsedValue = false;

	/**
	 * Default constructor (required by Hibernate)
	 */
	public ConfigParam() {
		super();
	}

	/**
	 * Full constructor
	 */
	public ConfigParam(String code, String valeurParam, String typeParam) {
		super();
		this.codeParam = code;
		this.valueParam = valeurParam;
		this.typeParam= typeParam;
	}
	
	public String getCodeParam() {
		return codeParam;
	}
	public void setCodeParam(String code) {
		this.codeParam = code;
	}

	public String getValueParam() {
		return valueParam;
	}

	public void setValueParam(String valueParam) {
		this.valueParam = valueParam;
	}

	public String getTypeParam() {
		return typeParam;
	}

	public void setTypeParam(String typeParam) {
		this.typeParam = typeParam;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		this.parsedValue = true;
	}

	public boolean isParsedValue() {
		return parsedValue;
	}
	
	public Boolean getBooleanValue() {
		return (Boolean) getValue();
	}
	
	public void setBooleanValue(Boolean value) {
		setValue(value);
	}
	
	public Integer getIntegerValue() {
		return (Integer) getValue();
	}
	
	public void setIntegerValue(Integer value) {
		setValue(value);
	}
	
	public BigDecimal getDecimalValue() {
		return (BigDecimal) getValue();
	}
	
	public void setDecimalValue(BigDecimal value) {
		setValue(value);
	}
	
	public String getStringValue() {
		return (String) getValue();
	}
	
	public void setStringValue(String value) {
		setValue(value);
	}
}