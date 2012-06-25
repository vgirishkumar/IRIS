package com.temenos.interaction.core.entity.vocabulary.terms;

import com.temenos.interaction.core.entity.vocabulary.Term;

/**
 * This Term describes the type of a property, e.g. NUMBER, TEXT, etc. 
 */
public class TermValueType implements Term {
	public final static String TERM_NAME = "TERM_VALUE_TYPE";
	
	public final static String TEXT = "TEXT";
	public final static String NUMBER = "NUMBER";
	
	private String valueType;
	
	public TermValueType(String valueType) {
		this.valueType = valueType;
	}
	
	@Override
	public String getString() {
		return valueType;
	}
	
	@Override
	public String getTermName() {
		return TERM_NAME;
	}	
	
}
