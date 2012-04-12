package com.temenos.interaction.winkext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.HashSet;

import org.apache.wink.common.DynamicResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.odata4j.edm.EdmDataServices;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;
import com.temenos.interaction.core.dynaresource.HTTPDynaRIM;
import com.temenos.interaction.core.state.HTTPResourceInteractionModel;
import com.temenos.interaction.core.state.ResourceInteractionModel;
import com.temenos.interaction.winkext.DynamicResourceDelegate;
import com.temenos.interaction.winkext.RegistrarWithSingletons;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistrarWithSingletons.class})
public class TestRegistrarWithSingletons {

	private HTTPDynaRIM createMockHTTPDynaRIM(String entityName, String path) {
		HTTPDynaRIM rim = mock(HTTPDynaRIM.class);
		ResourceState rs = mock(ResourceState.class);
		when(rs.getName()).thenReturn("");
		when(rs.getEntityName()).thenReturn(entityName);
		ResourceStateMachine rsm = mock(ResourceStateMachine.class);
		when(rsm.getInitial()).thenReturn(rs);
		when(rim.getStateMachine()).thenReturn(rsm);
		when(rim.getCurrentState()).thenReturn(rs);
		when(rim.getResourcePath()).thenReturn(path);
		when(rim.getFQResourcePath()).thenCallRealMethod();
		return rim;
	}

	@Test
	public void testHeirarchy() throws Exception {
		// mock a few resources with a simple hierarchy in the resource registry
		ResourceRegistry rRegistry = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPDynaRIM>());
		HTTPDynaRIM r1 = createMockHTTPDynaRIM("notes", "/notes");
		rRegistry.add(r1);
		// child 1
		HTTPDynaRIM cr1 = createMockHTTPDynaRIM("draftNote", "/draft/{id}");
		rRegistry.add(cr1);
		// child 2
		HTTPDynaRIM cr2 = createMockHTTPDynaRIM("note", "/{id}");
		rRegistry.add(cr2);
	
		whenNew(DynamicResourceDelegate.class).withParameterTypes(HTTPResourceInteractionModel.class, HTTPDynaRIM.class).withArguments(any(DynamicResource.class), any(ResourceInteractionModel.class)).thenAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) throws Throwable {
					return new DynamicResourceDelegate((HTTPResourceInteractionModel) invocation.getArguments()[0], (HTTPDynaRIM) invocation.getArguments()[1]);
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
		verifyNew(DynamicResourceDelegate.class, times(3)).withArguments(any(HTTPDynaRIM.class), any(HTTPDynaRIM.class));
	}

	@Test
	public void testParachute() throws Exception {
		// mock a few resources with a simple hierarchy, added out of order to test the climbing back up to the root
		ResourceRegistry rRegistry = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPDynaRIM>());
		HTTPDynaRIM r1 = createMockHTTPDynaRIM("home", "/");
		// child 1
		HTTPDynaRIM cr1 = createMockHTTPDynaRIM("notes", "/notes");
		when(cr1.getParent()).thenReturn(r1);
		// child 2
		HTTPDynaRIM cr2 = createMockHTTPDynaRIM("note", "/{id}");
		when(cr2.getParent()).thenReturn(cr1);

		rRegistry.add(cr2);
		rRegistry.add(cr1);
		rRegistry.add(r1);
	
		RegistrarWithSingletons rs = new RegistrarWithSingletons();
		rs.setResourceRegistry(rRegistry);
		assertNotNull(rs.getInstances());
		assertEquals(3, rs.getInstances().size());
	}

}
