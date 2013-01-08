package com.temenos.interaction.sdk.interaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

/**
 * Unit test for {@link InteractionModel}.
 */
public class TestInteractionModel {

	@Test
	public void testGetUriTemplateParametersMetadata() {
		Metadata mockMetadata = createMockCustomersMetadata();
		InteractionModel model = new InteractionModel(mockMetadata);
		assertTrue(model.getResourceStateMachines().size() == 1);
		assertEquals("Customer", model.getResourceStateMachines().get(0).getEntityName());
		assertEquals("{id2},'{id1}',{id4},'{id3}'", model.getResourceStateMachines().get(0).getPathParametersTemplate());
	}

	//@Test
	public void testGetUriTemplateParametersEdmDataServices() {
		EdmDataServices mockMetadata = createMockCustomersEdmDataServices();
		
		InteractionModel model = new InteractionModel(mockMetadata);
		assertTrue(model.getResourceStateMachines().size() == 1);
		assertEquals("Customer", model.getResourceStateMachines().get(0).getEntityName());
		assertEquals("{id2},'{id1}',{id4},'{id3}'", model.getResourceStateMachines().get(0).getPathParametersTemplate());
	}
	
	@Test
	public void testEntitySetsEdmDataServices() {
		EdmDataServices mockEdmMetadata = createMockCustomersEdmDataServices();
		
		InteractionModel model = new InteractionModel(mockEdmMetadata);
		assertEquals(1, model.getResourceStateMachines().size());
		assertEquals("Customers", model.getResourceStateMachines().get(0).getCollectionStateName());
		assertEquals("Customer", model.getResourceStateMachines().get(0).getEntityName());
	}

	private Metadata createMockCustomersMetadata() {
		//Define vocabulary for this entity
		Metadata metadata = new Metadata("Customers");
		EntityMetadata vocs = new EntityMetadata("Customer");
				
		Vocabulary id1 = new Vocabulary();
		id1.setTerm(new TermIdField(true));
		vocs.setPropertyVocabulary("id1", id1);

		Vocabulary id2 = new Vocabulary();
		id2.setTerm(new TermIdField(true));
		id2.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		vocs.setPropertyVocabulary("id2", id2);

		Vocabulary id3 = new Vocabulary();
		id3.setTerm(new TermIdField(true));
		id3.setTerm(new TermValueType(TermValueType.DATE));
		vocs.setPropertyVocabulary("id3", id3);

		Vocabulary id4 = new Vocabulary();
		id4.setTerm(new TermIdField(true));
		id4.setTerm(new TermValueType(TermValueType.BOOLEAN));
		vocs.setPropertyVocabulary("id4", id4);
		
		metadata.setEntityMetadata(vocs);
		
		return metadata;
	}

	private EdmDataServices createMockCustomersEdmDataServices() {
		EdmDataServices.Builder mockEDS = EdmDataServices.newBuilder();

		//Mock EdmDataServices
		List<String> keys = new ArrayList<String>();
		keys.add("id1");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("id1").setType(EdmSimpleType.STRING);
		properties.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName("Customer").addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Customers").setEntityType(eet);
		List<EdmEntityType.Builder> mockEntityTypes = new ArrayList<EdmEntityType.Builder>();
		mockEntityTypes.add(eet);
		List<EdmEntitySet.Builder> mockEntitySets = new ArrayList<EdmEntitySet.Builder>();
		mockEntitySets.add(ees);
		EdmEntityContainer.Builder eec = EdmEntityContainer.newBuilder().setName("MyEntityContainer").addEntitySets(mockEntitySets);
		List<EdmEntityContainer.Builder> mockEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
		mockEntityContainers.add(eec);
		EdmSchema.Builder es = EdmSchema.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").addEntityTypes(mockEntityTypes).addEntityContainers(mockEntityContainers);
		List<EdmSchema.Builder> mockSchemas = new ArrayList<EdmSchema.Builder>();
		mockSchemas.add(es);
		mockEDS.addSchemas(mockSchemas);

		return mockEDS.build();
	}

}
