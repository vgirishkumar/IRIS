package com.temenos.interaction.core.media.hal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.UriInfo;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;

import com.jayway.jaxrs.hateoas.HateoasLink;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.media.hal.HALProvider;
import com.temenos.interaction.core.media.hal.MediaType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

public class TestHALProvider {

	/*
	 * Test the getSize operation of GET with this provider
	 */
	@Test
	public void testSize() {
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		assertEquals(-1, hp.getSize(null, null, null, null, null));
	}

	/*
	 * Test the getSize operation of GET with this provider
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeserialise() throws IOException {
		EdmDataServices edmDS = mock(EdmDataServices.class);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("mockChild").setEntityType(mock(EdmEntityType.Builder.class));
		EdmEntitySet entitySet = ees.build();
		when(edmDS.getEdmEntitySet(anyString())).thenReturn(entitySet);
		HALProvider hp = new HALProvider(edmDS);
		
		String strEntityStream = "<resource><Child><name>noah</name><age>2</age></Child><links></links></resource>";
		InputStream entityStream = new ByteArrayInputStream(strEntityStream.getBytes());
		GenericEntity<EntityResource<OEntity>> ge = new GenericEntity<EntityResource<OEntity>>(new EntityResource<OEntity>()) {}; 
		EntityResource<OEntity> er = (EntityResource<OEntity>) hp.readFrom(RESTResource.class, ge.getType(), null, MediaType.APPLICATION_HAL_XML_TYPE, null, entityStream);
		assertNotNull(er.getEntity());
		OEntity entity = er.getEntity();
		assertEquals("mockChild", entity.getEntitySetName());
		assertNotNull(entity.getProperties());
		// string type
		assertEquals(EdmSimpleType.STRING, entity.getProperty("name").getType());
		assertEquals("noah", entity.getProperty("name").getValue());
		// int type
		// TODO handle non string entity properties
//		assertEquals(EdmSimpleType.INT32, entity.getProperty("age").getType());
//		assertEquals(2, entity.getProperty("age").getValue());
	}

	@Test(expected = WebApplicationException.class)
	public void testAttemptToSerialiseNonEntityResource() throws IOException {
		EntityResource<?> mdr = mock(EntityResource.class);

		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		hp.writeTo(mdr, MetaDataResource.class, null, null, MediaType.APPLICATION_HAL_XML_TYPE, null, new ByteArrayOutputStream());
	}
	
	@Test
	public void testSerialiseSimpleResource() throws Exception {
		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));

		OEntity entity = OEntities.create(createMockChildrenEntitySet(), entityKey, properties, new ArrayList<OLink>());
		EntityResource<OEntity> er = new EntityResource<OEntity>(entity);
		
		EdmDataServices edmDS = mock(EdmDataServices.class);
		when(edmDS.getEdmEntitySet(any(EdmEntityType.class))).thenReturn(createMockChildrenEntitySet());
		HALProvider hp = new HALProvider(edmDS);
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		hp.setUriInfo(mockUriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, OEntity.class, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource href=\"http://www.temenos.com/rest.svc/\"><name>noah</name><age>2</age></resource>";
		String responseString = createFlatXML(bos);
		
		Diff diff = new Diff(expectedXML, responseString);
		// don't worry about the order of the elements in the xml
		assertTrue(diff.similar());
	}

	@Test
	public void testSerialiseCollectionResource() throws Exception {
		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));

		Collection<EntityResource<OEntity>> entities = new ArrayList<EntityResource<OEntity>>();
		entities.add(new EntityResource<OEntity>(OEntities.create(createMockChildrenEntitySet(), entityKey, properties, new ArrayList<OLink>())));
		entities.add(new EntityResource<OEntity>(OEntities.create(createMockChildrenEntitySet(), entityKey, properties, new ArrayList<OLink>())));
		entities.add(new EntityResource<OEntity>(OEntities.create(createMockChildrenEntitySet(), entityKey, properties, new ArrayList<OLink>())));
		CollectionResource<OEntity> er = new CollectionResource<OEntity>("EntitySetName", entities);
		
		EdmDataServices edmDS = mock(EdmDataServices.class);
		when(edmDS.getEdmEntitySet(any(EdmEntityType.class))).thenReturn(createMockChildrenEntitySet());
		HALProvider hp = new HALProvider(edmDS);
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		hp.setUriInfo(mockUriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, CollectionResource.class, OEntity.class, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource href=\"http://www.temenos.com/rest.svc/\"><resource href=\"http://www.temenos.com/rest.svc/\" rel=\"Children collection self\"><age>2</age><name>noah</name></resource><resource href=\"http://www.temenos.com/rest.svc/\" rel=\"Children collection self\"><age>2</age><name>noah</name></resource><resource href=\"http://www.temenos.com/rest.svc/\" rel=\"Children collection self\"><age>2</age><name>noah</name></resource></resource>";
		String responseString = createFlatXML(bos);
		
		Diff diff = new Diff(expectedXML, responseString);
		// don't worry about the order of the elements in the xml
		assertTrue(diff.similar());
	}

	private String createFlatXML(ByteArrayOutputStream bos) throws Exception {
		String responseString = new String(bos.toByteArray(), "UTF-8");
		responseString = responseString.replaceAll(System.getProperty("line.separator"), "");
		responseString = responseString.replaceAll(">\\s+<", "><");
		return responseString;
	}
	
	private HateoasLink mockLink(String id, String rel, String href) {
		HateoasLink link = mock(HateoasLink.class);
		when(link.getId()).thenReturn(id);
		when(link.getRel()).thenReturn(rel);
		when(link.getHref()).thenReturn(href);
        return link;
	}
	
	@Test
	public void testSerialiseResourceNoEntity() throws Exception {
		EntityResource<?> er = mock(EntityResource.class);
		when(er.getEntity()).thenReturn(null);
		
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com"));
		hp.setUriInfo(mockUriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, null, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource href=\"http://www.temenos.com\"></resource>";
		String responseString = createFlatXML(bos);
		XMLAssert.assertXMLEqual(expectedXML, responseString);		
	}

	@Test
	public void testBaseUri() throws Exception {
		EntityResource<?> er = mock(EntityResource.class);
		when(er.getEntity()).thenReturn(null);
		
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		UriInfo mockUriInfo = mock(UriInfo.class);
		// java 1.6 bug getBaseUri returns absolute path
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		hp.setUriInfo(mockUriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, null, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource href=\"http://www.temenos.com/rest.svc/\"></resource>";
		String responseString = createFlatXML(bos);
		XMLAssert.assertXMLEqual(expectedXML, responseString);		
	}

	@Test
	public void testJSONMediaTypeWithCharset() throws Exception {
		EntityResource<?> er = mock(EntityResource.class);
		when(er.getEntity()).thenReturn(null);
		
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		UriInfo mockUriInfo = mock(UriInfo.class);
		// java 1.6 bug getBaseUri returns absolute path
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		hp.setUriInfo(mockUriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, null, null, javax.ws.rs.core.MediaType.valueOf("application/hal+json; charset=utf-8"), null, bos);

		String expectedXML = "{  \"_links\" : {    \"self\" : {      \"href\" : \"http://www.temenos.com/rest.svc/\"    }  }}";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		responseString = responseString.replaceAll(System.getProperty("line.separator"), "");
		assertEquals(expectedXML, responseString);
	}

	@Test
	public void testXMLMediaTypeWithCharset() throws Exception {
		EntityResource<?> er = mock(EntityResource.class);
		when(er.getEntity()).thenReturn(null);
		
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		UriInfo mockUriInfo = mock(UriInfo.class);
		// java 1.6 bug getBaseUri returns absolute path
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		hp.setUriInfo(mockUriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, null, null, javax.ws.rs.core.MediaType.valueOf("application/hal+xml; charset=utf-8"), null, bos);

		String expectedXML = "<resource href=\"http://www.temenos.com/rest.svc/\"></resource>";
		String responseString = createFlatXML(bos);
		XMLAssert.assertXMLEqual(expectedXML, responseString);		
	}

	@Test
	public void testSerialiseResourceWithLinks() throws Exception {
		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));
		// the test links
		List<HateoasLink> links = new ArrayList<HateoasLink>();
		links.add(mockLink("father", "_person", "humans/31"));
		links.add(mockLink("mother", "_person", "/rest.svc/humans/32"));
		
		OEntity entity = OEntities.create(createMockChildrenEntitySet(), entityKey, properties, new ArrayList<OLink>());
		EntityResource<OEntity> er = new EntityResource<OEntity>(entity);
		er.setLinks(links);
		
		EdmDataServices edmDS = mock(EdmDataServices.class);
		when(edmDS.getEdmEntitySet(any(EdmEntityType.class))).thenReturn(createMockChildrenEntitySet());
		HALProvider hp = new HALProvider(edmDS);
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		hp.setUriInfo(mockUriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, OEntity.class, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource href=\"http://www.temenos.com/rest.svc/\"><link href=\"humans/31\" rel=\"_person\" name=\"father\"/><link href=\"http://www.temenos.com/rest.svc/humans/32\" rel=\"_person\" name=\"mother\"/><name>noah</name><age>2</age></resource>";
		String responseString = createFlatXML(bos);
		
		Diff diff = new Diff(expectedXML, responseString);
		// don't worry about the order of the elements in the xml
		assertTrue(diff.similar());
	}

	@Test
	public void testSerialiseResourceWithRelatedLinks() throws Exception {
		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));
		// the test links
		/*
		 * Not sure, but I think relatedEntity and link are the same thing in an OEntity.
		 * However, a relatedEntity also has the relatedEntityInline capability.
		 * TODO add tests for 'inline' links
		 */
		List<HateoasLink> links = new ArrayList<HateoasLink>();
		links.add(mockLink("father", "_person", "humans/31"));
		links.add(mockLink("mother", "_person", "humans/32"));
		links.add(mockLink("siblings", "_family", "humans/phetheans"));
		
