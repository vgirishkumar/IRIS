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


import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermMandatory;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
import com.temenos.interaction.core.resource.ResourceMetadataManager;

public class TestMetadata {

	private static Metadata metadata;
	private static EntityMetadata vocs;
	
	
	@BeforeClass
	public static void setup()
	{
		//Define vocabulary for this entity
		metadata = new Metadata("Customers");
		vocs = new EntityMetadata("Customer");
				
		Vocabulary vocName = new Vocabulary();
		vocName.setTerm(new TermComplexType(false));
		vocName.setTerm(new TermIdField(true));
		vocs.setPropertyVocabulary("name", vocName);
		
		Vocabulary vocAddress = new Vocabulary();
		vocAddress.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("address", vocAddress);

		Stack<String> collectionNames = new Stack<String>();
		collectionNames.push("address");
		
		Vocabulary vocNumbert = new Vocabulary();
		vocNumbert.setTerm(new TermComplexGroup("address"));
		vocNumbert.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		vocs.setPropertyVocabulary("number", vocNumbert, collectionNames.elements());
		
		Vocabulary vocStreet = new Vocabulary();
		vocStreet.setTerm(new TermComplexGroup("address"));
		vocStreet.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("street", vocStreet, collectionNames.elements());
		
		Vocabulary vocTown = new Vocabulary();
		vocTown.setTerm(new TermComplexGroup("address"));
		vocTown.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("town", vocStreet, collectionNames.elements());
		
		Vocabulary vocPostCode = new Vocabulary();
		vocPostCode.setTerm(new TermComplexGroup("address"));
		vocPostCode.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("postCode", vocPostCode, collectionNames.elements());
		
		Vocabulary vocDob = new Vocabulary();
		vocDob.setTerm(new TermComplexType(false));
		vocDob.setTerm(new TermValueType(TermValueType.DATE));
		vocDob.setTerm(new TermMandatory(true));
		vocs.setPropertyVocabulary("dateOfBirth", vocDob);

		Vocabulary vocTimestamp = new Vocabulary();
		vocTimestamp.setTerm(new TermComplexType(false));
		vocTimestamp.setTerm(new TermValueType(TermValueType.TIMESTAMP));
        vocs.setPropertyVocabulary("timestamp", vocTimestamp);
		
		Vocabulary vocTime = new Vocabulary();
		vocTime.setTerm(new TermComplexType(false));
		vocTime.setTerm(new TermValueType(TermValueType.TIME));
		vocs.setPropertyVocabulary("time", vocTime);
		
		Vocabulary vocKids = new Vocabulary();
		vocKids.setTerm(new TermComplexType(false));
		vocKids.setTerm(new TermValueType(TermValueType.BOOLEAN));
        vocs.setPropertyVocabulary("kids", vocKids);

		Vocabulary vocSector = new Vocabulary();
		vocSector.setTerm(new TermComplexType(false));
		vocs.setPropertyVocabulary("sector", vocSector);
		
		Vocabulary vocIndustry = new Vocabulary();
		vocIndustry.setTerm(new TermComplexType(false));
		vocs.setPropertyVocabulary("industry", vocIndustry);
		
		metadata.setEntityMetadata(vocs);
	}
	
	@Test
	public void testPropertyVocabularyKeySet()
	{	
		Set<String> propertyKeys = vocs.getPropertyVocabularyKeySet();
		Assert.assertEquals(12, propertyKeys.size());
		Assert.assertTrue(propertyKeys.contains("name"));
		Assert.assertTrue(propertyKeys.contains("address"));
		Assert.assertTrue(propertyKeys.contains("address.number"));
		Assert.assertTrue(propertyKeys.contains("address.street"));
		Assert.assertTrue(propertyKeys.contains("address.town"));
		Assert.assertTrue(propertyKeys.contains("address.postCode"));
		Assert.assertTrue(propertyKeys.contains("dateOfBirth"));
		Assert.assertTrue(propertyKeys.contains("sector"));
		Assert.assertTrue(propertyKeys.contains("industry"));
		Assert.assertTrue(propertyKeys.contains("time"));
		Assert.assertTrue(propertyKeys.contains("timestamp"));
		Assert.assertTrue(propertyKeys.contains("kids"));
	}
	
