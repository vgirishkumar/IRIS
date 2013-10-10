package com.temenos.interaction.core.entity.vocabulary;

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


import java.util.Collection;
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
		terms.put(term.getName(), term);
	}

	/**
	 * Returns the terms contained in this vocabulary
	 * @return list of terms
	 */
	public Collection<Term> getTerms() {
		return terms.values();
	}
}
