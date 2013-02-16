package com.temenos.interaction.core.entity;

import java.util.Date;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

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
		
		Vocabulary vocNumbert = new Vocabulary();
		vocNumbert.setTerm(new TermComplexGroup("address"));
		vocNumbert.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		vocs.setPropertyVocabulary("number", vocNumbert);
		
		Vocabulary vocStreet = new Vocabulary();
		vocStreet.setTerm(new TermComplexGroup("address"));
		vocStreet.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("street", vocStreet);
		
		Vocabulary vocTown = new Vocabulary();
		vocTown.setTerm(new TermComplexGroup("address"));
		vocTown.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("town", vocStreet);
		
		Vocabulary vocPostCode = new Vocabulary();
		vocPostCode.setTerm(new TermComplexGroup("address"));
		vocPostCode.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("postCode", vocPostCode);
		
		Vocabulary vocDob = new Vocabulary();
		vocDob.setTerm(new TermComplexType(false));
		vocDob.setTerm(new TermValueType(TermValueType.DATE));
		vocs.setPropertyVocabulary("dateOfBirth", vocDob);
		
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
		Assert.assertEquals(9, propertyKeys.size());
		Assert.assertTrue(propertyKeys.contains("name"));
		Assert.assertTrue(propertyKeys.contains("address"));
		Assert.assertTrue(propertyKeys.contains("number"));
		Assert.assertTrue(propertyKeys.contains("street"));
		Assert.assertTrue(propertyKeys.contains("town"));
		Assert.assertTrue(propertyKeys.contains("postCode"));
		Assert.assertTrue(propertyKeys.contains("dateOfBirth"));
		Assert.assertTrue(propertyKeys.contains("sector"));
		Assert.assertTrue(propertyKeys.contains("industry"));
	}
	
	@Test
	public void testIsPropertyComplex()
	{		
		Assert.assertFalse(vocs.isPropertyComplex("name"));
		Assert.assertTrue(vocs.isPropertyComplex("address"));
		Assert.assertFalse(vocs.isPropertyComplex("number"));
		Assert.assertFalse(vocs.isPropertyComplex("street"));
		Assert.assertFalse(vocs.isPropertyComplex("town"));
		Assert.assertFalse(vocs.isPropertyComplex("postCode"));
		Assert.assertFalse(vocs.isPropertyComplex("dateOfBirth"));
		Assert.assertFalse(vocs.isPropertyComplex("sector"));
		Assert.assertFalse(vocs.isPropertyComplex("industry"));
	}
	
	@Test
	public void testGetComplexGroup()
	{		
		Assert.assertEquals("", vocs.getPropertyComplexGroup("name"));
		Assert.assertEquals("", vocs.getPropertyComplexGroup("address"));
		Assert.assertEquals("address", vocs.getPropertyComplexGroup("number"));
		Assert.assertEquals("address", vocs.getPropertyComplexGroup("street"));
		Assert.assertEquals("address", vocs.getPropertyComplexGroup("town"));
		Assert.assertEquals("address", vocs.getPropertyComplexGroup("postCode"));
		Assert.assertEquals("", vocs.getPropertyComplexGroup("dateOfBirth"));
		Assert.assertEquals("", vocs.getPropertyComplexGroup("sector"));
		Assert.assertEquals("", vocs.getPropertyComplexGroup("industry"));
	}
	
	@Test
	public void testIsPropertyText()
	{		
		Assert.assertTrue(vocs.isPropertyText("name"));
		Assert.assertTrue(vocs.isPropertyText("address"));
		Assert.assertFalse(vocs.isPropertyText("number"));
		Assert.assertTrue(vocs.isPropertyText("street"));
		Assert.assertTrue(vocs.isPropertyText("town"));
		Assert.assertTrue(vocs.isPropertyText("postCode"));
		Assert.assertFalse(vocs.isPropertyText("dateOfBirth"));
		Assert.assertTrue(vocs.isPropertyText("sector"));
		Assert.assertTrue(vocs.isPropertyText("industry"));
	}
	
	@Test
	public void testIsPropertyNumber()
	{		
		Assert.assertFalse(vocs.isPropertyNumber("name"));
		Assert.assertFalse(vocs.isPropertyNumber("address"));
		Assert.assertTrue(vocs.isPropertyNumber("number"));
		Assert.assertFalse(vocs.isPropertyNumber("street"));
		Assert.assertFalse(vocs.isPropertyNumber("town"));
		Assert.assertFalse(vocs.isPropertyNumber("postCode"));
		Assert.assertFalse(vocs.isPropertyNumber("dateOfBirth"));
		Assert.assertFalse(vocs.isPropertyNumber("sector"));
		Assert.assertFalse(vocs.isPropertyNumber("industry"));
	}
	
	@Test
	public void testIdFields() {
		Assert.assertEquals(1, vocs.getIdFields().size());
		Assert.assertEquals("name", vocs.getIdFields().get(0));
	}

	@Test
	public void testCreateEmptyEntityProperty() {
		Assert.assertEquals(0, ((Long) vocs.createEmptyEntityProperty("number").getValue()).longValue());
		Assert.assertEquals("", vocs.createEmptyEntityProperty("name").getValue());
		Assert.assertTrue(vocs.createEmptyEntityProperty("dateOfBirth").getValue() instanceof Date);
	}
}
