package com.temenos.interaction.core.media.atom;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.IgnoreTextAndAttributeValuesDifferenceListener;
import org.junit.Test;
import org.odata4j.core.ImmutableList;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;

import com.temenos.interaction.core.resource.EntityResource;

public class TestAtomXMLProvider {
	public class MockAtomXMLProvider extends AtomXMLProvider {
		public MockAtomXMLProvider(EdmDataServices edmDataServices) {
			super(edmDataServices);
		}
		public void setUriInfo(UriInfo uriInfo) {
			super.setUriInfo(uriInfo);
		}
	};

	@Test
	public void testWriteEntityResourceOEntity_XML() throws Exception {
		EdmDataServices mockEDS = createMockFlightEdmDataServices();
		EntityResource<OEntity> er = createMockEntityResourceOEntity();
		
        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(er) {};

		//Create provider
		MockAtomXMLProvider p = new MockAtomXMLProvider(mockEDS);
		UriInfo uriInfo = mock(UriInfo.class);
		URI uri = new URI("http://localhost:8080/responder/rest");
		when(uriInfo.getBaseUri()).thenReturn(uri);
		p.setUriInfo(uriInfo);
		
		//Serialize resource
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_XML_TYPE, null, bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");

		//Assert xml string but ignore text and attribute values
		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xml:base=\"http://localhost:8080/responder/rest\"><id>http://localhost:8080/responder/restFlight('123')</id><title type=\"text\"></title><updated>2012-03-14T11:29:19Z</updated><author><name></name></author><link rel=\"edit\" title=\"Flight\" href=\"Flight('123')\"></link><category term=\"InteractionTest.Flight\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"></category><content type=\"application/xml\"><m:properties><d:id>1</d:id><d:flight>EI218</d:flight></m:properties></content></entry>";
	    DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
	    Diff myDiff = new Diff(responseString, expectedXML);
	    myDiff.overrideDifferenceListener(myDifferenceListener);
	    assertTrue(myDiff.similar());		
	}

	@Test
	public void testWriteEntityResourceOEntity_AtomXML() throws Exception {
		EdmDataServices mockEDS = createMockFlightEdmDataServices();
		EntityResource<OEntity> er = createMockEntityResourceOEntity();
		
        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(er) {};

		//Create provider
		MockAtomXMLProvider p = new MockAtomXMLProvider(mockEDS);
		UriInfo uriInfo = mock(UriInfo.class);
		URI uri = new URI("http://localhost:8080/responder/rest");
		when(uriInfo.getBaseUri()).thenReturn(uri);
		p.setUriInfo(uriInfo);
		
		//Serialize resource
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");

		//Assert xml string but ignore text and attribute values
		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xml:base=\"http://localhost:8080/responder/rest\"><id>http://localhost:8080/responder/restFlight('123')</id><title type=\"text\"></title><updated>2012-03-14T11:29:19Z</updated><author><name></name></author><link rel=\"edit\" title=\"Flight\" href=\"Flight('123')\"></link><category term=\"InteractionTest.Flight\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"></category><content type=\"application/xml\"><m:properties><d:id>1</d:id><d:flight>EI218</d:flight></m:properties></content></entry>";
	    DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
	    Diff myDiff = new Diff(responseString, expectedXML);
	    myDiff.overrideDifferenceListener(myDifferenceListener);
	    assertTrue(myDiff.similar());		
	}
	
	private EdmDataServices createMockFlightEdmDataServices() {
		EdmDataServices mockEDS = mock(EdmDataServices.class);

		//Mock EdmDataServices
		List<String> keys = new ArrayList<String>();
		keys.add("MyId");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("MyId").setType(EdmSimpleType.STRING);
		properties.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName("Flight").addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Flight").setEntityType(eet);
		List<EdmEntityType.Builder> mockEntityTypes = new ArrayList<EdmEntityType.Builder>();
		mockEntityTypes.add(eet);
		List<EdmEntitySet.Builder> mockEntitySets = new ArrayList<EdmEntitySet.Builder>();
		mockEntitySets.add(ees);
		EdmEntityContainer.Builder eec = EdmEntityContainer.newBuilder().setName("MyEntityContainer").addEntitySets(mockEntitySets);
		List<EdmEntityContainer.Builder> mockEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
		mockEntityContainers.add(eec);
		EdmSchema.Builder es = EdmSchema.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").addEntityTypes(mockEntityTypes).addEntityContainers(mockEntityContainers);
		List<EdmSchema> mockSchemas = new ArrayList<EdmSchema>();
		mockSchemas.add(es.build());
		when(mockEDS.getSchemas()).thenReturn(ImmutableList.copyOf(mockSchemas));

		return mockEDS;
	}
	
	@SuppressWarnings("unchecked")
	private EntityResource<OEntity> createMockEntityResourceOEntity() {
		EntityResource<OEntity> er = mock(EntityResource.class);

		//Create an entity set
		List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("id").setType(EdmSimpleType.STRING);
		eprops.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Flight").addKeys(Arrays.asList("id")).addProperties(eprops);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Flight").setEntityType(eet);

		//Create an OEntity
		OEntityKey entityKey = OEntityKey.create("123");
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("id", "1"));
		properties.add(OProperties.string("flight", "EI218"));
		OEntity entity = OEntities.create(ees.build(), entityKey, properties, new ArrayList<OLink>());
		when(er.getEntity()).thenReturn(entity);
		return er;
	}
}
