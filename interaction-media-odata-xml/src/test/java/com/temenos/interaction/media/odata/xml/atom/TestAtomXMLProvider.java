package com.temenos.interaction.media.odata.xml.atom;

/*
 * #%L
 * interaction-media-odata-xml
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



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

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
import org.odata4j.exceptions.NotFoundException;
import org.odata4j.format.Entry;
import org.odata4j.format.xml.AtomEntryFormatParserExt;
import org.odata4j.internal.FeedCustomizationMapping;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.commands.odata.OEntityTransformer;
import com.temenos.interaction.core.ExtendedMediaTypes;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.CommandHelper;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.GenericError;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataParser;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.Transformer;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.hypermedia.TransitionCommandSpec;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.web.RequestContext;
import com.temenos.interaction.media.odata.xml.CustomError;
import com.temenos.interaction.media.odata.xml.Flight;
import com.temenos.interaction.media.odata.xml.IgnoreNamedElementsXMLDifferenceListener;
import com.temenos.interaction.odataext.entity.MetadataOData4j;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OEntityKey.class, AtomXMLProvider.class})
public class TestAtomXMLProvider {

	private final static String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xml:base=\"http://localhost:8080/responder/rest\"><id>http://localhost:8080/responder/restFlight('123')</id><title type=\"text\"></title><updated>2012-03-14T11:29:19Z</updated><author><name></name></author><category term=\"InteractionTest.Flight\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"></category><content type=\"application/xml\"><m:properties><d:id>1</d:id><d:flight>EI218</d:flight></m:properties></content></entry>";
	public final static String FLIGHT_ENTRY_XML = "FlightEntry.xml";
	public final static String FLIGHT_ENTRY_SIMPLE_XML = "FlightEntrySimple.xml";
	public final static String FLIGHT_COLLECTION_XML = "FlightsFeed.xml";
	public final static String ATOM_GENERIC_ERROR_ENTRY_XML = "AtomGenericErrorEntry.xml";
	public final static String ATOM_CUSTOM_ERROR_ENTRY_XML = "AtomCustomErrorEntry.xml";
	public final static String EMPTY_FUNDS_TRANSFERS_FEED_XML = "EmptyFundsTransfersFeed.xml";
	
	public class MockAtomXMLProvider extends AtomXMLProvider {
		public MockAtomXMLProvider(EdmDataServices edmDataServices) {
			super(edmDataServices, mock(Metadata.class), mock(ResourceStateMachine.class), new OEntityTransformer());
		}
		public MockAtomXMLProvider(EdmDataServices edmDataServices, Metadata metadata) {
			//super(null, metadata, new EntityTransformer());
			super(edmDataServices, metadata, mock(ResourceStateMachine.class), new OEntityTransformer());
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
	
	private EdmDataServices createMockEdmDataServices(String entitySetName) {
		EdmDataServices edmDataServices = mock(EdmDataServices.class);
		EdmEntitySet entitySet = mock(EdmEntitySet.class);
		when(entitySet.getName()).thenReturn(entitySetName);
		when(edmDataServices.getEdmEntitySet(any(EdmEntityType.class))).thenReturn(entitySet);
		EdmEntityType entityType = mock(EdmEntityType.class);
		when(edmDataServices.findEdmEntityType(anyString())).thenReturn(entityType);
		return edmDataServices;
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
		voc_id.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
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

		//Define a custom error entity
		EntityMetadata mdCustomError = new EntityMetadata("CustomError");
		Vocabulary vocCustomError = new Vocabulary();
		vocCustomError.setTerm(new TermValueType(TermValueType.TEXT));
		mdCustomError.setPropertyVocabulary("mycustomerror", vocCustomError);
		mockMetadata.setEntityMetadata(mdCustomError);
		
		return mockMetadata;
	}

	private Metadata createMockMetadata(String modelName) {
		Metadata metadata = mock(Metadata.class);
		when(metadata.getModelName()).thenReturn(modelName);
		return metadata;
	}
	
	private CollectionResource<Entity> createMockCollectionResourceEntity() {
		List<EntityResource<Entity>> erList = new ArrayList<EntityResource<Entity>>(); 
		erList.add(createMockEntityResourceEntity());
		
		CollectionResource<Entity> cr = new CollectionResource<Entity>("Flights", erList);
		cr.setLinks(new ArrayList<Link>());
		cr.setEntityName("Flight");
		return cr;
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
		when(er.getEntityName()).thenReturn("Flight");
		return er;
	}
	
	@SuppressWarnings("unchecked")
	private EntityResource<Flight> createMockEntityResourceObject() throws Exception {
		String testXMLString = "<resource><Flight><id>123</id><flight>EI218</flight></Flight></resource>";
		JAXBContext jc = JAXBContext.newInstance(EntityResource.class, Flight.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        EntityResource<Flight> er = (EntityResource<Flight>) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
        er.setEntityName("Flight");
        return er;
	}
	
	@Test (expected = WebApplicationException.class)
	public void testUnhandledRawType() throws IOException {
		EdmDataServices metadata = mock(EdmDataServices.class);

		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), mock(ResourceStateMachine.class), new OEntityTransformer());
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

	private final static String METADATA_AIRLINE_XML_FILE = "AirlinesMetadata.xml";
	private Metadata createAirlineMetadata() {
		MetadataParser parserAirline = new MetadataParser();
		InputStream isAirline = parserAirline.getClass().getClassLoader().getResourceAsStream(METADATA_AIRLINE_XML_FILE);
		Metadata metadataAirline = parserAirline.parse(isAirline);
		return metadataAirline;
	}
	private EdmDataServices createAirlineEdmMetadata() {
		// Create mock state machine with entity sets
		ResourceState serviceRoot = new ResourceState("SD", "initial", new ArrayList<Action>(), "/");
		serviceRoot.addTransition(new CollectionResourceState("FlightSchedule", "FlightSchedule", new ArrayList<Action>(), "/FlightSchedule"));
		serviceRoot.addTransition(new CollectionResourceState("Flight", "Flight", new ArrayList<Action>(), "/Flight"));
		serviceRoot.addTransition(new CollectionResourceState("Airport", "Airport", new ArrayList<Action>(), "/Airline"));
		ResourceStateMachine hypermediaEngine = new ResourceStateMachine(serviceRoot);
		
		return (new MetadataOData4j(createAirlineMetadata(), hypermediaEngine)).getMetadata();
	}
	
	@Test
	public void testReadPath() throws Exception {
		// enable mock of the static class (see also verifyStatic)
		mockStatic(OEntityKey.class);
		
		EdmDataServices metadata = createAirlineEdmMetadata();//mock(EdmDataServices.class);
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(mock(CollectionResourceState.class));
		when(rsm.getResourceStatesForPath(anyString())).thenReturn(states);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, createAirlineMetadata(), rsm, new OEntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource/2");
		ap.setUriInfo(uriInfo);
		
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), null, null, null, new ByteArrayInputStream(new String("Antyhing").getBytes()));
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());
		
		// verify get rim with /test/someresource
		verify(rsm).getResourceStatesForPath("/test/someresource");
		// verify static with entity key "2"
		verifyStatic();
		OEntityKey.parse("2");
	}

	/*
	 * The create (POST) will need to parse the OEntity without a key supplied in the path
	 */
	@Test
	public void testReadPathNoEntityKey() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(mock(CollectionResourceState.class));
		when(rsm.getResourceStatesForPathRegex("^/test/someresource(|\\(\\))")).thenReturn(states);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), rsm, new OEntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource");
		ap.setUriInfo(uriInfo);
		
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), null, null, null, new ByteArrayInputStream(new String("Antyhing").getBytes()));
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());
		
		// verify get rim with /test/someresource
		verify(rsm).getResourceStatesForPathRegex("^/test/someresource(|\\(\\))");
	}

	@Test
	public void testReadPath404() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		// never find any resources
		when(rsm.getResourceStatesForPath(anyString())).thenReturn(null);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), rsm, new OEntityTransformer());
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
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(mock(CollectionResourceState.class));
		when(rsm.getResourceStatesForPath(anyString())).thenReturn(states);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), rsm, new OEntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource/2");
		ap.setUriInfo(uriInfo);
		
		Annotation[] annotations = null;
		MediaType mediaType = null;
		MultivaluedMap<String, String> headers = null;
		InputStream content = new ByteArrayInputStream(new String("Antyhing").getBytes());
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());

		// verify parse was called
		verify(mockParser).parse(any(Reader.class));
	}
	
	@Test
	public void testReadNullContent() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(mock(CollectionResourceState.class));
		when(rsm.getResourceStatesForPath(anyString())).thenReturn(states);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), rsm, new OEntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource/2");
		ap.setUriInfo(uriInfo);
		
		Annotation[] annotations = null;
		MediaType mediaType = null;
		MultivaluedMap<String, String> headers = null;
		InputStream content = null;
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
		assertNotNull(result);
		assertEquals(null, result.getEntity());
	}
	
	@Test
	public void testReadEmptyContents() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(mock(CollectionResourceState.class));
		when(rsm.getResourceStatesForPath(anyString())).thenReturn(states);
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), rsm, new OEntityTransformer());
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/test/someresource/2");
		ap.setUriInfo(uriInfo);
		
		Annotation[] annotations = null;
		MediaType mediaType = null;
		MultivaluedMap<String, String> headers = null;
		InputStream content = new ByteArrayInputStream(new String("").getBytes());;
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
		assertNotNull(result);
		assertEquals(null, result.getEntity());
	}
	
	@Test
	public void testWriteCollectionResourceEntity_AtomXML() throws Exception {
		EdmEntitySet ees = createMockEdmEntitySet();
		EdmDataServices mockEDS = createMockFlightEdmDataServices();		
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees);
		
		Metadata mockMetadata = createMockFlightMetadata();
		CollectionResource<Entity> cr = createMockCollectionResourceEntity();
		
        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<CollectionResource<Entity>> ge = new GenericEntity<CollectionResource<Entity>>(cr) {};

		//Create provider
		MockAtomXMLProvider p = new MockAtomXMLProvider(mockEDS, mockMetadata);
		UriInfo uriInfo = mock(UriInfo.class);
		URI uri = new URI("http://localhost:8080/responder/rest/");
		when(uriInfo.getBaseUri()).thenReturn(uri);
		when(uriInfo.getPath()).thenReturn("Flight()");
		p.setUriInfo(uriInfo);

		//Serialize resource
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");

		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(readTextFile(FLIGHT_COLLECTION_XML), responseString);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
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

		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(readTextFile(FLIGHT_ENTRY_XML), responseString);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
	}

	@Test
	public void testWriteEntityResourceObject_AtomXML() throws Exception {
		EdmEntitySet ees = createMockEdmEntitySet();
		EdmDataServices mockEDS = createMockFlightEdmDataServices();		
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees);
		
		Metadata mockMetadata = createMockFlightMetadata();
		EntityResource<Flight> er = createMockEntityResourceObject();
		
        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<Flight>> ge = new GenericEntity<EntityResource<Flight>>(er) {};

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

		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(readTextFile(FLIGHT_ENTRY_SIMPLE_XML), responseString);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
	}
	
	@Test
	public void testSkipSelfIfEditExists() {
		/*
		 *  Is this in the OData spec?  It seems that if a 'self' and 'edit' link relation exists
		 *  then ODataExplorer barfs
		 */
		
		EdmDataServices edmDataServices = createMockEdmDataServices("FundsTransfers");
		Metadata metadata = createMockMetadata("MyModel");
		Transition transition = createMockTransition(
				createMockResourceState("FundsTransfers", "FundsTransfer", true), 
				createMockResourceState("fundstransfer", "FundsTransfer", false));

		
		AtomXMLProvider provider = new AtomXMLProvider(edmDataServices, metadata, mock(ResourceStateMachine.class), mock(Transformer.class));
		List<OLink> olinks = new ArrayList<OLink>();
		provider.addLinkToOLinks(olinks, new Link.Builder()
												.transition(transition)
												.title("title")
												.rel("self")
												.href("href")
												.build());
		assertEquals(1, olinks.size());
		
		// now add the 'edit' link, it should replace the 'self' link
		provider.addLinkToOLinks(olinks, new Link.Builder()
												.transition(transition)
												.title("title")
												.rel("edit")
												.href("href")
												.build());
		assertEquals(1, olinks.size());
		
	}

	@Test
	public void testSingleLinkEntryToCollection() {
		ResourceState account = new ResourceState("Account", "customerAccount", new ArrayList<Action>(), "/CustomerAccounts('{id}')");
		ResourceState fundsTransfers = new CollectionResourceState("FundsTransfer", "FundsTransfers", new ArrayList<Action>(), "/FundsTransfers");
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("DebitAcctNo", "{Acc}");
		account.addTransition(HttpMethod.GET, fundsTransfers, uriLinkageMap, "Debit funds transfers");
		
		AtomXMLProvider provider = new AtomXMLProvider(createMockEdmDataServices("FundsTransfers"), createMockMetadata("MyModel"), mock(ResourceStateMachine.class), mock(Transformer.class));
		List<OLink> olinks = new ArrayList<OLink>();
		Transition t = account.getTransition(fundsTransfers);
		provider.addLinkToOLinks(olinks, new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals(1, olinks.size());
		
		//Link relation should contain MS-DATA base uri + /related/ + navigation property. The nav. property in this case is the EntitySet name
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers", olinks.get(0).getRelation());
		
		assertEquals("Debit funds transfers", olinks.get(0).getTitle());
		
		assertEquals("/FundsTransfers()?$filter=DebitAcctNo eq '123'", olinks.get(0).getHref());
	}

	@Test
	public void testMultipleLinksEntryToCollection() {
		ResourceState account = new ResourceState("Account", "customerAccount", new ArrayList<Action>(), "/CustomerAccounts('{id}')");
		ResourceState fundsTransfers = new CollectionResourceState("FundsTransfer", "FundsTransfers", new ArrayList<Action>(), "/FundsTransfers");
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("DebitAcctNo", "{Acc}");
		account.addTransition(HttpMethod.GET, fundsTransfers, uriLinkageMap, "Debit funds transfers");
		uriLinkageMap.clear();
		uriLinkageMap.put("CreditAcctNo", "{Acc}");
		account.addTransition(HttpMethod.GET, fundsTransfers, uriLinkageMap, "Credit funds transfers");
		
		AtomXMLProvider provider = new AtomXMLProvider(createMockEdmDataServices("FundsTransfers"), createMockMetadata("MyModel"), mock(ResourceStateMachine.class), mock(Transformer.class));
		List<OLink> olinks = new ArrayList<OLink>();
		List<Transition> transitions = account.getTransitions(fundsTransfers);
		assertEquals(2, transitions.size());
		provider.addLinkToOLinks(olinks, new Link(transitions.get(0), transitions.get(0).getTarget().getRel(), transitions.get(0).getLabel().contains("Debit") ? "/FundsTransfers()?$filter=DebitAcctNo eq '123'" : "/FundsTransfers()?$filter=CreditAcctNo eq '123'", HttpMethod.GET));
		provider.addLinkToOLinks(olinks, new Link(transitions.get(1), transitions.get(1).getTarget().getRel(), transitions.get(1).getLabel().contains("Debit") ? "/FundsTransfers()?$filter=DebitAcctNo eq '123'" : "/FundsTransfers()?$filter=CreditAcctNo eq '123'", HttpMethod.GET));
		assertEquals(2, olinks.size());
		
		//Link relation should contain MS-DATA base uri + /related/ + navigation property. However, the nav. property in this case is NOT the EntitySet name
		//but a transition ID identifying the link (the link title at the moment). It does not fully comply with OData but this one does not cater for multiple links to the same target.  
		assertTrue(containsLink(olinks, "Debit funds transfers", "/FundsTransfers()?$filter=DebitAcctNo eq '123'", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers"));
		assertTrue(containsLink(olinks, "Credit funds transfers", "/FundsTransfers()?$filter=CreditAcctNo eq '123'", "http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers"));
	}

	@Test
	public void testSingleLinkFeedToCollectionSameEntity() throws Exception {
		// initialise the thread local request context with requestUri and baseUri
        RequestContext ctx = new RequestContext("http://localhost:8080/responder/rest", "/FundsTransfers", null);
        RequestContext.setRequestContext(ctx);

        //Create rsm
		ResourceState fundsTransfers = new CollectionResourceState("FundsTransfer", "FundsTransfers", new ArrayList<Action>(), "/FundsTransfers");
		ResourceState fundsTransfersIAuth = new CollectionResourceState("FundsTransfer", "FundsTransfersIAuth", new ArrayList<Action>(), "/FundsTransfersIAuth");
		fundsTransfers.addTransition(HttpMethod.GET, fundsTransfersIAuth, null, "Unauthorised input records");
		ResourceStateMachine rsm = new ResourceStateMachine(fundsTransfers);

		//Create collection resource
		CollectionResource<Entity> cr = new CollectionResource<Entity>("FundsTransfers", new ArrayList<EntityResource<Entity>>());
		List<Link> links = new ArrayList<Link>();
		links.add(rsm.createLinkToTarget(fundsTransfers.getTransition(fundsTransfersIAuth), null, null));
		cr.setLinks(links);
		GenericEntity<CollectionResource<Entity>> ge = new GenericEntity<CollectionResource<Entity>>(cr) {};

		//Create provider
		MockAtomXMLProvider p = new MockAtomXMLProvider(mock(EdmDataServices.class), mock(Metadata.class));
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getBaseUri()).thenReturn(new URI(ctx.getBasePath()));
		when(uriInfo.getPath()).thenReturn(ctx.getRequestUri());
		p.setUriInfo(uriInfo);

		//Serialize resource
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");
		System.out.println(responseString);

		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(readTextFile(EMPTY_FUNDS_TRANSFERS_FEED_XML), responseString);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
	}
	
	@Test
	public void testLinkEntryToEntity() {
		ResourceState account = new ResourceState("Account", "customerAccount", new ArrayList<Action>(), "/CustomerAccounts('{id}')");
		ResourceState currency = new ResourceState("Currency", "currency", new ArrayList<Action>(), "/Currencys('{id}')");
		account.addTransition(HttpMethod.GET, currency, new HashMap<String, String>(), "currency");
		
		AtomXMLProvider provider = new AtomXMLProvider(createMockEdmDataServices("Currencys"), createMockMetadata("MyModel"), mock(ResourceStateMachine.class), mock(Transformer.class));
		List<OLink> olinks = new ArrayList<OLink>();
		Transition t = account.getTransition(currency);
		provider.addLinkToOLinks(olinks, new Link(t, t.getTarget().getRel(), "/Currencys('USD')", HttpMethod.GET));
		assertEquals(1, olinks.size());
		
		//Link relation should contain MS-DATA base uri + /related/ + navigation property. The nav. property in this case is the entity (type) name
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Currency", olinks.get(0).getRelation());
		
		assertEquals("currency", olinks.get(0).getTitle());
		
		assertEquals("/Currencys('USD')", olinks.get(0).getHref());
	}

	@Test
	public void testSingleLinkToCollectionNotEntitySet() {
		ResourceState serviceRoot = new ResourceState("SD", "initial", new ArrayList<Action>(), "/");
		ResourceState fundsTransfer = new ResourceState("FundsTransfer", "fundsTransfer", new ArrayList<Action>(), "/FundsTransfers('{id}')");
		ResourceState fundsTransfersIAuth = new CollectionResourceState("Dummy", "FundsTransfersIAuth", new ArrayList<Action>(), "/FundsTransfersIAuth");
		serviceRoot.addTransition(fundsTransfer);
		fundsTransfer.addTransition(HttpMethod.GET, fundsTransfersIAuth, new HashMap<String, String>(), "Unauthorised funds transfers");
		
		//FundsTransfersIAuth is not an entity set
		EdmDataServices edmDataServices = createMockEdmDataServices("FundsTransfers");
		when(edmDataServices.getEdmEntitySet(any(EdmEntityType.class))).thenThrow(new NotFoundException("EntitySet for entity type Dummy has not been found"));
		
		AtomXMLProvider provider = new AtomXMLProvider(edmDataServices, createMockMetadata("MyModel"), mock(ResourceStateMachine.class), mock(Transformer.class));
		List<OLink> olinks = new ArrayList<OLink>();
		Transition t = fundsTransfer.getTransition(fundsTransfersIAuth);
		provider.addLinkToOLinks(olinks, new Link(t, t.getTarget().getRel(), "/FundsTransfersIAuth()", HttpMethod.GET));
		assertEquals(1, olinks.size());
		
		//Link relation should contain MS-DATA base uri + /related/ + navigation property. The nav. property in this case would be the EntitySet name, however, it is not
		//an entity set so use the name of the target state.
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfersIAuth", olinks.get(0).getRelation());
		
		assertEquals("Unauthorised funds transfers", olinks.get(0).getTitle());
		
		assertEquals("/FundsTransfersIAuth()", olinks.get(0).getHref());
	}
	
	@Test
	public void testSingleLinkOEntityFeedToNonEntitySet() throws Exception {
		// initialise the thread local request context with requestUri and baseUri
        RequestContext ctx = new RequestContext("http://localhost:8080/responder/rest", "/FundsTransfers", null);
        RequestContext.setRequestContext(ctx);

        //Create rsm
		ResourceState serviceRoot = new ResourceState("SD", "initial", new ArrayList<Action>(), "/");
		ResourceState fundsTransfers = new CollectionResourceState("FundsTransfer", "FundsTransfers", new ArrayList<Action>(), "/FundsTransfers");
		ResourceState fundsTransfersIAuth = new CollectionResourceState("FundsTransfer", "FundsTransfersIAuth", new ArrayList<Action>(), "/FundsTransfersIAuth");
		serviceRoot.addTransition(fundsTransfers);
		fundsTransfers.addTransition(HttpMethod.GET, fundsTransfersIAuth, null, "Unauthorised input records");
		ResourceStateMachine rsm = new ResourceStateMachine(fundsTransfers);

		//Create collection resource
		CollectionResource<OEntity> cr = new CollectionResource<OEntity>("FundsTransfers", new ArrayList<EntityResource<OEntity>>());
		List<Link> links = new ArrayList<Link>();
		links.add(rsm.createLinkToTarget(fundsTransfers.getTransition(fundsTransfersIAuth), null, null));
		cr.setLinks(links);
		GenericEntity<CollectionResource<OEntity>> ge = new GenericEntity<CollectionResource<OEntity>>(cr) {};

		//Create provider
		EdmDataServices edmDataServices = createMockEdmDataServices("FundsTransfers");
		Metadata metadata = mock(Metadata.class);
		when(metadata.getModelName()).thenReturn("MyModel");
		MockAtomXMLProvider p = new MockAtomXMLProvider(edmDataServices, metadata);
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getBaseUri()).thenReturn(new URI(ctx.getBasePath()));
		when(uriInfo.getPath()).thenReturn(ctx.getRequestUri());
		p.setUriInfo(uriInfo);

		//Serialize resource
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");
		System.out.println(responseString);

		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(readTextFile(EMPTY_FUNDS_TRANSFERS_FEED_XML), responseString);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
	}
	
	@Test
	public void testWriteEntityResourceGenericError_AtomXML() throws Exception {
		EdmEntitySet ees = createMockEdmEntitySet();
		EdmDataServices mockEDS = createMockFlightEdmDataServices();		
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees);
		
		Metadata mockMetadata = createMockFlightMetadata();
		EntityResource<GenericError> er = createMockEntityResourceGenericError();
		
        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<GenericError>> ge = new GenericEntity<EntityResource<GenericError>>(er) {};
		
		
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

		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(readTextFile(ATOM_GENERIC_ERROR_ENTRY_XML), responseString);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
	}
	
	@Test
	public void testWriteEntityResourceAcceptAtomSvc() throws Exception {
		EdmEntitySet ees = createMockEdmEntitySet();
		EdmDataServices mockEDS = createMockFlightEdmDataServices();		
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees);
		Metadata mockMetadata = createMockFlightMetadata();
		EntityResource<GenericError> er = createMockEntityResourceGenericError();
		GenericEntity<EntityResource<GenericError>> ge = new GenericEntity<EntityResource<GenericError>>(er) {};
		
		//Create provider
		MockAtomXMLProvider p = new MockAtomXMLProvider(mockEDS, mockMetadata);
		UriInfo uriInfo = mock(UriInfo.class);
		URI uri = new URI("http://localhost:8080/responder/rest/");
		when(uriInfo.getBaseUri()).thenReturn(uri);
		when(uriInfo.getPath()).thenReturn("Flight(123)");
		p.setUriInfo(uriInfo);

		//Set accept header to atomsvc+xml
		MultivaluedMap<String, Object> httpHeaders = new MultivaluedMapImpl<Object>();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, ExtendedMediaTypes.APPLICATION_ATOMSVC_XML_TYPE, httpHeaders, new ByteArrayOutputStream());

		//Make sure the response is atom+xml
		assertEquals(MediaType.APPLICATION_ATOM_XML, httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE));
	}
		
	@Test
	public void testWriteEntityResourceCustomError_AtomXML() throws Exception {
		EdmEntitySet ees = createMockEdmEntitySet();
		EdmDataServices mockEDS = createMockFlightEdmDataServices();		
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees);
		
		Metadata mockMetadata = createMockFlightMetadata();
		EntityResource<CustomError> er = CommandHelper.createEntityResource("CustomError", new CustomError("My custom error message."));
		
        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<CustomError>> ge = new GenericEntity<EntityResource<CustomError>>(er) {};
		
		
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

		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(readTextFile(ATOM_CUSTOM_ERROR_ENTRY_XML), responseString);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
	}
	
	@Test
	public void testLinkRelationCollectionToItem() {
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfers", "FundsTransfer", true), 
				createMockResourceState("customer", "Customer", false));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Customer", AtomXMLProvider.getODataLinkRelation(new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET), "FundsTransfers"));		
	}
	
	@Test
	public void testLinkRelationCollectionToItemSameEntity() {
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfers", "FundsTransfer", true), 
				createMockResourceState("fundsTransfer", "FundsTransfer", false));
		assertEquals("self", AtomXMLProvider.getODataLinkRelation(new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET), "FundsTransfers"));		
	}

	@Test
	public void testLinkRelationItemToCollection() {
		Transition t = createMockTransition(
				createMockResourceState("account", "Account", false), 
				createMockResourceState("FundsTransfers", "FundsTransfer", true));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers", AtomXMLProvider.getODataLinkRelation(new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET), "FundsTransfers"));		
	}

	@Test
	public void testLinkRelationItemToItem() {
		Transition t = createMockTransition(
				createMockResourceState("account", "Account", false), 
				createMockResourceState("FundsTransfers_new", "FundsTransfer", false));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfer", AtomXMLProvider.getODataLinkRelation(new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET), "FundsTransfers"));		
	}

	@Test
	public void testLinkFixedRelation() {
		ResourceState targetState = createMockResourceState("FundsTransfers_new", "FundsTransfer", true);
		when(targetState.getRel()).thenReturn("http://www.temenos.com/rels/new");
		Transition t = createMockTransition(
				createMockResourceState("account", "Account", false), 
				targetState);
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers http://www.temenos.com/rels/new", AtomXMLProvider.getODataLinkRelation(new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET), "FundsTransfers"));		
	}
	
	@Test
	public void testLinkRelationInitialCollectionToCollectionSameEntity() {
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfers", "FundsTransfer", true), 
				createMockResourceState("FundsTransfersIAuth", "FundsTransfer", true));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers", AtomXMLProvider.getODataLinkRelation(new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET), "FundsTransfers"));		
	}

	@Test
	public void testLinkRelationCollectionToCollectionSameEntity() {
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfersIAuth", "FundsTransfer", true), 
				createMockResourceState("FundsTransfersIHold", "FundsTransfer", true));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers", 
				AtomXMLProvider.getODataLinkRelation(new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET), "FundsTransfers"));		
	}

	private ResourceState createMockResourceState(String name, String entityName, boolean isCollection) {
		ResourceState state = mock(isCollection ? CollectionResourceState.class : ResourceState.class);
		when(state.getName()).thenReturn(name);
		when(state.getEntityName()).thenReturn(entityName);
		when(state.getRel()).thenReturn(isCollection ? "collection" : "item");
		return state; 
	}

	private Transition createMockTransition(ResourceState source, ResourceState target) {
		Transition.Builder builder = new Transition.Builder();
		builder.source(source);
		builder.target(target);
		TransitionCommandSpec command = mock(TransitionCommandSpec.class);
		when(command.getMethod()).thenReturn("GET");
		builder.command(command);
		return builder.build();
	}

	@SuppressWarnings("unchecked")
	private EntityResource<GenericError> createMockEntityResourceGenericError() {
		EntityResource<GenericError> er = mock(EntityResource.class);
				
		GenericError error = new GenericError("UPSTREAM_SERVER_UNAVAILABLE", "Failed to connect to resource manager.");
		when(er.getEntity()).thenReturn(error);
		when(er.getEntityName()).thenReturn("Flight");
		when(er.getGenericEntity()).thenReturn(new GenericEntity<EntityResource<GenericError>>(er, er.getClass().getGenericSuperclass()));
		return er;
	}	
	
	/*
	 * Read a text file
	 */
	private String readTextFile(String textFile) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(textFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String read;
		try {
			while((read = br.readLine()) != null) {
			    sb.append(read).append(System.getProperty("line.separator"));
			}
		}
		catch(IOException ioe) {
			fail(ioe.getMessage());
		}
		return sb.toString();		
	}
	
	private boolean containsLink(List<OLink> links, String title, String href, String relation) {
		assert(links != null);
		for (OLink l : links) {
			if (l.getHref().equals(href) && 
					l.getRelation().equals(relation) &&
					l.getTitle().equals(title)) {
				return true;
			}
		}
		
		for (OLink l : links) {
			System.out.println("[" + l.getTitle() + "]: rel=\"" + l.getRelation() + "\", href=\"" + l.getHref() + "\"");
		}
		return false;
	}
}
