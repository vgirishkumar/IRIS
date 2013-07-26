package com.temenos.interaction.core.entity.vocabulary.terms;

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