	@Test
	public void testIsPropertyComplex()
	{		
		Assert.assertFalse(vocs.isPropertyComplex("name"));
		Assert.assertTrue(vocs.isPropertyComplex("address"));
		Assert.assertFalse(vocs.isPropertyComplex("address.number"));
		Assert.assertFalse(vocs.isPropertyComplex("address.street"));
		Assert.assertFalse(vocs.isPropertyComplex("address.town"));
		Assert.assertFalse(vocs.isPropertyComplex("address.postCode"));
		Assert.assertFalse(vocs.isPropertyComplex("dateOfBirth"));
		Assert.assertFalse(vocs.isPropertyComplex("sector"));
		Assert.assertFalse(vocs.isPropertyComplex("industry"));
	}
	
	@Test
	public void testGetComplexGroup()
	{		
		Assert.assertEquals("", vocs.getPropertyComplexGroup("name"));
		Assert.assertEquals("", vocs.getPropertyComplexGroup("address"));
		Assert.assertEquals("address", vocs.getPropertyComplexGroup("address.number"));
		Assert.assertEquals("address", vocs.getPropertyComplexGroup("address.street"));
		Assert.assertEquals("address", vocs.getPropertyComplexGroup("address.town"));
		Assert.assertEquals("address", vocs.getPropertyComplexGroup("address.postCode"));
		Assert.assertEquals("", vocs.getPropertyComplexGroup("dateOfBirth"));
		Assert.assertEquals("", vocs.getPropertyComplexGroup("sector"));
		Assert.assertEquals("", vocs.getPropertyComplexGroup("industry"));
	}
	
	@Test
	public void testIsPropertyText()
	{		
		Assert.assertTrue(vocs.isPropertyText("name"));
		Assert.assertTrue(vocs.isPropertyText("address"));
		Assert.assertFalse(vocs.isPropertyText("address.number"));
		Assert.assertTrue(vocs.isPropertyText("address.street"));
		Assert.assertTrue(vocs.isPropertyText("address.town"));
		Assert.assertTrue(vocs.isPropertyText("address.postCode"));
		Assert.assertFalse(vocs.isPropertyText("dateOfBirth"));
		Assert.assertTrue(vocs.isPropertyText("sector"));
		Assert.assertTrue(vocs.isPropertyText("industry"));
	}
	
	@Test
	public void testIsPropertyNumber()
	{		
		Assert.assertFalse(vocs.isPropertyNumber("name"));
		Assert.assertFalse(vocs.isPropertyNumber("address"));
		Assert.assertTrue(vocs.isPropertyNumber("address.number"));
		Assert.assertFalse(vocs.isPropertyNumber("address.street"));
		Assert.assertFalse(vocs.isPropertyNumber("address.town"));
		Assert.assertFalse(vocs.isPropertyNumber("address.postCode"));
		Assert.assertFalse(vocs.isPropertyNumber("dateOfBirth"));
		Assert.assertFalse(vocs.isPropertyNumber("sector"));
		Assert.assertFalse(vocs.isPropertyNumber("industry"));
	}
	
	@Test
    public void testIsPropertyDate()
    {       
        Assert.assertFalse(vocs.isPropertyDate("name"));
        Assert.assertFalse(vocs.isPropertyDate("address"));
        Assert.assertFalse(vocs.isPropertyDate("address.number"));
        Assert.assertFalse(vocs.isPropertyDate("address.street"));
        Assert.assertFalse(vocs.isPropertyDate("address.town"));
        Assert.assertFalse(vocs.isPropertyDate("address.postCode"));
        Assert.assertTrue(vocs.isPropertyDate("dateOfBirth"));
        Assert.assertFalse(vocs.isPropertyDate("sector"));
        Assert.assertFalse(vocs.isPropertyDate("industry"));
    }
	
