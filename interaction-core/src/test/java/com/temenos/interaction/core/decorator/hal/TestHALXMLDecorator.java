package com.temenos.interaction.core.decorator.hal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.junit.Test;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmType;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.MetaDataResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.decorator.hal.HALXMLDecorator;

import org.custommonkey.xmlunit.XMLAssert;
public class TestHALXMLDecorator {

	@Test(expected = WebApplicationException.class)
	public void testAttemptToDecorateNonEntityResponse() {
		RESTResponse restResponse = mock(RESTResponse.class);
		MetaDataResource mdr = mock(MetaDataResource.class);
		when(restResponse.getResource()).thenReturn(mdr);

		HALXMLDecorator halDecorator = new HALXMLDecorator();
		halDecorator.decorateRESTResponse(restResponse);
	}
	
	@Test
	public void testSimpleResource() throws Exception {
		RESTResponse restResponse = mock(RESTResponse.class);
		EntityResource er = mock(EntityResource.class);
		when(restResponse.getStatus()).thenReturn(Response.Status.OK);
		when(restResponse.getResource()).thenReturn(er);
		
		// mock a simple entity (Children entity set)
		List<EdmProperty> edmProperties = new ArrayList<EdmProperty>();
		edmProperties.add(new EdmProperty("ID",
				EdmType.STRING, false, null, null, null,
				null, null, null, null, null, null));
		EdmEntityType childrenEntityType = new EdmEntityType("InteractionTest", null,
				"Children", null, Arrays.asList("ID"), edmProperties, null);
		EdmEntitySet childrenEntitySet = new EdmEntitySet("Children", childrenEntityType);
		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));

		OEntity entity = OEntities.create(childrenEntitySet, entityKey, properties, new ArrayList<OLink>());
		when(er.getEntity()).thenReturn(entity);
		
		HALXMLDecorator halDecorator = new HALXMLDecorator();
//		String expectedXML = "<resource><Children><name>noah</name><age>2</age></Children><links></links></resource>";
		String expectedXML = "<resource><name>noah</name><age>2</age><links></links></resource>";
		StreamingOutput response = halDecorator.decorateRESTResponse(restResponse);
		assertNotNull(response);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		response.write(bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	@Test
	public void testResourceWithLinks() throws Exception {
		RESTResponse restResponse = mock(RESTResponse.class);
		EntityResource er = mock(EntityResource.class);
		when(restResponse.getStatus()).thenReturn(Response.Status.OK);
		when(restResponse.getResource()).thenReturn(er);
		
		// mock a simple entity (Children entity set)
		List<EdmProperty> edmProperties = new ArrayList<EdmProperty>();
		edmProperties.add(new EdmProperty("ID",
				EdmType.STRING, false, null, null, null,
				null, null, null, null, null, null));
		EdmEntityType childrenEntityType = new EdmEntityType("InteractionTest", null,
				"Children", null, Arrays.asList("ID"), edmProperties, null);
		EdmEntitySet childrenEntitySet = new EdmEntitySet("Children", childrenEntityType);
		// the test key
		OEntityKey entityKey = OEntityKey.create("123");
		// the test properties
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "noah"));
		properties.add(OProperties.string("age", "2"));
		// the test links
		List<OLink> links = new ArrayList<OLink>();
		links.add(OLinks.link("_person", "father", "/humans/31"));
		links.add(OLinks.link("_person", "mother", "/humans/32"));
		
		OEntity entity = OEntities.create(childrenEntitySet, entityKey, properties, links);
		when(er.getEntity()).thenReturn(entity);
		
		HALXMLDecorator halDecorator = new HALXMLDecorator();
//		String expectedXML = "<resource><Children><name>noah</name><age>2</age></Children><links><link href=\"/humans/31\" rel=\"_person\" title=\"father\"/><link href=\"/humans/32\" rel=\"_person\" title=\"mother\"/></links></resource>";
		String expectedXML = "<resource><name>noah</name><age>2</age><links><link href=\"/humans/31\" rel=\"_person\" title=\"father\"/><link href=\"/humans/32\" rel=\"_person\" title=\"mother\"/></links></resource>";
		StreamingOutput response = halDecorator.decorateRESTResponse(restResponse);
		assertNotNull(response);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		response.write(bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);		
	}

	@Test
	public void testResourceWithRelatedLinks() throws Exception {
		RESTResponse restResponse = mock(RESTResponse.class);
		EntityResource er = mock(EntityResource.class);
		when(restResponse.getStatus()).thenReturn(Response.Status.OK);
		when(restResponse.getResource()).thenReturn(er);
		
		// mock a simple entity (Children entity set)
		List<EdmProperty> edmProperties = new ArrayList<EdmProperty>();
		edmProperties.add(new EdmProperty("ID",
				EdmType.STRING, false, null, null, null,
				null, null, null, null, null, null));
		EdmEntityType childrenEntityType = new EdmEntityType("InteractionTest", null,
				"Children", null, Arrays.asList("ID"), edmProperties, null);
		EdmEntitySet childrenEntitySet = new EdmEntitySet("Children", childrenEntityType);
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
		
		OEntity entity = OEntities.create(childrenEntitySet, entityKey, properties, links);
		when(er.getEntity()).thenReturn(entity);
		
		HALXMLDecorator halDecorator = new HALXMLDecorator();
//		String expectedXML = "<resource><Children><name>noah</name><age>2</age></Children><links><link href=\"/humans/31\" rel=\"_person\" title=\"father\"/><link href=\"/humans/32\" rel=\"_person\" title=\"mother\"/></links></resource>";
		String expectedXML = "<resource><name>noah</name><age>2</age><links><link href=\"/humans/31\" rel=\"_person\" title=\"father\"/><link href=\"/humans/32\" rel=\"_person\" title=\"mother\"/></links></resource>";
		StreamingOutput response = halDecorator.decorateRESTResponse(restResponse);
		assertNotNull(response);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		response.write(bos);
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);		

	}

	
	@Test
	public void testResourceWithForm() {
		
	}

	@Test
	public void testStreamingResource() {
		// cannot decorate a streaming resource so should fail
	}
	
}
