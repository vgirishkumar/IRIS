package com.temenos.interaction.core.entity.vocabulary.terms;

import com.temenos.interaction.core.entity.vocabulary.Term;

/**
 * This term describes whether an entity property is an id field
 */
public class TermIdField implements Term {
	public final static String TERM_NAME = "TERM_ID_FIELD";

	private boolean idField;
	
	public TermIdField(boolean idField) {
		this.idField = idField;
	}
	
	/**
	 * Returns true if the property is an id field
	 * @return true if id field, false otherwise
	 */
	public boolean isIdField() {
		return idField;
	}
	
	@Override
	public String getValue() {
		return idField ? "true" : "false";
	}

	@Override
	public String getName() {
		return TERM_NAME;
	}	
}
