package com.temenos.interaction.core.entity;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.entity.vocabulary.Vocabulary;

/**
 * Metadata class holding vocabularies used to describe an entity.  
 */
public class EntityMetadata  {
	
	private Vocabulary vocabulary;		//Entity Vocabulary

	//Map of <Entity property, Vocabulary>
	private Map<String, Vocabulary> propertyVocabularies = new HashMap<String, Vocabulary>();

	/**
	 * Gets the vocabulary associated to this entity.
	 * @return Vocabulary
	 */
	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	/**
	 * Sets the vocabulary associated to this entity.
	 * @param vocabulary Entity vocabulary
	 */
	public void setVocabulary(Vocabulary vocabulary) {
		this.vocabulary = vocabulary;
	}
	
	/**
	 * Gets the vocabulary associated to the specified entity property
	 * @param propertyName Property name
	 * @return Vocabulary
	 */
	public Vocabulary getPropertyVocabulary(String propertyName) {
		return propertyVocabularies.get(propertyName);
	}
	
	/**
	 * Sets the vocabulary for the specified entity property.
	 * @param propertyName Property name
	 * @param vocabulary Vocabulary
	 */
	public void setPropertyVocabulary(String propertyName, Vocabulary vocabulary) {
		propertyVocabularies.put(propertyName, vocabulary);
	}
}
