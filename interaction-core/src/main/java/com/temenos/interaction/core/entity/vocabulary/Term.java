package com.temenos.interaction.core.entity.vocabulary;

/**
 * A Term is an element of a vocabulary and is used to
 * describe entities. 
 */
public interface Term {

	/**
	 * Return a text value of this Term
	 * @return Term value
	 */
	public String getValue();

	/**
	 * Return the name of this Term
	 * @return term name
	 */
	public String getName();
}
