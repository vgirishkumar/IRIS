package com.temenos.interaction.core.entity.vocabulary.terms;

import com.temenos.interaction.core.entity.vocabulary.Term;

/**
 * This Term describes the position/ordinal value of a property
 * inside an entity. 
 */
public class TermOrdinal implements Term {
	public final static String TERM_NAME = "TERM_ORDINAL";
	
	private int ordinal;
	
	public TermOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
	
	/**
	 * Returns the ordinal value.
	 * @return Ordinal value.
	 */
	public int getOrginal() {
		return ordinal;
	}
	
	@Override
	public String getString() {
		return Integer.toString(ordinal);
	}
	
	@Override
	public String getTermName() {
		return TERM_NAME;
	}	
	
}
