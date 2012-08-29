package com.temenos.interaction.core.entity.vocabulary.terms;

import com.temenos.interaction.core.entity.vocabulary.Term;

/**
 * This term describes whether an entity property is mandatory
 */
public class TermMandatory implements Term {
	public final static String TERM_NAME = "TERM_MANDATORY";

	private boolean mandatory;
	
	public TermMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	/**
	 * Returns true if the property is mandatory
	 * @return true if mandatory, false otherwise
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	
	@Override
	public String getValue() {
		return mandatory ? "true" : "false";
	}

	@Override
	public String getName() {
		return TERM_NAME;
	}	
}
