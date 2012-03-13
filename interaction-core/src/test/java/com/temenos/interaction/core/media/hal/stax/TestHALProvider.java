package com.temenos.interaction.core.media.hal.stax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.media.hal.MediaType;
import com.temenos.interaction.core.media.hal.stax.HALProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

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
		assertNotNull(er.getOEntity());
		OEntity entity = er.getOEntity();
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
		EntityResource<?> er = mock(EntityResource.class);
		
		// mock a simple entity (Children entity set)
		List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("ID").setType(EdmSimpleType.STRING);
		eprops.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Children").addKeys(Arrays.asList("ID")).addProperties(eprops);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Children").setEntityType(eet);

		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));

		OEntity entity = OEntities.create(ees.build(), entityKey, properties, new ArrayList<OLink>());
		when(er.getOEntity()).thenReturn(entity);
		
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, null, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource><Children><name>noah</name><age>2</age></Children><links></links></resource>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	@Test
	public void testSerialiseResourceNoEntity() throws Exception {
		EntityResource<?> er = mock(EntityResource.class);
		when(er.getOEntity()).thenReturn(null);
		
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, null, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource></resource>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);		
	}

	@Test
	public void testSerialiseResourceWithLinks() throws Exception {
		EntityResource<?> er = mock(EntityResource.class);

		// mock a simple entity (Children entity set)
		List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("ID").setType(EdmSimpleType.STRING);
		eprops.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Children").addKeys(Arrays.asList("ID")).addProperties(eprops);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Children").setEntityType(eet);

		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));
		// the test links
		List<OLink> links = new ArrayList<OLink>();
		links.add(OLinks.relatedEntity("_person", "father", "/humans/31"));
		links.add(OLinks.relatedEntity("_person", "mother", "/humans/32"));
		
		OEntity entity = OEntities.create(ees.build(), entityKey, properties, links);
		when(er.getOEntity()).thenReturn(entity);
		
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, null, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource><Children><name>noah</name><age>2</age></Children><links><link href=\"/humans/31\" rel=\"_person\" title=\"father\"/><link href=\"/humans/32\" rel=\"_person\" title=\"mother\"/></links></resource>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	@Test
	public void testSerialiseResourceWithRelatedLinks() throws Exception {
		EntityResource<?> er = mock(EntityResource.class);
		
		// mock a simple entity (Children entity set)
		List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("ID").setType(EdmSimpleType.STRING);
		eprops.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Children").addKeys(Arrays.asList("ID")).addProperties(eprops);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Children").setEntityType(eet);

		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));
		// the test links
		/*
		 * Not sure, but I think relatedEntity and link are the same thing.
		 * However, a relatedEntity also has the relatedEntityInline capability.
		 */
		List<OLink> links = new ArrayList<OLink>();
		links.add(OLinks.relatedEntity("_person", "father", "/humans/31"));
		links.add(OLinks.relatedEntity("_person", "mother", "/humans/32"));
		OLinks.relatedEntities("_family", "siblings", "/humans/phetheans");
		
		OEntity entity = OEntities.create(ees.build(), entityKey, properties, links);
		when(er.getOEntity()).thenReturn(entity);
		
		HALProvider hp = new HALProvider(mock(EdmDataServices.class));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		hp.writeTo(er, EntityResource.class, null, null, MediaType.APPLICATION_HAL_XML_TYPE, null, bos);

		String expectedXML = "<resource><Children><name>noah</name><age>2</age></Children><links><link href=\"/humans/31\" rel=\"_person\" title=\"father\"/><link href=\"/humans/32\" rel=\"_person\" title=\"mother\"/></links></resource>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);		
	}

	
	@Test
	public void testSerialiseResourceWithForm() {
		// don't know how to deal with forms yet, possibly embed an xform
	}

	@Test
	public void testSerialiseStreamingResource() {
		// cannot decorate a streaming resource so should fail
	}
	
}