	@Test
    public void testIsPropertyTimestamp()
    {       
        Assert.assertFalse(vocs.isPropertyTimestamp("name"));
        Assert.assertFalse(vocs.isPropertyTimestamp("address"));
        Assert.assertFalse(vocs.isPropertyTimestamp("address.number"));
        Assert.assertFalse(vocs.isPropertyTimestamp("address.street"));
        Assert.assertFalse(vocs.isPropertyTimestamp("address.town"));
        Assert.assertFalse(vocs.isPropertyTimestamp("address.postCode"));
        Assert.assertFalse(vocs.isPropertyTimestamp("dateOfBirth"));
        Assert.assertTrue(vocs.isPropertyTimestamp("timestamp"));
        Assert.assertFalse(vocs.isPropertyTimestamp("sector"));
        Assert.assertFalse(vocs.isPropertyTimestamp("industry"));
    }
	
	@Test
    public void testIsPropertyTime()
    {       
        Assert.assertFalse(vocs.isPropertyTime("name"));
        Assert.assertFalse(vocs.isPropertyTime("address"));
        Assert.assertFalse(vocs.isPropertyTime("address.number"));
        Assert.assertFalse(vocs.isPropertyTime("address.street"));
        Assert.assertFalse(vocs.isPropertyTime("address.town"));
        Assert.assertFalse(vocs.isPropertyTime("address.postCode"));
        Assert.assertFalse(vocs.isPropertyTime("dateOfBirth"));
        Assert.assertFalse(vocs.isPropertyTime("timestamp"));
        Assert.assertTrue(vocs.isPropertyTime("time"));
        Assert.assertFalse(vocs.isPropertyTime("sector"));
        Assert.assertFalse(vocs.isPropertyTime("industry"));
    }
	
	@Test
    public void testIsPropertyBoolean()
    {       
        Assert.assertFalse(vocs.isPropertyBoolean("name"));
        Assert.assertFalse(vocs.isPropertyBoolean("address"));
        Assert.assertFalse(vocs.isPropertyBoolean("address.number"));
        Assert.assertFalse(vocs.isPropertyBoolean("address.street"));
        Assert.assertFalse(vocs.isPropertyBoolean("address.town"));
        Assert.assertFalse(vocs.isPropertyBoolean("address.postCode"));
        Assert.assertFalse(vocs.isPropertyBoolean("dateOfBirth"));
        Assert.assertFalse(vocs.isPropertyBoolean("timestamp"));
        Assert.assertFalse(vocs.isPropertyBoolean("time"));
        Assert.assertFalse(vocs.isPropertyBoolean("sector"));
        Assert.assertFalse(vocs.isPropertyBoolean("industry"));
        Assert.assertTrue(vocs.isPropertyBoolean("kids"));
    }
	
	@Test
	public void testIdFields() {
		Assert.assertEquals(1, vocs.getIdFields().size());
		Assert.assertEquals("name", vocs.getIdFields().get(0));
	}

	@Test
	public void testCreateEmptyEntityProperty() {
		Assert.assertEquals(null, vocs.createEmptyEntityProperty("address.number").getValue());
		Assert.assertEquals("", vocs.createEmptyEntityProperty("name").getValue());
		Assert.assertTrue(vocs.createEmptyEntityProperty("dateOfBirth").getValue() instanceof Date);
		Assert.assertEquals(null, vocs.createEmptyEntityProperty("industry").getValue());
	}
	
	@Test
	public void testTimePropertyAsString() {
		Assert.assertEquals("00:00:00.001", vocs.getPropertyValueAsString("time", new LocalTime(1, DateTimeZone.UTC)));
	}
	
	@Test
	public void testEntityList() {
		String entity1 = "CountryList";
		String entity2 = "CustomerInfo";
		ResourceMetadataManager rmManager = new ResourceMetadataManager();
		Metadata metadata = new Metadata(rmManager);
		EntityMetadata em1 = metadata.getEntityMetadata(entity1);
		EntityMetadata em2 = metadata.getEntityMetadata(entity2);
		
		Assert.assertEquals("CountryList", em1.getEntityName().toString());
		Assert.assertEquals("CustomerInfo", em2.getEntityName().toString());
	}
	
