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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.Entry;
import org.odata4j.format.xml.AtomEntryFormatParserExt;
import org.odata4j.internal.FeedCustomizationMapping;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.commands.odata.OEntityTransformer;
import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataParser;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.odataext.entity.MetadataOData4j;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OEntityKey.class, AtomXMLProvider.class})
public class TestAtomXMLProviderPowerMock {

	@Test
	public void testReadPath() throws Exception {
		// enable mock of the static class (see also verifyStatic)
		mockStatic(OEntityKey.class);
		
		EdmDataServices edmDataServices = createAirlineEdmMetadata();//mock(EdmDataServices.class);
		Metadata metadata = createAirlineMetadata();
		ResourceStateMachine rsm = new ResourceStateMachine(
				new ResourceState("Flight", "SomeResource", new ArrayList<Action>(), "/test/someresource/{id}"));

		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(edmDataServices, metadata, rsm, new OEntityTransformer());
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		when(mockUriInfo.getPath()).thenReturn("/test/someresource/2");
		MultivaluedMap<String, String> mockPathParameters = new MultivaluedMapImpl<String>();
		mockPathParameters.add("id", "2");
		when(mockUriInfo.getPathParameters()).thenReturn(mockPathParameters);
		ap.setUriInfo(mockUriInfo);
		Request requestContext = mock(Request.class);
		when(requestContext.getMethod()).thenReturn("GET");
		ap.setRequestContext(requestContext);
		
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, new ByteArrayInputStream(new String("Antyhing").getBytes()));
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());

		// verify entityset and key
		verifyNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), eq("Flight"), any(OEntityKey.class), any(FeedCustomizationMapping.class));

		// verify static with entity key "2"
		verifyStatic();
		OEntityKey.parse("2");
	}

	/*
	 * The create (POST) will need to parse the OEntity without a key supplied in the path
	 */
	@Test
	public void testReadPathNoEntityKey() throws Exception {
		EdmDataServices edmDataServices = createAirlineEdmMetadata();//mock(EdmDataServices.class);
		Metadata metadata = createAirlineMetadata();
		ResourceStateMachine rsm = new ResourceStateMachine(
				new ResourceState("Flight", "SomeResource", new ArrayList<Action>(), "/test/someresource"));

		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(edmDataServices, metadata, rsm, new OEntityTransformer());
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		when(mockUriInfo.getPath()).thenReturn("/test/someresource");
		ap.setUriInfo(mockUriInfo);
		Request requestContext = mock(Request.class);
		when(requestContext.getMethod()).thenReturn("GET");
		ap.setRequestContext(requestContext);
		
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, new ByteArrayInputStream(new String("Antyhing").getBytes()));
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());
		
		verifyNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), eq("Flight"), any(OEntityKey.class), any(FeedCustomizationMapping.class));
	}

	@Test
	public void testReadPath404() throws Exception {
		EdmDataServices metadata = mock(EdmDataServices.class);
		ResourceStateMachine rsm = new ResourceStateMachine(
				new ResourceState("SomeResource", "initial", new ArrayList<Action>(), "/test/someresource"));

		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, mock(Metadata.class), rsm, new OEntityTransformer());
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		// mock not finding any resources
		when(mockUriInfo.getPath()).thenReturn("/test/someotherresource");
		ap.setUriInfo(mockUriInfo);
		Request requestContext = mock(Request.class);
		when(requestContext.getMethod()).thenReturn("GET");
		ap.setRequestContext(requestContext);
		
		int status = -1;
		try {
			ap.readFrom(RESTResource.class, ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, new ByteArrayInputStream(new byte[0]));
		} catch (WebApplicationException wae) {
			status = wae.getResponse().getStatus();
		}
		assertEquals(400, status);
	}

	@Test
	public void testReadEntityResourceOData() throws Exception {
		EdmDataServices edmDataServices = createAirlineEdmMetadata();//mock(EdmDataServices.class);
		Metadata metadata = createAirlineMetadata();
		ResourceStateMachine rsm = new ResourceStateMachine(
				new ResourceState("Flight", "SomeResource", new ArrayList<Action>(), "/test('{id}')"));

		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(edmDataServices, metadata, rsm, new OEntityTransformer());
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		when(mockUriInfo.getPath()).thenReturn("/test('2')");
		MultivaluedMap<String, String> mockPathParameters = new MultivaluedMapImpl<String>();
		mockPathParameters.add("id", "2");
		when(mockUriInfo.getPathParameters()).thenReturn(mockPathParameters);
		ap.setUriInfo(mockUriInfo);
		Request requestContext = mock(Request.class);
		when(requestContext.getMethod()).thenReturn("GET");
		ap.setRequestContext(requestContext);
		
		Annotation[] annotations = null;
		MediaType mediaType = MediaType.APPLICATION_ATOM_XML_TYPE;
		MultivaluedMap<String, String> headers = null;
		InputStream content = new ByteArrayInputStream(new String("Antyhing").getBytes());
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());

		// verify parse was called
		verify(mockParser).parse(any(Reader.class));
		// verify correct entity name resolved
		verifyNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), eq("Flight"), any(OEntityKey.class), any(FeedCustomizationMapping.class));
	}
	
	@Test
	public void testReadCollectionResourceOData() throws Exception {
		// enable mock of the static class (see also verifyStatic)
		mockStatic(OEntityKey.class);
		
		EdmDataServices metadata = createAirlineEdmMetadata();//mock(EdmDataServices.class);
		ResourceStateMachine rsm = new ResourceStateMachine(
				new ResourceState("Flight", "SomeResource", new ArrayList<Action>(), "/test()"));

		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, createAirlineMetadata(), rsm, new OEntityTransformer());
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		when(mockUriInfo.getPath()).thenReturn("/test()");
		ap.setUriInfo(mockUriInfo);
		Request requestContext = mock(Request.class);
		when(requestContext.getMethod()).thenReturn("GET");
		ap.setRequestContext(requestContext);
		
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, new ByteArrayInputStream(new String("Antyhing").getBytes()));
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());

		// verify entityset and key
		verifyNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), eq("Flight"), any(OEntityKey.class), any(FeedCustomizationMapping.class));
	}

	@Test
	public void testReadCollectionResourceWithoutBracketsOData() throws Exception {
		// enable mock of the static class (see also verifyStatic)
		mockStatic(OEntityKey.class);
		
		EdmDataServices metadata = createAirlineEdmMetadata();//mock(EdmDataServices.class);
		ResourceState initial = new CollectionResourceState("Flight", "SomeResources", new ArrayList<Action>(), "/test()");
		initial.addTransition("GET", new CollectionResourceState("SomeOtherResource", "nav", new ArrayList<Action>(), "/test()/1"));
		initial.addTransition("GET", new CollectionResourceState("SomeOtherResource", "nav", new ArrayList<Action>(), "/test()/2"));
		initial.addTransition("GET", new CollectionResourceState("SomeOtherResource", "nav", new ArrayList<Action>(), "/test()/3"));
		initial.addTransition("GET", new CollectionResourceState("SomeOtherResource", "nav", new ArrayList<Action>(), "/test()/4"));
		initial.addTransition("GET", new CollectionResourceState("SomeOtherResource", "nav", new ArrayList<Action>(), "/test()/5"));
		initial.addTransition("GET", new CollectionResourceState("SomeOtherResource", "nav", new ArrayList<Action>(), "/test()/navproperty"));
		ResourceStateMachine rsm = new ResourceStateMachine(initial);

		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		// don't do anything when trying to read context
		AtomEntryFormatParserExt mockParser = mock(AtomEntryFormatParserExt.class);
		Entry mockEntry = mock(Entry.class);
		OEntity mockOEntity = mock(OEntity.class);
		when(mockEntry.getEntity()).thenReturn(mockOEntity);
		when(mockParser.parse(any(Reader.class))).thenReturn(mockEntry);
		whenNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), anyString(), any(OEntityKey.class), any(FeedCustomizationMapping.class)).thenReturn(mockParser);
		
		AtomXMLProvider ap = new AtomXMLProvider(metadata, createAirlineMetadata(), rsm, new OEntityTransformer());
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		// An odata request for the colleciton might arrive without the brackets
		when(mockUriInfo.getPath()).thenReturn("/test");
		ap.setUriInfo(mockUriInfo);
		Request requestContext = mock(Request.class);
		when(requestContext.getMethod()).thenReturn("GET");
		ap.setRequestContext(requestContext);
		
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), null, MediaType.APPLICATION_ATOM_XML_TYPE, null, new ByteArrayInputStream(new String("Antyhing").getBytes()));
		assertNotNull(result);
		assertEquals(mockOEntity, result.getEntity());

		// verify entityset and key
		verifyNew(AtomEntryFormatParserExt.class).withArguments(any(EdmDataServices.class), eq("Flight"), any(OEntityKey.class), any(FeedCustomizationMapping.class));
	}

	@Test
	public void testReadNullContent() throws Exception {
		EdmDataServices edmDataServices = createAirlineEdmMetadata();//mock(EdmDataServices.class);
		Metadata metadata = createAirlineMetadata();
		ResourceStateMachine rsm = new ResourceStateMachine(
				new ResourceState("Flight", "initial", new ArrayList<Action>(), "/test/someresource"));
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		
		AtomXMLProvider ap = new AtomXMLProvider(edmDataServices, metadata, rsm, new OEntityTransformer());
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		when(mockUriInfo.getPath()).thenReturn("/test/someresource");
		ap.setUriInfo(mockUriInfo);
		Request requestContext = mock(Request.class);
		when(requestContext.getMethod()).thenReturn("GET");
		ap.setRequestContext(requestContext);
		
		Annotation[] annotations = null;
		MediaType mediaType = MediaType.APPLICATION_ATOM_XML_TYPE;
		MultivaluedMap<String, String> headers = null;
		InputStream content = null;
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
		assertNotNull(result);
		assertEquals(null, result.getEntity());
	}
	
	@Test
	public void testReadEmptyContents() throws Exception {
		EdmDataServices edmDataServices = createAirlineEdmMetadata();//mock(EdmDataServices.class);
		Metadata metadata = createAirlineMetadata();
		ResourceStateMachine rsm = new ResourceStateMachine(
				new ResourceState("Flight", "initial", new ArrayList<Action>(), "/test/someresource"));
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>(null)) {};
		
		AtomXMLProvider ap = new AtomXMLProvider(edmDataServices, metadata, rsm, new OEntityTransformer());
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		when(mockUriInfo.getPath()).thenReturn("/test/someresource");
		ap.setUriInfo(mockUriInfo);
		Request requestContext = mock(Request.class);
		when(requestContext.getMethod()).thenReturn("GET");
		ap.setRequestContext(requestContext);
		
		Annotation[] annotations = null;
		MediaType mediaType = MediaType.APPLICATION_ATOM_XML_TYPE;
		MultivaluedMap<String, String> headers = null;
		InputStream content = new ByteArrayInputStream(new String("").getBytes());;
		EntityResource<OEntity> result = ap.readFrom(RESTResource.class, ge.getType(), annotations, mediaType, headers, content);
		assertNotNull(result);
		assertEquals(null, result.getEntity());
	}
	
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
	
}
