package com.temenos.interaction.sdk.entity;

/*
 * #%L
 * interaction-sdk
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds information about an entity field
 */
public class EMProperty {
	private String name;
	private List<EMTerm> terms = new ArrayList<EMTerm>();
	private Map<String, EMProperty> childProperties = new HashMap<String, EMProperty>();

	public EMProperty(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this property
	 * 
	 * @return property name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns all the vocabulary terms of this property
	 * 
	 * @return all vocabulary terms
	 */
	public List<EMTerm> getVocabularyTerms() {
		return terms;
	}

	/**
	 * Adds a term to this property vocabulary.
	 * 
	 * @param term
	 */
	public void addVocabularyTerm(EMTerm term) {
		terms.add(term);
	}

	/**
	 * Returns <i>true</i> this property has a child property for the name child
	 * property name.
	 * 
	 * @param childPropertyName
	 * @return true if this property contains a child property for the child
	 *         property name, false otherwise.
	 */
	public boolean hasChildProperty(String childPropertyName) {
		return childProperties.containsKey(childPropertyName);
	}

	/**
	 * Adds a child property this this property.
	 * 
	 * @param childProperty
	 */
	public void addChildProperty(EMProperty childProperty) {
		childProperties.put(childProperty.getName(), childProperty);
	}

	/**
	 * Returns the child property for the child property name.
	 * 
	 * @param childName
	 * @return child property
	 */
	public EMProperty getChildProperty(String childName) {
		return childProperties.get(childName);
	}

	/**
	 * Returns <i>true</i> if this property has any child property.
	 * 
	 * @return true if this property has any child property, false otherwise.
	 */
	public boolean hasChildren() {
		return !childProperties.isEmpty();
	}

	/**
	 * Returns all the child properties of this property.
	 * 
	 * @return child properties
	 */
	public Collection<EMProperty> getChildProperties() {
		return childProperties.values();
	}
}
