package com.temenos.interaction.core.media.atom;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.IgnoreTextAndAttributeValuesDifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.odata4j.format.Entry;
import org.odata4j.format.xml.AtomEntryFormatParser;
import org.odata4j.internal.FeedCustomizationMapping;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
import com.temenos.interaction.core.hypermedia.EntityTransformer;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceRegistry;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transformer;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.rim.HTTPResourceInteractionModel;
import com.temenos.interaction.core.rim.ResourceInteractionModel;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OEntityKey.class, AtomXMLProvider.class})
public class TestAtomXMLProvider {
	
	private final static String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xml:base=\"http://localhost:8080/responder/rest\"><id>http://localhost:8080/responder/restFlight('123')</id><title type=\"text\"></title><updated>2012-03-14T11:29:19Z</updated><author><name></name></author><category term=\"InteractionTest.Flight\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"></category><content type=\"application/xml\"><m:properties><d:id>1</d:id><d:flight>EI218</d:flight></m:properties></content></entry>";
	private final static String EXPECTED_ENTITY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xml:base=\"http://localhost:8080/responder/rest/\"><id>http://localhost:8080/responder/rest/Flight(123)</id><title type=\"text\"></title><updated>2012-10-04T10:17:00Z</updated><author><name></name></author><category term=\"FlightModelModel.Flight\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"></category><content type=\"application/xml\"><m:properties><d:id>123</d:id><d:flight>EI218</d:flight><d:MvSvGroup><d:MvSvStartGroup><d:MvSvEnd>mv sv end 1:1</d:MvSvEnd><d:MvSvStart>mv sv start 1:1</d:MvSvStart></d:MvSvStartGroup><d:MvSvStartGroup><d:MvSvEnd>mv sv end 1:2</d:MvSvEnd><d:MvSvStart>mv sv start 1:2</d:MvSvStart></d:MvSvStartGroup><d:MvSv>mv sv 1:1</d:MvSv></d:MvSvGroup><d:MvSvGroup><d:MvSvStartGroup><d:MvSvEnd>mv sv end 2:1</d:MvSvEnd><d:MvSvStart>mv sv start 2:1</d:MvSvStart></d:MvSvStartGroup><d:MvSvStartGroup><d:MvSvEnd>mv sv end 2:2</d:MvSvEnd><d:MvSvStart>mv sv start 2:2</d:MvSvStart></d:MvSvStartGroup><d:MvSv>mv sv 2:1</d:MvSv></d:MvSvGroup></m:properties></content></entry>";
	
	public class MockAtomXMLProvider extends AtomXMLProvider {
		public MockAtomXMLProvider(EdmDataServices edmDataServices) {
			super(edmDataServices, mock(Metadata.class), new ResourceRegistry(edmDataServices, new HashSet<HTTPResourceInteractionModel>()), new EntityTransformer());
		}
		public MockAtomXMLProvider(EdmDataServices edmDataServices, Metadata metadata) {
			//super(null, metadata, new EntityTransformer());
			super(edmDataServices, metadata, new ResourceRegistry(edmDataServices, new HashSet<HTTPResourceInteractionModel>()), new EntityTransformer());
		}
		public void setUriInfo(UriInfo uriInfo) {
			super.setUriInfo(uriInfo);
		}
	};

	@Test
	public void testWriteEntityResourceOEntity_XML() throws Exception {
		EdmEntitySet ees = createMockEdmEntitySet();
		EdmDataServices mockEDS = createMockFlightEdmDataServices();
		EntityResource<OEntity> er = createMockEntityResourceOEntity(ees);
		
		when(mockEDS.getEdmEntitySet(any(EdmEntityType.class))).thenReturn(ees);
		
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
		System.out.println(responseString);

		//Assert xml string but ignore text and attribute values
	    DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
	    Diff myDiff = new Diff(responseString, EXPECTED_XML);
	    myDiff.overrideDifferenceListener(myDifferenceListener);
	    assertTrue(myDiff.similar());		
	}

	@Test
	public void testWriteEntityResourceOEntity_AtomXML() throws Exception {
		EdmEntitySet ees = createMockEdmEntitySet();
		EdmDataServices mockEDS = createMockFlightEdmDataServices();
		EntityResource<OEntity> er = createMockEntityResourceOEntity(ees);
		
		when(mockEDS.getEdmEntitySet(any(EdmEntityType.class))).thenReturn(ees);

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
	    DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
	    Diff myDiff = new Diff(responseString, EXPECTED_XML);
	    myDiff.overrideDifferenceListener(myDifferenceListener);
	    assertTrue(myDiff.similar());		
	}
	
