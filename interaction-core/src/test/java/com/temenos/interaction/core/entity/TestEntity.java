package com.temenos.interaction.core.entity;

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


import java.util.Set;

import org.junit.Assert;
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
		Metadata metadata = new Metadata("CustomerServiceTest");
		EntityMetadata vocs = new EntityMetadata("Customer");
		Vocabulary voc = new Vocabulary();
		voc.setTerm(new TermResourceManager("T24"));
		vocs.setVocabulary(voc);
		Vocabulary vocName = new Vocabulary();
		vocName.setTerm(new TermValueType("String"));
		vocs.setPropertyVocabulary("name", vocName);
		metadata.setEntityMetadata(vocs);
		
		//Create entity
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("id", "123"));
		customerFields.setProperty(new EntityProperty("name", "Fred"));
		Entity customer = new Entity("Customer", customerFields);
		
		Assert.assertEquals("Customer", customer.getName());
		Assert.assertEquals("T24", vocs.getVocabulary().getTerm(TermResourceManager.TERM_NAME).getValue()); 
		Assert.assertEquals("Fred", customer.getProperties().getProperty("name").getValue());
		Assert.assertEquals("String", vocs.getPropertyVocabulary(customer.getProperties().getProperty("name").getFullyQualifiedName()).getTerm(TermValueType.TERM_NAME).getValue()); 
	}
	 
	@Test
	public void testComplexEntity() {
		//Create entity
		EntityProperties addressFields = new EntityProperties();
		EntityProperty postCodeProperty = new EntityProperty("postcode", "WD8 1LK");
		addressFields.setProperty(postCodeProperty);
		addressFields.setProperty(new EntityProperty("houseNumber", "45"));
		
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("id", "123"));
		customerFields.setProperty(new EntityProperty("name", "Fred"));
		EntityProperty addressProperty = new EntityProperty("address", addressFields);
		postCodeProperty.setParent(addressProperty);
		
		customerFields.setProperty(addressProperty);
		Entity customer = new Entity("Customer", customerFields);
		
		Assert.assertEquals("Customer", customer.getName());
		Assert.assertEquals("Fred", customer.getProperties().getProperty("name").getValue().toString());
		addressFields = (EntityProperties) customer.getProperties().getProperty("address").getValue();
		Assert.assertEquals("45", addressFields.getProperty("houseNumber").getValue().toString());
	}
	
	@Test
	public void testComplexEntityVocabulary() {
		//Define vocabulary for this entity
		Metadata metadata = new Metadata("CustomerServiceTest");
		EntityMetadata vocs = new EntityMetadata("Customer");
		Vocabulary vocAddress = new Vocabulary();
		vocAddress.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("address", vocAddress);
		metadata.setEntityMetadata(vocs);
		
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
	
	@Test
	public void testPropertyVocabularyKeySet() {
		//Define vocabulary for this entity
		Metadata metadata = new Metadata("CustomerServiceTest");
		EntityMetadata vocs = new EntityMetadata("Customer");
		
		Vocabulary vocName = new Vocabulary();
		vocName.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("name", vocName);
		
		Vocabulary vocAddress = new Vocabulary();
		vocAddress.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("address", vocAddress);
		
		Vocabulary vocDob = new Vocabulary();
		vocDob.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("dateOfBirth", vocDob);
		
		Vocabulary vocSector = new Vocabulary();
		vocSector.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("sector", vocSector);
		
		Vocabulary vocIndustry = new Vocabulary();
		vocIndustry.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("industry", vocIndustry);
		
		metadata.setEntityMetadata(vocs);		
		
		Set<String> propertyKeys = vocs.getPropertyVocabularyKeySet();
		Assert.assertEquals(5, propertyKeys.size());
		Assert.assertTrue(propertyKeys.contains("name"));
		Assert.assertTrue(propertyKeys.contains("address"));
		Assert.assertTrue(propertyKeys.contains("dateOfBirth"));
		Assert.assertTrue(propertyKeys.contains("sector"));
		Assert.assertTrue(propertyKeys.contains("industry"));
	}	
}
