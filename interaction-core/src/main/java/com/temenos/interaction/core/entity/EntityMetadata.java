package com.temenos.interaction.core.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

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
	 * Gets the list of vocalularyProperty names in this Vocabulary
	 * @return The set of vocabulary names
	 */
	public Set<String> getPropertyVocabularyKeySet() {
		return propertyVocabularies.keySet();
	}
	
	/**
	 * Sets the vocabulary for the specified entity property.
	 * @param propertyName Property name
	 * @param vocabulary Vocabulary
	 */
	public void setPropertyVocabulary(String propertyName, Vocabulary vocabulary) {
		propertyVocabularies.put(propertyName, vocabulary);
	}
	
	/**
	 * Checks whether a property is a complex type or not
	 * @param propertyName The name of the property to check
	 * @return Whether the property is a complex type or not
	 */
	public boolean isPropertyComplex( String propertyName )
	{
		boolean complexType = false;
		
		try
		{
			complexType = ((TermComplexType)getPropertyVocabulary( propertyName ).getTerm(TermComplexType.TERM_NAME)).isComplexType(); 
		}
		catch( Exception e )
		{
		}
		
		return complexType;
	}
	
	/**
	 * Returns the complex type group name of a property
	 * @param propertyName The name of the property to process
	 * @return The name of the complex type group - if any
	 */
	public String getPropertyComplexGroup( String propertyName )
	{
		String complexGroup = "";
		
		try
		{
			complexGroup = ((TermComplexGroup)getPropertyVocabulary( propertyName ).getTerm(TermComplexGroup.TERM_NAME)).getComplexGroup(); 
		}
		catch( Exception e )
		{
		}
		
		return complexGroup;
	}
	
	/**
	 * Checks whether a property is a text type or not
	 * @param propertyName The name of the property to check
	 * @return Whether the property is a text type or not
	 */
	public boolean isPropertyText( String propertyName )
	{
		boolean textValue = true;
		
		try
		{
			textValue = ((TermValueType)getPropertyVocabulary( propertyName ).getTerm(TermValueType.TERM_NAME)).isText(); 
		}
		catch( Exception e )
		{
		}
		
		return textValue;
	}
	
	/**
	 * Checks whether a property is a number type or not
	 * @param propertyName The name of the property to check
	 * @return Whether the property is a number type or not
	 */
	public boolean isPropertyNumber( String propertyName )
	{
		boolean numberValue = false;
		
		try
		{
			numberValue = ((TermValueType)getPropertyVocabulary( propertyName ).getTerm(TermValueType.TERM_NAME)).isNumber(); 
		}
		catch( Exception e )
		{
		}
		
		return numberValue;
	}
	
	/**
	 * Converts the field value in to a string (for TEXT and NUMBER types)
	 * @param propertyName The name of the property to convert
	 * @return The property value as a string
	 */
	public String getPropertyValueAsString( EntityProperty property )
	{
		String value = "";
		String propertyName = property.getName();
		
		if ( isPropertyText( propertyName ) )
		{
			value = (String) property.getValue();
		}
		else if ( isPropertyNumber( propertyName ) )
		{
			value = Long.toString( (Long) property.getValue() );
		}
		
		return value;
	}
}
