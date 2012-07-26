package com.temenos.interaction.core.entity.vocabulary.terms;

import com.temenos.interaction.core.entity.vocabulary.Term;

/**
 *  * This term gives the name of the complex group (if any) that an attribute belongs to
 */
public class TermComplexGroup implements Term {
	public final static String TERM_NAME = "TERM_COMPLEX_GROUP";

	private String complexGroup;
	
	public TermComplexGroup(String complexGroup) {
		this.complexGroup = complexGroup;
	}
	
	/**
	 * Returns the name of the complex group it belongs to
	 * @return complex group name
	 */
	public String getComplexGroup() {
		return complexGroup;
	}
	
	@Override
	public String getValue() {
		return complexGroup;
	}

	@Override
	public String getName() {
		return TERM_NAME;
	}	
}
