package com.temenos.interaction.core.entity.vocabulary;

import com.temenos.interaction.core.entity.vocabulary.Term;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermMandatory;
import com.temenos.interaction.core.entity.vocabulary.terms.TermRange;
import com.temenos.interaction.core.entity.vocabulary.terms.TermResourceManager;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

/**
 * Factory class responsible for creating vocabulary terms
 */
public class TermFactory {
	
	/**
	 * Create a vocabulary Term
	 * @param name Term name
	 * @param value Term value
	 * @return The create term
	 * @throws Exception
	 */
	public Term createTerm(String name, String value) throws Exception {
		Term term;
		if(name == null || value == null) {
			throw new Exception("Unable to create vocabulary Term with null values.");
		}
		else if(name.equalsIgnoreCase(TermValueType.TERM_NAME)) {
			term = new TermValueType(value);
		}
		else if(name.equalsIgnoreCase(TermMandatory.TERM_NAME)) {
			term = new TermMandatory(value.equalsIgnoreCase("true"));
		}
		else if(name.equalsIgnoreCase(TermComplexType.TERM_NAME)) {
			term = new TermComplexType(value.equalsIgnoreCase("true"));
		}
		else if(name.equalsIgnoreCase(TermComplexGroup.TERM_NAME)) {
			term = new TermComplexGroup(value);
		}
		else if(name.equalsIgnoreCase(TermResourceManager.TERM_NAME)) {
			term = new TermResourceManager(value);
		}
		else if(name.equalsIgnoreCase(TermRange.TERM_NAME)) {
			String[] minmax = value.split(",");
			if(minmax.length == 2) {
				int min = Integer.parseInt(minmax[0].trim());
				int max = Integer.parseInt(minmax[1].trim());
				term = new TermRange(min, max);
			}
			else {
				throw new Exception("Failed to parse range value: " + value);
			}
		}
		else {
			throw new Exception("Term " + name + " does not exist.");
		}
		return term;
	}

	/**
	 * Return the default string value of a vocabulary term or null for those that do not have a default value.
	 * @param name Term name
	 * @return the default value
	 */
	public String getTermDefaultValue(String name) {
		if(name != null) {
			if(name.equalsIgnoreCase(TermValueType.TERM_NAME)) {
				return TermValueType.TEXT;
			}
			else if(name.equalsIgnoreCase(TermMandatory.TERM_NAME) ||
					name.equalsIgnoreCase(TermComplexType.TERM_NAME)) {
				return "false";
			}
		}
		return null;
	}
}