	@Test
	public void testAllEntity() {
		String entity1 = "CountryList";
		String entity2 = "CustomerInfo";
		ResourceMetadataManager rmManager = new ResourceMetadataManager();
		Metadata metadata = new Metadata(rmManager);
		Map<String, EntityMetadata> mapEntity = metadata.getEntitiesMetadata();
		
		Assert.assertTrue(mapEntity.isEmpty());
		
		EntityMetadata em1 = metadata.getEntityMetadata(entity1);
		EntityMetadata em2 = metadata.getEntityMetadata(entity2);
		
		Assert.assertEquals("CountryList", em1.getEntityName().toString());
		Assert.assertEquals("CustomerInfo", em2.getEntityName().toString());

		Assert.assertTrue(mapEntity.containsKey(entity1));
		Assert.assertTrue(mapEntity.containsKey(entity2));
	}
	
	
	@Test
	public void testEntityWithDiaplyAndFilterOnlyTerms() {
		String entityName = "DisplayAndFilterOnly";
		ResourceMetadataManager rmManager = new ResourceMetadataManager();
		Metadata metadata = new Metadata(rmManager);
		EntityMetadata em = metadata.getEntityMetadata(entityName);
		
		Assert.assertFalse(em.isPropertyFilterOnly("Sector"));
		Assert.assertFalse(em.isPropertyDisplayOnly("Sector"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("AccountOfficer"));
		Assert.assertFalse(em.isPropertyDisplayOnly("AccountOfficer"));
		
		Assert.assertTrue(em.isPropertyFilterOnly("Industry"));
		Assert.assertFalse(em.isPropertyDisplayOnly("Industry"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("Nationality"));
		Assert.assertTrue(em.isPropertyDisplayOnly("Nationality"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("Residence"));
		Assert.assertFalse(em.isPropertyDisplayOnly("Residence"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("Hdr1"));
		Assert.assertTrue(em.isPropertyDisplayOnly("Hdr1"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("CusNo"));
		Assert.assertFalse(em.isPropertyDisplayOnly("CusNo"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("Mnem"));
		Assert.assertTrue(em.isPropertyDisplayOnly("Mnem"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("ShortNameMvGroup.LanguageCode"));
		Assert.assertFalse(em.isPropertyDisplayOnly("ShortNameMvGroup.LanguageCode"));
		
		Assert.assertTrue(em.isPropertyFilterOnly("ShortNameMvGroup.ShortName"));
		Assert.assertFalse(em.isPropertyDisplayOnly("ShortNameMvGroup.ShortName"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("Natlty"));
		Assert.assertFalse(em.isPropertyDisplayOnly("Natlty"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("Res"));
		Assert.assertFalse(em.isPropertyDisplayOnly("Res"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("AcOfcr"));
		Assert.assertFalse(em.isPropertyDisplayOnly("AcOfcr"));
		
		Assert.assertFalse(em.isPropertyFilterOnly("Ind"));
		Assert.assertFalse(em.isPropertyDisplayOnly("Ind"));
		
		Assert.assertTrue(em.isPropertyFilterOnly("Sect"));
		Assert.assertFalse(em.isPropertyDisplayOnly("Sect"));
	}
	
	@Test
    public void testGetSimplePropertyName() {
	    Assert.assertNotNull(vocs.getSimplePropertyName("sector"));
	    Assert.assertEquals(vocs.getSimplePropertyName("sector"), "sector");
	}
	
	@Test
    public void testGetVocabulary() {
	    Vocabulary voc = new Vocabulary();
	    vocs.setVocabulary(voc);	    
        Assert.assertNotNull(vocs.getVocabulary());
    }
	
	@Test
    public void testGetTopLevelPropertiesy() {     
        Assert.assertNotNull(vocs.getTopLevelProperties());
    }
}
