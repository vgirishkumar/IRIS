package com.temenos.interaction.core.entity;

import junit.framework.Assert;

import org.junit.Test;

import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermResourceManager;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

public class TestEntity {

	@Test
	public void testEntity() {
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("id", "123"));
		customerFields.setProperty(new EntityProperty("name", "Fred"));
		Entity customer = new Entity("Customer", customerFields);
		Assert.assertEquals("Fred", customer.getProperties().getProperty("name").getValue());
	}

	@Test
	public void testEntityVocabulary() {
		//Define vocabulary for this entity
		Metadata metadata = new Metadata();
		EntityMetadata vocs = new EntityMetadata();
		Vocabulary voc = new Vocabulary();
		voc.setTerm(new TermResourceManager("T24"));
		vocs.setVocabulary(voc);
		Vocabulary vocName = new Vocabulary();
		vocName.setTerm(new TermValueType("String"));
		vocs.setPropertyVocabulary("name", vocName);
		metadata.setEntityMetadata("Customer", vocs);
		
		//Create entity
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("id", "123"));
		customerFields.setProperty(new EntityProperty("name", "Fred"));
		Entity customer = new Entity("Customer", customerFields);
		
		Assert.assertEquals("Customer", customer.getName());
		Assert.assertEquals("T24", vocs.getVocabulary().getTerm(TermResourceManager.TERM_NAME).getString()); 
		Assert.assertEquals("Fred", customer.getProperties().getProperty("name").getValue());
		Assert.assertEquals("String", vocs.getPropertyVocabulary("name").getTerm(TermValueType.TERM_NAME).getString()); 
	}
	 
	@Test
	public void testComplexEntity() {
		//Create entity
		EntityProperties addressFields = new EntityProperties();
		addressFields.setProperty(new EntityProperty("postcode", "WD8 1LK"));
		addressFields.setProperty(new EntityProperty("houseNumber", "45"));
		
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("id", "123"));
		customerFields.setProperty(new EntityProperty("name", "Fred"));
		customerFields.setProperty(new EntityProperty("address", addressFields));
		Entity customer = new Entity("Customer", customerFields);
		
		Assert.assertEquals("Customer", customer.getName());
		Assert.assertEquals("Fred", customer.getProperties().getProperty("name").getValue().toString());
		addressFields = (EntityProperties) customer.getProperties().getProperty("address").getValue();
		Assert.assertEquals("45", addressFields.getProperty("houseNumber").getValue().toString());
	}
	
	@Test
	public void testComplexEntityVocabulary() {
		//Define vocabulary for this entity
		Metadata metadata = new Metadata();
		EntityMetadata vocs = new EntityMetadata();
		Vocabulary vocAddress = new Vocabulary();
		vocAddress.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("address", vocAddress);
		metadata.setEntityMetadata("Customer", vocs);
		
		//Create entity
		EntityProperties addressFields = new EntityProperties();
		addressFields.setProperty(new EntityProperty("postcode", "WD8 1LK"));
		addressFields.setProperty(new EntityProperty("houseNumber", "45"));
		
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("id", "123"));
		customerFields.setProperty(new EntityProperty("name", "Fred"));
		customerFields.setProperty(new EntityProperty("address", addressFields));
		Entity customer = new Entity("Customer", customerFields);
		
		Assert.assertEquals("Customer", customer.getName());
		Assert.assertEquals("Fred", customer.getProperties().getProperty("name").getValue().toString());
		addressFields = (EntityProperties) customer.getProperties().getProperty("address").getValue();
		Assert.assertEquals(true, ((TermComplexType)vocs.getPropertyVocabulary("address").getTerm(TermComplexType.TERM_NAME)).isComplexType()); 
		Assert.assertEquals("45", addressFields.getProperty("houseNumber").getValue().toString());
	}
}
