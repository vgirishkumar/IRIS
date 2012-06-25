package com.temenos.interaction.core.entity.vocabulary.terms;

import com.temenos.interaction.core.entity.vocabulary.Term;

/**
 * This term annotates an entity property as a range of values.
 */
public class TermRange implements Term {
	public final static String TERM_NAME = "TERM_RANGE";

	private int min;
	private int max;
	
	public TermRange(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Returns the minimum value of this range
	 * @return min value
	 */
	public int getMin() {
		return min;
	}
	
	/**
	 * Returns the maximum value of this range
	 * @return max value
	 */
	public int getMax() {
		return max;
	}
	
	@Override
	public String getString() {
		return "[" + min + "," + max + "]";
	}

	@Override
	public String getTermName() {
		return TERM_NAME;
	}	
}