	private EdmEntitySet createMockEdmEntitySet() {
		// Create an entity set
		List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("id").setType(EdmSimpleType.STRING);
		eprops.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Flight").addKeys(Arrays.asList("id")).addProperties(eprops);
		EdmEntitySet.Builder eesb = EdmEntitySet.newBuilder().setName("Flight").setEntityType(eet);
		return eesb.build();
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
	private EntityResource<OEntity> createMockEntityResourceOEntity(EdmEntitySet ees) {
		EntityResource<OEntity> er = mock(EntityResource.class);

		//Create an OEntity
		OEntityKey entityKey = OEntityKey.create("123");
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("id", "1"));
		properties.add(OProperties.string("flight", "EI218"));
		OEntity entity = OEntities.create(ees, entityKey, properties, new ArrayList<OLink>());
		when(er.getEntity()).thenReturn(entity);
		return er;
	}
	
	private Metadata createMockFlightMetadata() {
		Metadata mockMetadata = new Metadata("FlightModel");
		
		// Define vocabulary for this entity - minimum fields for an input
		EntityMetadata entityMetadata = new EntityMetadata("Flight");
		
		Vocabulary voc_id = new Vocabulary();
		voc_id.setTerm(new TermValueType(TermValueType.TEXT));
		voc_id.setTerm(new TermIdField(true));
		entityMetadata.setPropertyVocabulary("id", voc_id);
		
		Vocabulary voc_flight = new Vocabulary();
		voc_flight.setTerm(new TermValueType(TermValueType.TEXT));
		entityMetadata.setPropertyVocabulary("flight", voc_flight);
			
		// MvSv Group
		Vocabulary voc_MvSvGroup = new Vocabulary();
		voc_MvSvGroup.setTerm(new TermComplexType(true));
		entityMetadata.setPropertyVocabulary("MvSvGroup", voc_MvSvGroup);
		
		Vocabulary voc_MvSv = new Vocabulary();
		voc_MvSv.setTerm(new TermComplexGroup("MvSvGroup"));
		voc_MvSv.setTerm(new TermValueType(TermValueType.TEXT));
		entityMetadata.setPropertyVocabulary("MvSv", voc_MvSv);
		
		Vocabulary voc_MvSvStartGroup = new Vocabulary();
		voc_MvSvStartGroup.setTerm(new TermComplexType(true));
		voc_MvSvStartGroup.setTerm(new TermComplexGroup("MvSvGroup"));
		entityMetadata.setPropertyVocabulary("MvSvStartGroup", voc_MvSvStartGroup);
	
		Vocabulary voc_MvSvStart = new Vocabulary();
		voc_MvSvStart.setTerm(new TermComplexGroup("MvSvStartGroup"));
		voc_MvSvStart.setTerm(new TermValueType(TermValueType.TEXT));
		entityMetadata.setPropertyVocabulary("MvSvStart", voc_MvSvStart);
		
		Vocabulary voc_MvSvEnd = new Vocabulary();
		voc_MvSvEnd.setTerm(new TermComplexGroup("MvSvStartGroup"));
		voc_MvSvEnd.setTerm(new TermValueType(TermValueType.TEXT));
		entityMetadata.setPropertyVocabulary("MvSvEnd", voc_MvSvEnd);

		mockMetadata.setEntityMetadata(entityMetadata);

		return mockMetadata;
	}
	
