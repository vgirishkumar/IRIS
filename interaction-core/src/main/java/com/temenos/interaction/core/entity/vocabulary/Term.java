package com.temenos.interaction.core.entity.vocabulary;

/**
 * A Term is an element of a vocabulary and is used to
 * describe entities. 
 */
public interface Term {

	/**
	 * Return a text description of this Term
	 * @return Term description
	 */
	public String getString();

	/**
	 * Return the name of this Term
	 * @return term name
	 */
	public String getTermName();
}
