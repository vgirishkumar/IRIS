package com.temenos.interaction.core.link;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.odata4j.core.OLink;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEventReader2;

import com.jayway.jaxrs.hateoas.LinkableInfo;
import com.temenos.interaction.core.dynaresource.HTTPDynaRIM;
import com.temenos.interaction.core.state.ResourceInteractionModel;

public class TestResourceRegistry {

	@Test
	public void testNoResources() {
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPDynaRIM>());
		Set<ResourceInteractionModel> set = rr.getResourceInteractionModels();
		assertNotNull(set);
		assertEquals(0, set.size());
	}

	private HTTPDynaRIM createMockHTTPDynaRIM(String id) {
		HTTPDynaRIM rim1 = mock(HTTPDynaRIM.class);
		ResourceState rs = mock(ResourceState.class);
		when(rs.getId()).thenReturn(id);
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		when(rsm.getInitial()).thenReturn(rs);
		when(rim1.getStateMachine()).thenReturn(rsm);
		when(rim1.getCurrentState()).thenReturn(rs);
		return rim1;
	}
	
	@Test
	public void testConstructedWithResourceSet() {
		HashSet<HTTPDynaRIM> resourceSet = new HashSet<HTTPDynaRIM>();
		resourceSet.add(createMockHTTPDynaRIM("a"));
		HTTPDynaRIM rim2 = createMockHTTPDynaRIM("b");
		when(rim2.getFQResourcePath()).thenReturn("rim2");
		resourceSet.add(rim2);
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), resourceSet);
		Set<ResourceInteractionModel> set = rr.getResourceInteractionModels();
		assertNotNull(set);
		assertEquals(2, set.size());
	}

	@Test
	public void testConstructedWithRootResource() {
		HTTPDynaRIM parent = createMockHTTPDynaRIM("parent");
		when(parent.getFQResourcePath()).thenReturn("parent");
		HTTPDynaRIM child = createMockHTTPDynaRIM("child");
		when(child.getFQResourcePath()).thenReturn("child");

		HashSet<ResourceInteractionModel> resourceSet = new HashSet<ResourceInteractionModel>();
		resourceSet.add(child);
		when(parent.getChildren()).thenReturn(resourceSet);

		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), parent);
		Set<ResourceInteractionModel> set = rr.getResourceInteractionModels();
		assertNotNull(set);
		assertEquals(2, set.size());
	}

	@Test
	public void testConstructedWithRootCircularResource() {
		HTTPDynaRIM parent = createMockHTTPDynaRIM("parent");
		when(parent.getFQResourcePath()).thenReturn("parent");
		HTTPDynaRIM child = createMockHTTPDynaRIM("child");
		when(child.getFQResourcePath()).thenReturn("child");

		HashSet<ResourceInteractionModel> parentResourceSet = new HashSet<ResourceInteractionModel>();
		parentResourceSet.add(child);
		when(parent.getChildren()).thenReturn(parentResourceSet);

		HashSet<ResourceInteractionModel> childResourceSet = new HashSet<ResourceInteractionModel>();
		childResourceSet.add(parent);
		when(child.getChildren()).thenReturn(childResourceSet);

		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), parent);
		Set<ResourceInteractionModel> set = rr.getResourceInteractionModels();
		assertNotNull(set);
		assertEquals(2, set.size());
	}

	@Test
	public void testEntityResourcePath() {
		/*
		 * Creating a resource registry with a resource that has neither a state machine, nor
		 * an initial state should populate the entity resource map with a path to the entity
		 */
		String ENTITY_NAME = "TEST_ENTITY";
		HashSet<HTTPDynaRIM> resourceSet = new HashSet<HTTPDynaRIM>();
		HTTPDynaRIM testResource = createMockHTTPDynaRIM("test");
		when(testResource.getCurrentState().getEntityName()).thenReturn(ENTITY_NAME);
		when(testResource.getFQResourcePath()).thenReturn("/blah/test");
		resourceSet.add(testResource);
		
		ResourceRegistry registry = new ResourceRegistry(mock(EdmDataServices.class), resourceSet);
		assertEquals("/blah/test", registry.getEntityResourcePath(ENTITY_NAME));
	}

	@Test
	public void testNavigationLinks() {
		InputStream in = ClassLoader.getSystemResourceAsStream("com/temenos/interaction/core/link/TestResourceRegistryEDMX.xml");
		XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(in)));
		EdmxFormatParser formatParser = new EdmxFormatParser();
		EdmDataServices ds = formatParser.parseMetadata(reader);
		assertNotNull(ds.findEdmEntityType("AirlineModel.Flight"));

		// Flight has a navigation property to FlightSchedule
		ResourceRegistry rr = new ResourceRegistry(ds, new HashSet<HTTPDynaRIM>());
		// give the FlightSchedule resource a path
		HTTPDynaRIM rim = createMockHTTPDynaRIM("test");
		when(rim.getCurrentState().getEntityName()).thenReturn("FlightSchedule");
		when(rim.getFQResourcePath()).thenReturn("/FS/{id}");
		// register the resource
		rr.add(rim);

		// get the links for the Flight entity
		EdmEntitySet entitySet = ds.findEdmEntitySet("Flight");
		List<OLink> entityLinks = rr.getNavigationLinks(entitySet.getType());
		assertEquals(1, entityLinks.size());
		assertEquals("/FS/{id}", entityLinks.get(0).getHref());
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FlightSchedule", entityLinks.get(0).getRelation());
		assertEquals("FlightSchedule", entityLinks.get(0).getTitle());
		
	}

	@Test (expected = AssertionError.class)
	public void testNotImplementedMapClass() {
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPDynaRIM>());
		// this method is used to map the Linkable annotation, we don't use that
		rr.mapClass(String.class);
	}

	@Test (expected = AssertionError.class)
	public void testLinkableNotRegistered() {
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPDynaRIM>());
		rr.getLinkableInfo("test");
	}

	@Test
	public void testLinkableSelf() {
		String ENTITY_NAME = "TEST_ENTITY";
		HashSet<HTTPDynaRIM> resourceSet = new HashSet<HTTPDynaRIM>();
		HTTPDynaRIM testResource = createMockHTTPDynaRIM("test");
		when(testResource.getCurrentState().getEntityName()).thenReturn(ENTITY_NAME);
		when(testResource.getFQResourcePath()).thenReturn("/blah/test");
		resourceSet.add(testResource);
		
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), resourceSet);
		LinkableInfo link = rr.getLinkableInfo(ENTITY_NAME);
		assertNotNull(link);
		assertEquals("TEST_ENTITY", link.getId());
		assertEquals("GET", link.getHttpMethod());
		assertEquals("/blah/test", link.getMethodPath());
        assertEquals("lookup label from EDMX", link.getLabel());
        assertEquals("lookup description from EDMX", link.getDescription());
        assertNull(link.getConsumes());	
        assertNull(link.getProduces());	
        assertNull(link.getTemplateClass());	
    }

	@Test
	public void testLinkableStateTransition() {
		String ENTITY_NAME = "TEST_ENTITY";
		
		HashSet<HTTPDynaRIM> resourceSet = new HashSet<HTTPDynaRIM>();
		HTTPDynaRIM testResource = mock(HTTPDynaRIM.class);
		when(testResource.getFQResourcePath()).thenReturn("/blah/test");

		// create a little CRUD state machine
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "/blah/test");
		ResourceState exists = new ResourceState(initial, "exists");
		ResourceState deleted = new ResourceState(initial, "deleted");
		// update
		exists.addTransition("PUT", exists);
		// delete
		exists.addTransition("DELETE", deleted);
		
		when(testResource.getStateMachine()).thenReturn(new ResourceStateMachine(exists));
		when(testResource.getCurrentState()).thenReturn(exists);
		resourceSet.add(testResource);
		
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), resourceSet);
		// link to self
		LinkableInfo link = rr.getLinkableInfo("TEST_ENTITY");
		assertNotNull(link);
		assertEquals("TEST_ENTITY", link.getId());
		assertEquals("GET", link.getHttpMethod());
		assertEquals("/blah/test", link.getMethodPath());

		// link to update
		LinkableInfo updateLink = rr.getLinkableInfo("TEST_ENTITY.exists>TEST_ENTITY.exists");
		assertNotNull(updateLink);
		assertEquals("TEST_ENTITY.exists>TEST_ENTITY.exists", updateLink.getId());
		assertEquals("PUT", updateLink.getHttpMethod());
		assertEquals("/blah/test", updateLink.getMethodPath());

		// link to delete
		LinkableInfo deleteLink = rr.getLinkableInfo("TEST_ENTITY.exists>TEST_ENTITY.deleted");
		assertNotNull(deleteLink);
		assertEquals("TEST_ENTITY.exists>TEST_ENTITY.deleted", deleteLink.getId());
		assertEquals("DELETE", deleteLink.getHttpMethod());
		assertEquals("/blah/test", deleteLink.getMethodPath());
		
	}

}
