package com.temenos.interaction.core.entity.vocabulary.terms;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
	public String getValue() {
		return "[" + min + "," + max + "]";
	}

	@Override
	public String getName() {
		return TERM_NAME;
	}	
}
