package com.temenos.interaction.core.entity.vocabulary.terms;

import com.temenos.interaction.core.entity.vocabulary.Term;

/**
 * This Term describes the type of a property, e.g. NUMBER, TEXT, etc. 
 */
public class TermValueType implements Term {
	public final static String TERM_NAME = "TERM_VALUE_TYPE";
	
	public final static String TEXT = "TEXT";
	public final static String NUMBER = "NUMBER";
	public final static String TIMESTAMP = "TIMESTAMP";
	
	private String valueType;
	
	public TermValueType(String valueType) {
		this.valueType = valueType;
	}
	
	@Override
	public String getValue() {
		return valueType;
	}
	
	/**
	 * @return Whether the value is of type TEXT or not
	 */
	public boolean isText() {
		if ( valueType.equals(TEXT) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * @return Whether the value is of type NUMBER or not
	 */
	public boolean isNumber() {
		if ( valueType.equals(NUMBER) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public String getName() {
		return TERM_NAME;
	}	
	
}
