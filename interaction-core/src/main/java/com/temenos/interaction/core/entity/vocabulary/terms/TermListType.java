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
 * This term describes whether an entity property is a list of complex type
 */
public class TermListType implements Term {
	
		public final static String TERM_NAME = "TERM_LIST_TYPE";

		private boolean listType;
		
		public TermListType(boolean listType) {
			this.listType = listType;
		}
		
		/**
		 * Returns true if the property is a complex type
		 * @return true if complex type, false otherwise
		 */
		public boolean isListType() {
			return listType;
		}
		
		@Override
		public String getValue() {
			return listType ? "true" : "false";
		}

		@Override
		public String getName() {
			return TERM_NAME;
		}	
}
