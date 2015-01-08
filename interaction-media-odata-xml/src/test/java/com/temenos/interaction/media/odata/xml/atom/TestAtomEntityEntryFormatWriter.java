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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriInfo;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.temenos.interaction.core.command.CommandHelper;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.Term;
import com.temenos.interaction.core.entity.vocabulary.TermFactory;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ResourceMetadataManager;
import com.temenos.interaction.media.odata.xml.IgnoreNamedElementsXMLDifferenceListener;

public class TestAtomEntityEntryFormatWriter {

	public final static String METADATA_XML_FILE = "TestMetadataParser.xml";
	public final static String METADATA_CUSTOMER = "Customer";
	public final static String METADATA_CUSTOMER_ALL_TERM = "CustomerAllTermList";
	public final static String METADATA_CUSTOMER_WITH_TERM = "CustomerWithTermList";
	private static final String FIELD_METADTATA = "T24FieldMetadata";
	private static Entity simpleEntity;
	private static Entity simpleEmptyEntity;
	private static Entity simpleEmptyDOBEntity;
	private static Entity simpleEntityWithComplexTypes;
	private static Entity complexEntity;
	private static Entity complexEntity2;
	
	private static ResourceState serviceDocument;
	private static Metadata metadata;
			
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
		
		// Initialise
		ResourceMetadataManager rm = new ResourceMetadataManager(null, termFactory);
		serviceDocument = mock(ResourceState.class);
		//MetadataParser parser = new MetadataParser(termFactory);
		//InputStream is = parser.getClass().getClassLoader().getResourceAsStream(METADATA_XML_FILE);
		//metadata = parser.parse(is);
		//metadata.setResourceMetadataManager(rm);
		
		metadata = new Metadata(rm);
		metadata.setModelName("CustomerServiceTest");
		metadata.getEntityMetadata(METADATA_CUSTOMER);
		metadata.getEntityMetadata(METADATA_CUSTOMER_ALL_TERM);
		metadata.getEntityMetadata(METADATA_CUSTOMER_WITH_TERM);
		metadata.getEntityMetadata(FIELD_METADTATA);
		
		Assert.assertNotNull(metadata);
	
		// Simple Metadata and Entity
		simpleEntity = getSimpleEntity("Customer");
		simpleEmptyEntity = getSimpleEmptyEntity("Customer");
		simpleEmptyDOBEntity = getSimpleEmptyEntity("CustomerNonManDateOfBirth");
		simpleEntityWithComplexTypes = getComplexEntity("Customer");
		
		// Complex Metadata and Entity
		complexEntity = getComplexEntity("CustomerWithTermList");
		
