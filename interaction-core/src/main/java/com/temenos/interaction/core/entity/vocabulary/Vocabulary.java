package com.temenos.interaction.core.entity.vocabulary;

import java.util.HashMap;
import java.util.Map;

/**
 * A Vocabulary contains a set of Terms used to describe resources.
 */
public class Vocabulary {
	//Map of <Term name, Term>
	private Map<String, Term> terms = new HashMap<String, Term>();
	
	/**
	 * Returns the specified Term
	 * @param termName Term name
	 * @return Term
	 */
	public Term getTerm(String termName) {
		return terms.get(termName);
	}
	
	/**
	 * Adds the specified Term to the Vocabulary
	 * @param term Term
	 */
	public void setTerm(Term term) {
		terms.put(term.getTermName(), term);
	}
}
