package com.temenos.interaction.core.entity.vocabulary.terms;

import com.temenos.interaction.core.entity.vocabulary.Term;

/**
 * This term describes whether an entity property is a complex type
 */
public class TermComplexType implements Term {
	public final static String TERM_NAME = "TERM_COMPLEX_TYPE";

	private boolean complexType;
	
	public TermComplexType(boolean complexType) {
		this.complexType = complexType;
	}
	
	/**
	 * Returns true if the property is a complex type
	 * @return true if complex type, false otherwise
	 */
	public boolean isComplexType() {
		return complexType;
	}
	
	@Override
	public String getString() {
		return complexType ? "true" : "false";
	}

	@Override
	public String getTermName() {
		return TERM_NAME;
	}	
}
