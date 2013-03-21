package com.temenos.interaction.core.entity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.vocabulary.Term;
import com.temenos.interaction.core.entity.vocabulary.TermFactory;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

/**
 * Metadata class holding vocabularies used to describe an entity.  
 */
public class EntityMetadata  {
	private final static Logger logger = LoggerFactory.getLogger(EntityMetadata.class);

	private TermFactory termFactory = new TermFactory();
	private String entityName;			//Entity name
	private Vocabulary vocabulary;		//Entity Vocabulary

	//Map of <Entity property, Vocabulary>
	private Map<String, Vocabulary> propertyVocabularies = new HashMap<String, Vocabulary>();

	public EntityMetadata(String entityName) {
		this.entityName = entityName;
	}
	
	/**
	 * Returns the entity name associated to this metadata
	 * @return entity name
	 */
	public String getEntityName() {
		return entityName;
	}
	
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
	 * Gets the top level properties.
	 * I.e. names of properties which do not belong to any complex group.
	 * @return The set of property names
	 */
	public Set<String> getTopLevelProperties() {
		Set<String> props = new HashSet<String>();
		for(String prop : propertyVocabularies.keySet()) {
			Vocabulary voc = propertyVocabularies.get(prop);
			if(voc == null || voc.getTerm(TermComplexGroup.TERM_NAME) == null) {
				props.add(prop);
			}
		}
		return props;
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
		Vocabulary voc = propertyVocabularies.get(propertyName);
		if(voc != null) {
			TermComplexType term = (TermComplexType) voc.getTerm(TermComplexType.TERM_NAME);
			complexType = term != null && term.isComplexType();
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
		Vocabulary voc = propertyVocabularies.get(propertyName);
		if(voc != null) {
			TermComplexGroup term = (TermComplexGroup) voc.getTerm(TermComplexGroup.TERM_NAME);
			complexGroup = (term != null ? term.getComplexGroup() : "");
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
		Vocabulary voc = propertyVocabularies.get(propertyName);
		if(voc != null) {
			TermValueType term = (TermValueType) voc.getTerm(TermValueType.TERM_NAME);
			textValue = term == null || term.isText();
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
		Vocabulary voc = propertyVocabularies.get(propertyName);
		if(voc != null) {
			TermValueType term = (TermValueType) voc.getTerm(TermValueType.TERM_NAME);
			numberValue = term != null && term.isNumber();
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
		String propertyName = property.getName();
		Object propertyValue = property.getValue();
		return getPropertyValueAsString(propertyName, propertyValue);
	}

	/**
	 * Converts the field value in to a string
	 * @param propertyName Property name
	 * @param propertyValue Property value
	 * @return The property value as a string
	 */
	public String getPropertyValueAsString(String propertyName, Object propertyValue)
	{
		String value = "";
		String termValueType = getTermValue(propertyName, TermValueType.TERM_NAME);
		if(propertyValue == null) {
			value = "";
		}
		else if(termValueType.equals(TermValueType.TEXT) ||
				termValueType.equals(TermValueType.RECURRENCE) ||
				termValueType.equals(TermValueType.ENCRYPTED_TEXT) ||				
				termValueType.equals(TermValueType.INTEGER_NUMBER) ||
				termValueType.equals(TermValueType.NUMBER) ||
				termValueType.equals(TermValueType.BOOLEAN)) {
			value = String.valueOf(propertyValue);
		}
		else if(termValueType.equals(TermValueType.TIMESTAMP) ||
				termValueType.equals(TermValueType.DATE) ||
				termValueType.equals(TermValueType.TIME)) {
			value = DateFormat.getDateTimeInstance().format((Date) propertyValue);
		}
		else if(termValueType.equals(TermValueType.ENUMERATION)) {
			if(propertyValue instanceof String[]) {
				String[] enumValues = (String[]) propertyValue;
				for(String item : enumValues) {
					value += value.isEmpty() ? item : "," + item;
				}
			}
			else {
				value = String.valueOf(propertyValue);
			}
		}
		else {
			logger.warn("Unable to return a text representation for field " + propertyName + " of type " + termValueType);
		}		
		return value;
	}
	
	/**
	 * Returns the value of a vocabulary term. 
	 * If the term does not exist it returns it default value or null if it 
	 * does not have a default value.
	 * @param propertyName Property name
	 * @param termName Vocabulary term name
	 * @return The term value as a string or null if not available
	 */
	public String getTermValue(String propertyName, String termName) {
		Vocabulary voc = getPropertyVocabulary(propertyName);
		if(voc != null) {
			Term term = voc.getTerm(termName);
			if(term != null) {
				return term.getValue();
			}
		}
		return termFactory.getTermDefaultValue(termName);
	}
	
	/**
	 * Returns a list of fields which have the TermIdField vocabulary term
	 * @return list of id fields
	 */
	public List<String> getIdFields() {
		List<String> idFields = new ArrayList<String>();
		for(String propertyName : getPropertyVocabularyKeySet()) {
			if(getTermValue(propertyName, TermIdField.TERM_NAME).equals("true")) {
				idFields.add(propertyName);
			}
		}
		return idFields;
	}
	
	/**
	 * Returns a new empty EntityProperty.
	 * The value will be defaulted to according to the properties value type.
	 * @param propertyName Entity property name
	 * @return Entity property
	 */
	public EntityProperty createEmptyEntityProperty(String propertyName) {
		String termValue = getTermValue(propertyName, TermValueType.TERM_NAME);
		if(termValue.equals(TermValueType.INTEGER_NUMBER)) {
			return new EntityProperty(propertyName, new Long(0));
		}
		else if(termValue.equals(TermValueType.NUMBER)) {
			return new EntityProperty(propertyName, new Double(0.0));
		}
		else if(termValue.equals(TermValueType.BOOLEAN)) {
			return new EntityProperty(propertyName, new Boolean(false));
		}
		else if(termValue.equals(TermValueType.TIMESTAMP) ||
				termValue.equals(TermValueType.DATE) ||
				termValue.equals(TermValueType.TIME)) {
			return new EntityProperty(propertyName, new Date());
		}
		else if(termValue.equals(TermValueType.ENUMERATION)) {
			String[] enumValues = {};
			return new EntityProperty(propertyName, enumValues);
		}		
		return new EntityProperty(propertyName, "");
	}
}
