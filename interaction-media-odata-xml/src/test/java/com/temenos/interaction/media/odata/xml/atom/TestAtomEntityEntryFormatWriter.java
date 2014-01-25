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


import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriInfo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataParser;
import com.temenos.interaction.core.entity.vocabulary.Term;
import com.temenos.interaction.core.entity.vocabulary.TermFactory;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;

public class TestAtomEntityEntryFormatWriter {

	public final static String METADATA_XML_FILE = "TestMetadataParser.xml";
	private static Entity simpleEntity;
	private static Entity simpleEntityWithComplexTypes;
	private static Entity complexEntity;
	private static Entity complexEntity2;
	
	private static EntityMetadata entityMetadata;
	private static EntityMetadata complexEntityMetadata;
	private static EntityMetadata complexEntityMetadata2;
	private static String modelName;
			
	@BeforeClass
	public static void setup() {
		
		// Just adding as we do not want to add more metadata files
		//Read the metadata file
		TermFactory termFactory = new TermFactory() {
			public Term createTerm(String name, String value) throws Exception {
				if(name.equals("TEST_ENTITY_ALIAS")) {
					Term mockTerm = mock(Term.class);
					when(mockTerm.getValue()).thenReturn(value);
					when(mockTerm.getName()).thenReturn(name);
					return mockTerm;
				}
				else {
					return super.createTerm(name, value);
				}
			}			
		};
		
		// Initailise
		MetadataParser parser = new MetadataParser(termFactory);
		InputStream is = parser.getClass().getClassLoader().getResourceAsStream(METADATA_XML_FILE);
		Metadata metadata = parser.parse(is);
		Assert.assertNotNull(metadata);
	
		modelName = metadata.getModelName();
		
		// Simple Metadata and Entity
		entityMetadata = metadata.getEntityMetadata("Customer");
		simpleEntity = getSimpleEntity("Customer");
		simpleEntityWithComplexTypes = getComplexEntity("Customer");
		
		// Complex Metadata and Entity
		complexEntityMetadata = metadata.getEntityMetadata("CustomerWithTermList");
		complexEntity = getComplexEntity("CustomerWithTermList");
		
		// Second Complex Metadata and Entity
		complexEntityMetadata2 = metadata.getEntityMetadata("CustomerAllTermList");
		complexEntity2 = getComplexEntity("CustomerAllTermList");
	}
	
	@AfterClass
	public static void tearDown() {
		simpleEntity = null;
		complexEntity = null;
		entityMetadata = null;
		complexEntityMetadata = null;
	}
	
