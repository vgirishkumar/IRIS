package com.temenos.interaction.media.hal.metadata;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.GenericEntity;

import org.junit.Assert;
import org.junit.Test;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
import com.temenos.interaction.core.resource.MetaDataResource;

public class TestMetadataProvider {

	@SuppressWarnings("unchecked")
	@Test
	public void testWriteMetadataResource() throws Exception {
		MetaDataResource<Metadata> mr = mock(MetaDataResource.class);
		
		Metadata mockMetadata = createMockFlightMetadata();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockMetadata);
		
		//Serialize metadata resource
		MetaDataProvider p = new MetaDataProvider();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mr, MetaDataResource.class, Metadata.class, null, com.temenos.interaction.media.hal.MediaType.APPLICATION_HAL_JSON_TYPE, null, bos);

		String expected = "{  \"_links\" : {    \"self\" : { \"href\" : \"http://localhost:8080/example/api/$metadata\" }  },  \"modelName\" : \"Customers\",  \"entities\" : \"Customer\"}";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		Assert.assertEquals(expected, responseString);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWriteMetadataResourceGenericEntity() throws Exception {
		MetaDataResource<Metadata> mr = mock(MetaDataResource.class);
		
		Metadata mockMetadata = createMockFlightMetadata();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockMetadata);

        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<MetaDataResource<Metadata>> ge = new GenericEntity<MetaDataResource<Metadata>>(mr) {};
		
		//Serialize metadata resource
		MetaDataProvider p = new MetaDataProvider();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, com.temenos.interaction.media.hal.MediaType.APPLICATION_HAL_JSON_TYPE, null, bos);

		String expected = "{  \"_links\" : {    \"self\" : { \"href\" : \"http://localhost:8080/example/api/$metadata\" }  },  \"modelName\" : \"Customers\",  \"entities\" : \"Customer\"}";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		Assert.assertEquals(expected, responseString);
	}
	
	private Metadata createMockFlightMetadata() {
		//Define vocabulary for this entity
		Metadata metadata = new Metadata("Customers");
		EntityMetadata vocs = new EntityMetadata("Customer");
				
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
		vocs.setPropertyVocabulary("dateOfBirth", vocDob);
		
		Vocabulary vocSector = new Vocabulary();
		vocSector.setTerm(new TermComplexType(false));
		vocs.setPropertyVocabulary("sector", vocSector);
		
		Vocabulary vocIndustry = new Vocabulary();
		vocIndustry.setTerm(new TermComplexType(false));
		vocs.setPropertyVocabulary("industry", vocIndustry);
		
		metadata.setEntityMetadata(vocs);
		
		return metadata;
	}
}
