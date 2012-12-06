package com.temenos.interaction.winkext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wink.common.DynamicResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.odata4j.edm.EdmDataServices;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.core.hypermedia.ResourceRegistry;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.rim.HTTPHypermediaRIM;
import com.temenos.interaction.core.rim.HTTPResourceInteractionModel;
import com.temenos.interaction.core.rim.ResourceInteractionModel;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistrarWithSingletons.class})
public class TestRegistrarWithSingletons {

	private HTTPResourceInteractionModel createMockHTTPRIM(String entityName, String path) {
		HTTPHypermediaRIM rim = mock(HTTPHypermediaRIM.class);
		ResourceState rs = mock(ResourceState.class);
		when(rs.getName()).thenReturn("");
		when(rs.getEntityName()).thenReturn(entityName);
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		when(rsm.getInitial()).thenReturn(rs);
		when(rim.getCurrentState()).thenReturn(rs);
		when(rim.getResourcePath()).thenReturn(path);
		when(rim.getFQResourcePath()).thenCallRealMethod();
		return rim;
	}

	@Test
	public void testSimpleServiceRoot() {
		HTTPResourceInteractionModel serviceRoot = createMockHTTPRIM("notes", "/notes");
		RegistrarWithSingletons rs = new RegistrarWithSingletons();
		rs.setServiceRoot(serviceRoot);
		assertNotNull(rs.getInstances());
		assertEquals(1, rs.getInstances().size());
	}

	@Test
	public void testSimpleServiceRoots() {
		Set<HTTPResourceInteractionModel> serviceRoots = new HashSet<HTTPResourceInteractionModel>();
		serviceRoots.add(createMockHTTPRIM("notes", "/"));
		serviceRoots.add(createMockHTTPRIM("metadata", "/$metadata"));
		RegistrarWithSingletons rs = new RegistrarWithSingletons();
		rs.setServiceRoots(serviceRoots);
		assertNotNull(rs.getInstances());
		assertEquals(2, rs.getInstances().size());
	}

	@Test
	public void testHierarchyServiceRoot() throws Exception {
		HTTPResourceInteractionModel serviceRoot = createMockHTTPRIM("notes", "/notes");
		List<ResourceInteractionModel> children = new ArrayList<ResourceInteractionModel>();
		children.add(createMockHTTPRIM("draftNote", "/draft/{id}"));
		children.add(createMockHTTPRIM("note", "/{id}"));
		when(serviceRoot.getChildren()).thenReturn(children);
		
		whenNew(DynamicResourceDelegate.class).withParameterTypes(HTTPResourceInteractionModel.class, HTTPResourceInteractionModel.class).withArguments(any(DynamicResource.class), any(ResourceInteractionModel.class)).thenAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) throws Throwable {
					return new DynamicResourceDelegate((HTTPResourceInteractionModel) invocation.getArguments()[0], (HTTPResourceInteractionModel) invocation.getArguments()[1]);
				}
		});
		RegistrarWithSingletons rs = new RegistrarWithSingletons();
		rs.setServiceRoot(serviceRoot);
		assertNotNull(rs.getInstances());
		assertEquals(3, rs.getInstances().size());
		
		// should have the same parent
		DynamicResourceDelegate parent = null;
		for (Object obj : rs.getInstances()) {
			DynamicResourceDelegate rd = (DynamicResourceDelegate) obj;
			if (rd.getParent() != null && parent == null)
				parent = (DynamicResourceDelegate) rd.getParent();
			else
				assertEquals(parent, rd.getParent());
		}
		
		// verify resource delegate created 3 times
		verifyNew(DynamicResourceDelegate.class, times(3)).withArguments(any(HTTPResourceInteractionModel.class), any(HTTPResourceInteractionModel.class));
	}

	@Test
	public void testHeirarchy() throws Exception {
		// mock a few resources with a simple hierarchy in the resource registry
		ResourceRegistry rRegistry = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPResourceInteractionModel>());
		HTTPResourceInteractionModel r1 = createMockHTTPRIM("notes", "/notes");
		rRegistry.add(r1);
		// child 1
		HTTPResourceInteractionModel cr1 = createMockHTTPRIM("draftNote", "/draft/{id}");
		rRegistry.add(cr1);
		// child 2
		HTTPResourceInteractionModel cr2 = createMockHTTPRIM("note", "/{id}");
		rRegistry.add(cr2);
	
		whenNew(DynamicResourceDelegate.class).withParameterTypes(HTTPResourceInteractionModel.class, HTTPResourceInteractionModel.class).withArguments(any(DynamicResource.class), any(ResourceInteractionModel.class)).thenAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) throws Throwable {
					return new DynamicResourceDelegate((HTTPResourceInteractionModel) invocation.getArguments()[0], (HTTPResourceInteractionModel) invocation.getArguments()[1]);
				}
		});
		RegistrarWithSingletons rs = new RegistrarWithSingletons();
		rs.setResourceRegistry(rRegistry);
		assertNotNull(rs.getInstances());
		assertEquals(3, rs.getInstances().size());
		
		// should have the same parent
		DynamicResourceDelegate parent = null;
		for (Object obj : rs.getInstances()) {
			DynamicResourceDelegate rd = (DynamicResourceDelegate) obj;
			if (rd.getParent() != null && parent == null)
				parent = (DynamicResourceDelegate) rd.getParent();
			else
				assertEquals(parent, rd.getParent());
		}
		
		// verify resource delegate created 3 times
		verifyNew(DynamicResourceDelegate.class, times(3)).withArguments(any(HTTPResourceInteractionModel.class), any(HTTPResourceInteractionModel.class));
	}

	@Test
	public void testParachute() throws Exception {
		// mock a few resources with a simple hierarchy, added out of order to test the climbing back up to the root
		ResourceRegistry rRegistry = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPResourceInteractionModel>());
		HTTPResourceInteractionModel r1 = createMockHTTPRIM("home", "/");
		// child 1
		HTTPResourceInteractionModel cr1 = createMockHTTPRIM("notes", "/notes");
		when(cr1.getParent()).thenReturn(r1);
		// child 2
		HTTPResourceInteractionModel cr2 = createMockHTTPRIM("note", "/{id}");
		when(cr2.getParent()).thenReturn(cr1);

		rRegistry.add(cr2);
		rRegistry.add(cr1);
		rRegistry.add(r1);
	
		RegistrarWithSingletons rs = new RegistrarWithSingletons();
		rs.setResourceRegistry(rRegistry);
		assertNotNull(rs.getInstances());
		assertEquals(3, rs.getInstances().size());
	}

	@Test
	public void testODataCollectionResource() throws Exception {
		ResourceRegistry rRegistry = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPResourceInteractionModel>());
		HTTPResourceInteractionModel r1 = createMockHTTPRIM("home", "/customers()");
		rRegistry.add(r1);
	
		RegistrarWithSingletons rs = new RegistrarWithSingletons();
		rs.setResourceRegistry(rRegistry);
		assertNotNull(rs.getInstances());
		assertEquals(2, rs.getInstances().size());
		for(Object resource : rs.getInstances()) {
			DynamicResourceDelegate dr = (DynamicResourceDelegate) resource;
			Assert.assertTrue(dr.getPath().equals("/customers()") || dr.getPath().equals("/customers"));
		}
	}
}
