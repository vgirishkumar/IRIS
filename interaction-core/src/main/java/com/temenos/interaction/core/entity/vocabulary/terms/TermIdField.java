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