		OEntity entity = OEntities.create(createMockChildrenEntitySet(), entityKey, properties, new ArrayList<OLink>());
		EntityResource<OEntity> er = new EntityResource<OEntity>(entity);
		er.setLinks(links);
		
		EdmDataServices edmDS = mock(EdmDataServices.class);
		when(edmDS.getEdmEntitySet(any(EdmEntityType.class))).thenReturn(createMockChildrenEntitySet());
		HALProvider hp = new HALProvider(edmDS);
		UriInfo mockUriInfo = mock(UriInfo.class);
		when(mockUriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/rest.svc/"));
		hp.setUriInfo(mockUriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, OEntity.class, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource href=\"http://www.temenos.com/rest.svc/\"><link href=\"humans/phetheans\" rel=\"_family\" name=\"siblings\"/><link href=\"humans/31\" rel=\"_person\" name=\"father\"/><link href=\"humans/32\" rel=\"_person\" name=\"mother\"/><name>noah</name><age>2</age></resource>";
		String responseString = createFlatXML(bos);
		
		Diff diff = new Diff(expectedXML, responseString);
		// don't worry about the order of the elements in the xml
		assertTrue(diff.similar());
	}

	@Test
	public void testBuildMapFromOEntity() {
		EdmDataServices edmDS = mock(EdmDataServices.class);
		when(edmDS.getEdmEntitySet(any(EdmEntityType.class))).thenReturn(createMockChildrenEntitySet());
		HALProvider hp = new HALProvider(edmDS);
		
		/* 
		 * create an OEntity with more properties than the 
		 * supplied metadata.  The point is that we should only
		 * fill the map with the defined entities properties.
		 */
		OEntityKey entityKey = OEntityKey.create("123");
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));
		properties.add(OProperties.string("shoeSize", "5"));
		OEntity entity = OEntities.create(createMockChildrenEntitySet(), entityKey, properties, new ArrayList<OLink>());
		
		// map of property name to value object
		Map<String, Object> map = new HashMap<String, Object>();
		hp.buildFromOEntity(map, entity);
		
		assertEquals(2, map.keySet().size());
		assertTrue(map.keySet().contains("name"));
		assertTrue(map.keySet().contains("age"));
		assertEquals("noah", map.get("name"));
		assertEquals("2", map.get("age"));
	}
	
	@Test
	public void testSerialiseResourceWithForm() {
		// don't know how to deal with forms yet, possibly embed an xform
	}

	@Test
	public void testSerialiseStreamingResource() {
		// cannot decorate a streaming resource so should fail
	}
	
	private EdmEntitySet createMockChildrenEntitySet() {
		// mock a simple entity (Children entity set)
		List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
		eprops.add(EdmProperty.newBuilder("ID").setType(EdmSimpleType.STRING));
		eprops.add(EdmProperty.newBuilder("name").setType(EdmSimpleType.STRING));
		eprops.add(EdmProperty.newBuilder("age").setType(EdmSimpleType.STRING));
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Children").addKeys(Arrays.asList("ID")).addProperties(eprops);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Children").setEntityType(eet);
		return ees.build();
	}
}
