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
 * This Term describes the type of a property, e.g. NUMBER, TEXT, etc. 
 */
public class TermValueType implements Term {
	public final static String TERM_NAME = "TERM_VALUE_TYPE";
	
	public final static String TEXT = "TEXT";
	public final static String NUMBER = "NUMBER";
	public final static String INTEGER_NUMBER = "INTEGER_NUMBER";
	public final static String TIMESTAMP = "TIMESTAMP";
	public final static String DATE = "DATE";
	public final static String TIME = "TIME";
	public final static String BOOLEAN = "BOOLEAN";
	public final static String RECURRENCE = "RECURRENCE";
	public final static String ENCRYPTED_TEXT = "ENCRYPTED_TEXT";
	public final static String IMAGE = "IMAGE";
	public final static String ENUMERATION = "ENUMERATION";
	
	private String valueType;
	
	public TermValueType(String valueType) {
		this.valueType = valueType;
	}
	
	@Override
	public String getValue() {
		return valueType;
	}
	
	/**
	 * @return Whether the value is of type TEXT or not
	 */
	public boolean isText() {
		return valueType.equals(TEXT);
	}
	
	/**
	 * @return Whether the value is of type NUMBER or not
	 */
	public boolean isNumber() {
		return valueType.equals(NUMBER) || valueType.equals(INTEGER_NUMBER);
	}
	
	/**
     * @return Whether the value is of type DATE or not
     */
    public boolean isDate() {
        return valueType.equals(DATE);
    }
    
    /**
     * @return Whether the value is of type DATE or not
     */
    public boolean isTimestamp() {
        return valueType.equals(TIMESTAMP);
    }
    
    /**
     * @return Whether the value is of type TIMESTAMP or not
     */
    public boolean isTime() {
        return valueType.equals(TIME);
    }
    
    /**
     * @return Whether the value is of type TIMESTAMP or not
     */
    public boolean isBoolean() {
        return valueType.equals(BOOLEAN);
    }
	
	@Override
	public String getName() {
		return TERM_NAME;
	}	
	
}