	@Test
	public void testWriteSimpleEntry() {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		try {
			when(uriInfo.getBaseUri()).thenReturn(new URI("http", "//www.temenos.com/iris/test", "simple"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		List<Link> links = new ArrayList<Link>();
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter();
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity, entityMetadata, links, new HashMap<Transition, RESTResource>(), modelName);
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		// We should not have List or infact any complex type representation here
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_address m:type=\"Bag(CustomerServiceTestModel.CustomerWithTermList_address)\">"));
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_street m:type=\"CustomerServiceTestModel.CustomerWithTermList_street\">"));
	}

	@Test
	public void testWriteSimpleEntryWithLink() {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		try {
			when(uriInfo.getBaseUri()).thenReturn(new URI("http", "//www.temenos.com/iris/test", "simple"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		List<Link> links = new ArrayList<Link>();
		ResourceState mockResourceState = mock(ResourceState.class);
		when(mockResourceState.getEntityName()).thenReturn("Entity");
		Transition mockTransition = mock(Transition.class);
		when(mockTransition.getLabel()).thenReturn("title");
		when(mockTransition.getTarget()).thenReturn(mockResourceState);
		links.add(new Link(mockTransition, "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Entity", "href", "GET"));
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter();
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity, entityMetadata, links, new HashMap<Transition, RESTResource>(), modelName);
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		// We should not have List or infact any complex type representation here
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_address m:type=\"Bag(CustomerServiceTestModel.CustomerWithTermList_address)\">"));
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_street m:type=\"CustomerServiceTestModel.CustomerWithTermList_street\">"));

		String relContent = extractLinkRelFromString(output);
		Assert.assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Entity", relContent);
	}

	@Test
	public void testWriteSimpleEntryWithEmbedded() {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		try {
			when(uriInfo.getBaseUri()).thenReturn(new URI("http", "//www.temenos.com/iris/test", "simple"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		List<Link> links = new ArrayList<Link>();
		ResourceState mockResourceState = mock(ResourceState.class);
		when(mockResourceState.getEntityName()).thenReturn("Entity");
		Transition mockTransition = mock(Transition.class);
		when(mockTransition.getLabel()).thenReturn("title");
		when(mockTransition.getTarget()).thenReturn(mockResourceState);
		links.add(new Link(mockTransition, "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Entity", "href", "GET"));
		
		Map<Transition, RESTResource> embeddedResources = new HashMap<Transition, RESTResource>();
		embeddedResources.put(mockTransition, new EntityResource<Entity>(simpleEntity));
		
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter();
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity, entityMetadata, links, embeddedResources, modelName);
		
		String output = strWriter.toString();
		System.out.println(strWriter);
		
		String relContent = extractLinkRelFromString(output);
		Assert.assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Entity", relContent);
		Assert.assertTrue(output.contains("<m:inline>"));
	}

	private String extractLinkRelFromString(String in) {
		String result = null;
		Pattern pattern = Pattern.compile("rel=\"(.*?)\"");
		Matcher matcher = pattern.matcher(in);
		if (matcher.find()) {
			result = matcher.group(1);
		}
		return result;
	}
	
	@Test
	public void testWriteSimpleEntryWithComplexType() {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		try {
			when(uriInfo.getBaseUri()).thenReturn(new URI("http", "//www.temenos.com/iris/test", "simpleWithComplexType"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		List<Link> links = new ArrayList<Link>();
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter();
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntityWithComplexTypes, entityMetadata, links, new HashMap<Transition, RESTResource>(), modelName);
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		// We should not have List or infact any complex type representation here
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_address m:type=\"CustomerServiceTestModel.CustomerWithTermList_address\">"));
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_street m:type=\"CustomerServiceTestModel.CustomerWithTermList_street\">"));
	}
	
	@Test
	public void testWriteComplexEntry() {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		try {
			when(uriInfo.getBaseUri()).thenReturn(new URI("http", "//www.temenos.com/iris/test", "complex"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		List<Link> links = new ArrayList<Link>();
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter();
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, complexEntity, complexEntityMetadata, links, new HashMap<Transition, RESTResource>(), modelName);
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		// Lets check if we have represented the Entry successfully 
		Assert.assertTrue(output.contains("<d:CustomerWithTermList_address m:type=\"Bag(CustomerServiceTestModel.CustomerWithTermList_address)\">"));
		Assert.assertTrue(output.contains("<d:CustomerWithTermList_street m:type=\"CustomerServiceTestModel.CustomerWithTermList_street\">"));
	}
	
	@Test
	public void testWriteComplexEntryWithAllList() {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		try {
			when(uriInfo.getBaseUri()).thenReturn(new URI("http", "//www.temenos.com/iris/test", "complex2"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		List<Link> links = new ArrayList<Link>();
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter();
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, complexEntity2, complexEntityMetadata2, links, new HashMap<Transition, RESTResource>(), modelName);
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		// Lets check if we have represented the Entry successfully 
		Assert.assertTrue(output.contains("<d:CustomerAllTermList_address m:type=\"Bag(CustomerServiceTestModel.CustomerAllTermList_address)\">"));
		Assert.assertTrue(output.contains("<d:CustomerAllTermList_street m:type=\"Bag(CustomerServiceTestModel.CustomerAllTermList_street)\">"));
	}
		
	private static Entity getSimpleEntity(String entityName) {
		EntityProperties props = new EntityProperties();
		props.setProperty(new EntityProperty("name", "SomeName"));
		props.setProperty(new EntityProperty("dateOfBirth", new Date()));
		props.setProperty(new EntityProperty("sector", "Finance"));
		props.setProperty(new EntityProperty("industry", "Banking"));
		props.setProperty(new EntityProperty("loyal", "true"));
		props.setProperty(new EntityProperty("loyalty_rating", 10));
		return new Entity(entityName, props);
	}
	
	private static Entity getComplexEntity(String entityName) {
		EntityProperties props = new EntityProperties();
		props.setProperty(new EntityProperty("name", "SomeName"));
		
		// Addressess
		List<EntityProperties> addGroup = new ArrayList<EntityProperties>();
		
		// Address 1
		EntityProperties addGroup1 = new EntityProperties();
		addGroup1.setProperty(new EntityProperty("number", 2));
		List<EntityProperties> add1StreetGroup = new ArrayList<EntityProperties>();
		EntityProperties addGroup1Street1 = new EntityProperties();
		addGroup1Street1.setProperty(new EntityProperty("streetType", "Peoples Building"));
		add1StreetGroup.add(addGroup1Street1);
		EntityProperties addGroup1Street2 = new EntityProperties();
		addGroup1Street2.setProperty(new EntityProperty("streetType", "Mayland's Avenue"));
		add1StreetGroup.add(addGroup1Street2);
		addGroup1.setProperty(new EntityProperty("street", add1StreetGroup));
		addGroup1.setProperty(new EntityProperty("town", "Hemel Hempstead"));
		addGroup1.setProperty(new EntityProperty("postCode", "HP2 4NW"));
		addGroup.add(addGroup1);
		
		// Address2
		EntityProperties addGroup2 = new EntityProperties();
		addGroup2.setProperty(new EntityProperty("number", 2));
		List<EntityProperties> add2StreetGroup = new ArrayList<EntityProperties>();
		EntityProperties addGroup2Street1 = new EntityProperties();
		addGroup2Street1.setProperty(new EntityProperty("streetType", "Mayland's Avenue"));
		add2StreetGroup.add(addGroup2Street1);
		addGroup2.setProperty(new EntityProperty("street", add2StreetGroup));
		addGroup2.setProperty(new EntityProperty("town", "Hemel Hempstead"));
		addGroup2.setProperty(new EntityProperty("postCode", "HP2 4NW"));
		addGroup.add(addGroup2);
		
		props.setProperty(new EntityProperty("address", addGroup));
		
		props.setProperty(new EntityProperty("dateOfBirth",  new Date()));
		props.setProperty(new EntityProperty("sector", "Finance"));
		props.setProperty(new EntityProperty("industry", "Banking"));
		props.setProperty(new EntityProperty("loyal", "true"));
		props.setProperty(new EntityProperty("loyalty_rating", 10));
		return new Entity(entityName, props);
	}
}