		// Second Complex Metadata and Entity
		complexEntity2 = getComplexEntity("CustomerAllTermList");
	}
	
	@AfterClass
	public static void tearDown() {
		simpleEntity = null;
		complexEntity = null;
		complexEntity2 = null;
	}
	
	private final static String SIMPLE_ENTRY_OUTPUT = "<?xml version='1.0' encoding='UTF-8'?>" +
				"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xml:base=\"http://www.temenos.com/iris/service/\">" +
				"  <id>http://www.temenos.com/iris/service/simple('NAME')</id>" +
				"  <title type=\"text\"></title>" +
				"  <updated>2014-02-25T09:15:50Z</updated>" +
				"  <author>" +
				"    <name></name>" +
				"  </author>" +
				"  <category term=\"CustomerServiceTestModel.Customer\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\">" +
					"  </category>" +
					"  <content type=\"application/xml\">" +
					"    <m:properties>" +
					"      <d:loyal m:type=\"Edm.Boolean\">true</d:loyal>" +
					"      <d:sector>Finance</d:sector>" +
					"      <d:dateOfBirth m:type=\"Edm.DateTime\">2014-02-25T09:15:50</d:dateOfBirth>" +
					"      <d:name>SomeName</d:name>" +
					"      <d:loyalty_rating m:type=\"Edm.Double\">10</d:loyalty_rating>" +
					"      <d:industry>Banking</d:industry>" +
					"    </m:properties>" +
					"  </content>" +
					"</entry>";
	
	@Test
	public void testWriteSimpleEntry() throws SAXException, IOException, URISyntaxException {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/iris/service/"));
		when(uriInfo.getPath()).thenReturn("simple('NAME')");
		
		List<Link> links = new ArrayList<Link>();
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity.getName(), simpleEntity, links, new HashMap<Transition, RESTResource>());
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(SIMPLE_ENTRY_OUTPUT, output);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated", "d:dateOfBirth"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
		
		// We should not have List or infact any complex type representation here
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_address m:type=\"Bag(CustomerServiceTestModel.CustomerWithTermList_address)\">"));
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_street m:type=\"CustomerServiceTestModel.CustomerWithTermList_street\">"));
	}

	private final static String SIMPLE_ENTRY_COMPANY_OUTPUT = "<?xml version='1.0' encoding='UTF-8'?>" +
			"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xml:base=\"http://www.temenos.com/iris/service/123/\">" +
			"  <id>http://www.temenos.com/iris/service/123/simple('NAME')</id>" +
			"  <title type=\"text\"></title>" +
			"  <updated>2014-02-25T09:15:50Z</updated>" +
			"  <author>" +
			"    <name></name>" +
			"  </author>" +
			"  <category term=\"CustomerServiceTestModel.Customer\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\">" +
				"  </category>" +
				"  <content type=\"application/xml\">" +
				"    <m:properties>" +
				"      <d:loyal m:type=\"Edm.Boolean\">true</d:loyal>" +
				"      <d:sector>Finance</d:sector>" +
				"      <d:dateOfBirth m:type=\"Edm.DateTime\">2014-02-25T09:15:50</d:dateOfBirth>" +
				"      <d:name>SomeName</d:name>" +
				"      <d:loyalty_rating m:type=\"Edm.Double\">10</d:loyalty_rating>" +
				"      <d:industry>Banking</d:industry>" +
				"    </m:properties>" +
				"  </content>" +
				"</entry>";

	@Test
	public void testWriteSimpleEntryCompany() throws SAXException, IOException, URISyntaxException {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/iris/service/"));
		when(uriInfo.getPath()).thenReturn("123/simple('NAME')");
		
		List<Link> links = new ArrayList<Link>();
				
		// service document with company context
		ResourceState initial = new ResourceState("ServiceDocument", "ServiceDocument", new ArrayList<Action>(), "/{companyid}");
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(initial, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity.getName(), simpleEntity, links, new HashMap<Transition, RESTResource>());
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(SIMPLE_ENTRY_COMPANY_OUTPUT, output);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated", "d:dateOfBirth"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
	}

	@Test
	public void testWriteSimpleEntryWithLink() throws URISyntaxException {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/iris/test/"));
		when(uriInfo.getPath()).thenReturn("simple");

		List<Link> links = new ArrayList<Link>();
		ResourceState mockResourceState = mock(ResourceState.class);
		when(mockResourceState.getEntityName()).thenReturn("Entity");
		Transition mockTransition = mock(Transition.class);
		when(mockTransition.getLabel()).thenReturn("title");
		when(mockTransition.getTarget()).thenReturn(mockResourceState);
		links.add(new Link(mockTransition, "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Entity", "href", "GET"));
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity.getName(), simpleEntity, links, new HashMap<Transition, RESTResource>());
		
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
		EntityResource<Entity> embeddedEntityResource = new EntityResource<Entity>(simpleEntity);
		embeddedEntityResource.setEntityName(simpleEntity.getName());
		embeddedResources.put(mockTransition, embeddedEntityResource);
		
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity.getName(), simpleEntity, links, embeddedResources);
		
		String output = strWriter.toString();
		System.out.println(strWriter);
		
		String relContent = extractLinkRelFromString(output);
		Assert.assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Entity", relContent);
		Assert.assertTrue(output.contains("<m:inline>"));
	}

	@Test
	public void testWriteNullEntityWithEmbedded() {
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
		EntityResource<Entity> embeddedEntityResource = new EntityResource<Entity>(null);
		embeddedEntityResource.setEntityName("Customer");
		embeddedResources.put(mockTransition, embeddedEntityResource);
		
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity.getName(), simpleEntity, links, embeddedResources);
		
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
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntityWithComplexTypes.getName(), simpleEntityWithComplexTypes, links, new HashMap<Transition, RESTResource>());
		
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
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, complexEntity.getName(), complexEntity, links, new HashMap<Transition, RESTResource>());
		
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
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, complexEntity2.getName(), complexEntity2, links, new HashMap<Transition, RESTResource>());
		
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
	
	private final static String SIMPLE_EMPTY_ENTRY_OUTPUT = "<?xml version='1.0' encoding='UTF-8'?>" +
			"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xml:base=\"http://www.temenos.com/iris/service/\">" +
			"  <id>http://www.temenos.com/iris/service/simple('NAME')</id>" +
			"  <title type=\"text\"></title>" +
			"  <updated>2014-02-25T09:15:50Z</updated>" +
			"  <author>" +
			"    <name></name>" +
			"  </author>" +
			"  <category term=\"CustomerServiceTestModel.Customer\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\">" +
				"  </category>" +
				"  <content type=\"application/xml\">" +
				"    <m:properties>" +
				"      <d:loyal m:type=\"Edm.Boolean\" m:null=\"true\"></d:loyal>" +
				"      <d:sector></d:sector>" +
				"      <d:dateOfBirth m:type=\"Edm.DateTime\">2014-03-17T23:01:58</d:dateOfBirth>" +
				"      <d:name></d:name>" +
				"      <d:loyalty_rating m:type=\"Edm.Double\" m:null=\"true\"></d:loyalty_rating>" +
				"      <d:industry></d:industry>" +
				"    </m:properties>" +
				"  </content>" +
				"</entry>";
	
	@Test
	public void testWriteSimpleEmptyEntry() throws Exception {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/iris/service/"));
		when(uriInfo.getPath()).thenReturn("simple('NAME')");
		
		List<Link> links = new ArrayList<Link>();
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEmptyEntity.getName(), simpleEmptyEntity, links, new HashMap<Transition, RESTResource>());
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(SIMPLE_EMPTY_ENTRY_OUTPUT, output);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated", "d:dateOfBirth"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
		
		// We should not have List or infact any complex type representation here
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_address m:type=\"Bag(CustomerServiceTestModel.CustomerWithTermList_address)\">"));
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_street m:type=\"CustomerServiceTestModel.CustomerWithTermList_street\">"));
	}
	
	private final static String SIMPLE_EMPTY_ENTRY_WITH_DOB_NONMAN_OUTPUT = "<?xml version='1.0' encoding='UTF-8'?>" +
			"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xml:base=\"http://www.temenos.com/iris/service/\">" +
			"  <id>http://www.temenos.com/iris/service/simple('NAME')</id>" +
			"  <title type=\"text\"></title>" +
			"  <updated>2014-02-25T09:15:50Z</updated>" +
			"  <author>" +
			"    <name></name>" +
			"  </author>" +
			"  <category term=\"CustomerServiceTestModel.CustomerNonManDateOfBirth\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\">" +
				"  </category>" +
				"  <content type=\"application/xml\">" +
				"    <m:properties>" +
				"      <d:loyal m:type=\"Edm.Boolean\" m:null=\"true\"></d:loyal>" +
				"      <d:sector></d:sector>" +
				"      <d:dateOfBirth m:type=\"Edm.DateTime\" m:null=\"true\"></d:dateOfBirth>" +
				"      <d:name></d:name>" +
				"      <d:loyalty_rating m:type=\"Edm.Double\" m:null=\"true\"></d:loyalty_rating>" +
				"      <d:industry></d:industry>" +
				"    </m:properties>" +
				"  </content>" +
				"</entry>";
	
	@Test
	public void testWriteSimpleEmptyDOBEntry() throws Exception {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/iris/service/"));
		when(uriInfo.getPath()).thenReturn("simple('NAME')");
		
		List<Link> links = new ArrayList<Link>();
				
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEmptyDOBEntity.getName(), simpleEmptyDOBEntity, links, new HashMap<Transition, RESTResource>());
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		//Check response
		XMLUnit.setIgnoreWhitespace(true);
		Diff myDiff = XMLUnit.compareXML(SIMPLE_EMPTY_ENTRY_WITH_DOB_NONMAN_OUTPUT, output);
	    myDiff.overrideDifferenceListener(new IgnoreNamedElementsXMLDifferenceListener("updated"));
	    if(!myDiff.similar()) {
	    	fail(myDiff.toString());
	    }
		
		// We should not have List or infact any complex type representation here
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_address m:type=\"Bag(CustomerServiceTestModel.CustomerWithTermList_address)\">"));
		Assert.assertFalse(output.contains("<d:CustomerWithTermList_street m:type=\"CustomerServiceTestModel.CustomerWithTermList_street\">"));
	}

	@Test
	public void testLinkId() throws URISyntaxException {
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getBaseUri()).thenReturn(new URI("http://www.temenos.com/iris/test/"));
		when(uriInfo.getPath()).thenReturn("simple");
		List<Link> links = new ArrayList<Link>();
		ResourceState mockResourceState = mock(ResourceState.class);
		when(mockResourceState.getEntityName()).thenReturn("Entity");
		Transition mockTransition = mock(Transition.class);
		when(mockTransition.getLabel()).thenReturn("title");
		when(mockTransition.getLinkId()).thenReturn("123456789");
		when(mockTransition.getTarget()).thenReturn(mockResourceState);
		links.add(new Link(mockTransition, "http://schemas.microsoft.com/ado/2007/08/dataservices/related/Entity", "href", "GET"));
					
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, simpleEntity.getName(), simpleEntity, links, new HashMap<Transition, RESTResource>());
		
		String output = strWriter.toString();
		
		Assert.assertFalse(output.contains("id=123456789"));
	}
	
	@Test
	public void testComplexEntityWithInlineMetadata() {
		// Get UriInfo and Links
		UriInfo uriInfo = mock(UriInfo.class);
		try {
			when(uriInfo.getBaseUri()).thenReturn(new URI("http", "//www.temenos.com/iris/test", "simple"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		// EmbeddedResources
		Map<Transition, RESTResource> embeddedResources = new HashMap<Transition, RESTResource>();
		
		// Self link
		List<Link> links = new ArrayList<Link>();
		ResourceState mockResourceState = mock(ResourceState.class);
		when(mockResourceState.getEntityName()).thenReturn("CustomerAllTermList");
		Transition mockTransition = mock(Transition.class);
		when(mockTransition.getLabel()).thenReturn("title");
		when(mockTransition.getTarget()).thenReturn(mockResourceState);
		links.add(new Link(mockTransition, "self", "href", "POST"));
		
		// Inline metadata
		ResourceState mockInlineResourceState = mock(ResourceState.class);
		when(mockInlineResourceState.getEntityName()).thenReturn(FIELD_METADTATA);
		Transition mockInlineMetadataTransition = mock(Transition.class);
		when(mockInlineMetadataTransition.getLabel()).thenReturn("inline metadata");
		when(mockInlineMetadataTransition.getTarget()).thenReturn(mockInlineResourceState);
		links.add(new Link(mockInlineMetadataTransition, "inline metaedata", "href", "POST"));
		
		CollectionResource<Entity> embeddedEntityResource = getFieldMetadata();
		embeddedEntityResource.setEntityName(embeddedEntityResource.getEntityName());
		embeddedResources.put(mockInlineMetadataTransition, embeddedEntityResource);
		
		AtomEntityEntryFormatWriter writer = new AtomEntityEntryFormatWriter(serviceDocument, metadata);
		StringWriter strWriter = new StringWriter();
		writer.write(uriInfo, strWriter, complexEntity2.getName(), complexEntity2, links, embeddedResources);
		
		String output = strWriter.toString();
		//System.out.println(strWriter);
		
		String relContent = extractLinkRelFromString(output);
		Assert.assertEquals("self", relContent);
		Assert.assertTrue(output.contains("<link href=\"href\" rel=\"inline metaedata\" type=\"application/atom+xml;type=entry\" title=\"inline metadata\">"));
		Assert.assertTrue(output.contains("<m:inline>"));
		Assert.assertTrue(output.contains("<feed>"));
		Assert.assertTrue(output.contains("<d:FieldValue>industry-1000</d:FieldValue>"));
		Assert.assertTrue(output.contains("<d:Enrichment>industry-1000-Enrichment</d:Enrichment>"));
		Assert.assertTrue(output.contains("<d:FieldValue>industry-1001</d:FieldValue>"));
		Assert.assertTrue(output.contains("<d:Enrichment>industry-1001-Enrichment</d:Enrichment>"));
		Assert.assertTrue(output.contains("<d:FieldValue>sector-1000</d:FieldValue>"));
		Assert.assertTrue(output.contains("<d:Enrichment>sector-1000-Enrichment</d:Enrichment>"));
		Assert.assertTrue(output.contains("<d:FieldValue>sector-1001</d:FieldValue>"));
		Assert.assertTrue(output.contains("<d:Enrichment>sector-1001-Enrichment</d:Enrichment>"));
		Assert.assertTrue(output.contains("<d:streetType>Mayland's Avenue</d:streetType>"));
	}
	
	private CollectionResource<Entity> getFieldMetadata() {
		String entityName = "CustomerAllTermList";
		List<EntityResource<Entity>> entityResources = new ArrayList<EntityResource<Entity>>();
		
		// Metadat 1
		Entity entity1 = createT24FieldMetadata(entityName, "industry");
		entityResources.add(CommandHelper.createEntityResource(entity1));
	
		// Metadat 2
		Entity entity2 = createT24FieldMetadata(entityName, "sector");
		entityResources.add(CommandHelper.createEntityResource(entity2));
		
		return new CollectionResource<Entity>(FIELD_METADTATA, entityResources) {};
	}
	
	private Entity createT24FieldMetadata(String entityName, String fieldName) {
		String fullyQualifiedName = entityName + "." + fieldName;
		
		EntityProperties entityProperties = new EntityProperties();
		entityProperties.setProperty(new EntityProperty("Id", fullyQualifiedName));
		entityProperties.setProperty(new EntityProperty("Entity", entityName));
		entityProperties.setProperty(new EntityProperty("Property", fieldName));
		entityProperties.setProperty(new EntityProperty("Visible", true));
		entityProperties.setProperty(new EntityProperty("Mandatory", false));
		entityProperties.setProperty(new EntityProperty("MandatorySelection", false));
		entityProperties.setProperty(new EntityProperty("ReadOnly", false));
		entityProperties.setProperty(new EntityProperty("Copy", true));
		entityProperties.setProperty(new EntityProperty("PrimaryKey", false));
		
		List<EntityProperties> possibleValues = new ArrayList<EntityProperties>();
		entityProperties.setProperty(new EntityProperty("PossibleValuesMvGroup", possibleValues));
		
		// Now List of Enrichments
		List<EntityProperties> enrichments = new ArrayList<EntityProperties>();
			EntityProperties enrichmentValueElem = new EntityProperties();
			enrichmentValueElem.setProperty(new EntityProperty("FieldNumber", "1"));
			enrichmentValueElem.setProperty(new EntityProperty("FieldValue", fieldName + "-1000"));
			enrichmentValueElem.setProperty(new EntityProperty("Enrichment", fieldName + "-1000-Enrichment"));
			enrichments.add(enrichmentValueElem);
			
			EntityProperties enrichmentValueElem2 = new EntityProperties();
			enrichmentValueElem2.setProperty(new EntityProperty("FieldNumber", "2"));
			enrichmentValueElem2.setProperty(new EntityProperty("FieldValue", fieldName + "-1001"));
			enrichmentValueElem2.setProperty(new EntityProperty("Enrichment", fieldName + "-1001-Enrichment"));
			enrichments.add(enrichmentValueElem2);
		
		entityProperties.setProperty(new EntityProperty("EnrichmentsMvGroup", enrichments));
				
		return new Entity(FIELD_METADTATA, entityProperties);
	}
	
	private static Entity getSimpleEmptyEntity(String entityName) {
		EntityProperties entityProperties = new EntityProperties();
		EntityMetadata entityMetadata = metadata.getEntityMetadata(entityName);
		for(String propertyName : entityMetadata.getTopLevelProperties()) {
			Vocabulary vocab = entityMetadata.getPropertyVocabulary(propertyName);
			// Only Simple Properties
			if(!entityMetadata.isPropertyComplex(propertyName) && 
				vocab.getTerm(TermComplexGroup.TERM_NAME) == null) {
				entityProperties.setProperty(entityMetadata.createEmptyEntityProperty(propertyName));
			}
		}		
		return new Entity(entityName, entityProperties);
	}
}