	@SuppressWarnings("unchecked")
	private EntityResource<Entity> createMockEntityResourceEntity() {
		EntityResource<Entity> er = mock(EntityResource.class);

		// Create entity
		EntityProperties properties = new EntityProperties();
		properties.setProperty(new EntityProperty("id", "123"));
		properties.setProperty(new EntityProperty("flight", "EI218"));
		
		// Multi-value group with sub-value group
		List<EntityProperties> mvSvGroup = new ArrayList<EntityProperties>();
		EntityProperties mvSvGroup1 = new EntityProperties();
			mvSvGroup1.setProperty(new EntityProperty("MvSv", "mv sv 1:1"));
			List<EntityProperties> mvSvStartGroup1 = new ArrayList<EntityProperties>();
			// Sub-value group
				EntityProperties mvSvStartGroup1_1 = new EntityProperties();
					mvSvStartGroup1_1.setProperty(new EntityProperty("MvSvStart", "mv sv start 1:1"));
					mvSvStartGroup1_1.setProperty(new EntityProperty("MvSvEnd", "mv sv end 1:1"));
					mvSvStartGroup1.add(mvSvStartGroup1_1);
				EntityProperties mvSvStartGroup1_2 = new EntityProperties();
					mvSvStartGroup1_2.setProperty(new EntityProperty("MvSvStart", "mv sv start 1:2"));
					mvSvStartGroup1_2.setProperty(new EntityProperty("MvSvEnd", "mv sv end 1:2"));
					mvSvStartGroup1.add(mvSvStartGroup1_2);
			mvSvGroup1.setProperty(new EntityProperty("MvSvStartGroup", mvSvStartGroup1));
		mvSvGroup.add(mvSvGroup1);

		// Multi-value group
		EntityProperties mvSvGroup2 = new EntityProperties();
			mvSvGroup2.setProperty(new EntityProperty("MvSv", "mv sv 2:1"));
			List<EntityProperties> mvSvStartGroup2 = new ArrayList<EntityProperties>();
				// Sub-value group
				EntityProperties mvSvStartGroup2_1 = new EntityProperties();
					mvSvStartGroup2_1.setProperty(new EntityProperty("MvSvStart", "mv sv start 2:1"));
					mvSvStartGroup2_1.setProperty(new EntityProperty("MvSvEnd", "mv sv end 2:1"));
					mvSvStartGroup2.add(mvSvStartGroup2_1);
				EntityProperties mvSvStartGroup2_2 = new EntityProperties();
					mvSvStartGroup2_2.setProperty(new EntityProperty("MvSvStart", "mv sv start 2:2"));
					mvSvStartGroup2_2.setProperty(new EntityProperty("MvSvEnd", "mv sv end 2:2"));
					mvSvStartGroup2.add(mvSvStartGroup2_2);
			mvSvGroup2.setProperty(new EntityProperty("MvSvStartGroup", mvSvStartGroup2));
		mvSvGroup.add(mvSvGroup2);
				
		properties.setProperty(new EntityProperty("MvSvGroup", mvSvGroup));
		// End Multi-value group
	
		Entity entity = new Entity("Flight", properties);
		when(er.getEntity()).thenReturn(entity);
		return er;
	}
	
	@Test (expected = WebApplicationException.class)
	public void testUnhandledRawType() throws IOException {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceRegistry registry = mock(ResourceRegistry.class);

		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), registry, new EntityTransformer());
        // Wrap an unsupported resource into a JAX-RS GenericEntity instance
		GenericEntity<MetaDataResource<String>> ge = new GenericEntity<MetaDataResource<String>>(new MetaDataResource<String>("")) {};
		// will throw exception if we check the class properly
		Annotation[] annotations = null;
		MediaType mediaType = null;
		MultivaluedMap<String, String> headers = null;
		InputStream content = null;
		ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
	}

	/*
	 * Wink does not seem to supply us with a Generic type so we must accept everything and hope for the best
	@Test (expected = WebApplicationException.class)
	public void testUnhandledGenericType() throws IOException {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceRegistry registry = mock(ResourceRegistry.class);

		AtomXMLProvider ap = new AtomXMLProvider(metadata, registry);
        // Wrap an unsupported entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<String>> ge = new GenericEntity<EntityResource<String>>(new EntityResource<String>(null)) {};
		// will throw exception if we check the class properly
		Annotation[] annotations = null;
		MediaType mediaType = null;
		MultivaluedMap<String, String> headers = null;
		InputStream content = null;
		ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
	}
	 */

	@Test
	public void testReadPath() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceRegistry registry = mock(ResourceRegistry.class);
		ResourceInteractionModel rim = mock(ResourceInteractionModel.class);
		when(rim.getCurrentState()).thenReturn(mock(ResourceState.class));
		when(registry.getResourceInteractionModel(anyString())).thenReturn(rim);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParser mockParser = mock(AtomEntryFormatParser.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParser.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), registry, new EntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource/2");
		ap.setUriInfo(uriInfo);
		
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), null, null, null, new ByteArrayInputStream(new byte[0]));
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());
		
		// verify get rim with /test/someresource
		verify(registry).getResourceInteractionModel("/test/someresource");
		// verify static with entity key "2"
		verifyStatic();
		OEntityKey.parse("2");
	}

	@Test
	public void testReadPathNoEntityKey() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceRegistry registry = mock(ResourceRegistry.class);
		ResourceInteractionModel rim = mock(ResourceInteractionModel.class);
		when(rim.getCurrentState()).thenReturn(mock(ResourceState.class));
		when(registry.getResourceInteractionModel("/test/someresource")).thenReturn(rim);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParser mockParser = mock(AtomEntryFormatParser.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParser.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), registry, new EntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource");
		ap.setUriInfo(uriInfo);
		
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), null, null, null, new ByteArrayInputStream(new byte[0]));
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());
		
		// verify get rim with /test/someresource
		verify(registry).getResourceInteractionModel("/test/someresource");
		// verify static with entity key "2"
		verifyStatic();
		OEntityKey.parse("2");
	}

	@Test
	public void testReadPath404() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceRegistry registry = mock(ResourceRegistry.class);
		// never find any resources
		when(registry.getResourceInteractionModel(anyString())).thenReturn(null);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParser mockParser = mock(AtomEntryFormatParser.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParser.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), registry, new EntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource");
		ap.setUriInfo(uriInfo);
		
		int status = -1;
		try {
			ap.readFrom(RESTResource.class, ge.getType(), null, null, null, new ByteArrayInputStream(new byte[0]));
		} catch (WebApplicationException wae) {
			status = wae.getResponse().getStatus();
		}
		assertEquals(404, status);
	}

	@Test
	public void testReadEntityResourceOEntity() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceRegistry registry = mock(ResourceRegistry.class);
		ResourceInteractionModel rim = mock(ResourceInteractionModel.class);
		when(rim.getCurrentState()).thenReturn(mock(ResourceState.class));
		when(registry.getResourceInteractionModel(anyString())).thenReturn(rim);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParser mockParser = mock(AtomEntryFormatParser.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParser.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), registry, new EntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource/2");
		ap.setUriInfo(uriInfo);
		
		Annotation[] annotations = null;
		MediaType mediaType = null;
		MultivaluedMap<String, String> headers = null;
		InputStream content = new ByteArrayInputStream(new byte[0]);
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());

		// verify parse was called
		verify(mockParser).parse(any(Reader.class));
	}
	
	@Test
	public void testWriteEntityResourceEntity_AtomXML() throws Exception {
		EdmEntitySet ees = createMockEdmEntitySet();
		EdmDataServices mockEDS = createMockFlightEdmDataServices();		
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees);
		
		Metadata mockMetadata = createMockFlightMetadata();
		EntityResource<Entity> er = createMockEntityResourceEntity();
		
        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<Entity>> ge = new GenericEntity<EntityResource<Entity>>(er) {};

		//Create provider
		MockAtomXMLProvider p = new MockAtomXMLProvider(mockEDS, mockMetadata);
		UriInfo uriInfo = mock(UriInfo.class);
		URI uri = new URI("http://localhost:8080/responder/rest/");
		when(uriInfo.getBaseUri()).thenReturn(uri);
		when(uriInfo.getPath()).thenReturn("Flight(123)");
		p.setUriInfo(uriInfo);

		//Serialize resource
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");

		//Assert xml string but ignore text and attribute values
		XMLUnit.setIgnoreWhitespace(true);
	    DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
	    Diff myDiff = new Diff(responseString, EXPECTED_ENTITY_XML);
	    myDiff.overrideDifferenceListener(myDifferenceListener);
	    assertTrue(myDiff.similar());		
	}
	
	@Test
	public void testSkipSelfIfEditExists() {
		/*
		 *  Is this in the OData spec?  It seems that if a 'self' and 'edit' link relation exists
		 *  then ODataExplorer barfs
		 */
		
		AtomXMLProvider provider = new AtomXMLProvider(mock(EdmDataServices.class), mock(Metadata.class), mock(ResourceRegistry.class), mock(Transformer.class));
		List<OLink> olinks = new ArrayList<OLink>();
		provider.addLinkToOLinks(olinks, new Link("title", "self", "href", "type", null));
		assertEquals(1, olinks.size());
		
		// now add the 'edit' link, it should replace the 'self' link
		provider.addLinkToOLinks(olinks, new Link("title", "edit", "href", "type", null));
		assertEquals(1, olinks.size());
		
	}
	
}
