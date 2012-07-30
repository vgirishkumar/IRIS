package com.temenos.interaction.core.rim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class TestEqualityHTTPHypermediaRIM {

	@Test
	public void testEqualSamePathDifferentStateNames() {
		String ENTITY_NAME = "NOTE";
		// use same resource path, vital to this equality test
		String resourcePath = "/notes";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", resourcePath);
		String OTHER_ENTITY_NAME = "DIFFERENT";
		ResourceState begin2 = new ResourceState(OTHER_ENTITY_NAME, "begin2", resourcePath);
		NewCommandController cc = mock(NewCommandController.class);
		NewCommandController cc2 = mock(NewCommandController.class);
		HTTPHypermediaRIM rim1 = new HTTPHypermediaRIM(cc, new ResourceStateMachine(begin));
		HTTPHypermediaRIM rim2 = new HTTPHypermediaRIM(cc2, new ResourceStateMachine(begin2));
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testEqualityParent() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "/{id}");
		String PARENT_ENTITY_NAME = "PARENT";
		ResourceState parentBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", "/notes");
		String DIFFERENT_ENTITY_NAME = "DIFFERENT";
		ResourceState differentBegin = new ResourceState(DIFFERENT_ENTITY_NAME, "begin", "/{id}");
		NewCommandController cc = mock(NewCommandController.class);
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(cc, new ResourceStateMachine(parentBegin));
		HTTPHypermediaRIM rim1 = new HTTPHypermediaRIM(parent, cc, new ResourceStateMachine(begin), begin);
		HTTPHypermediaRIM rim2 = new HTTPHypermediaRIM(parent, cc, new ResourceStateMachine(differentBegin), differentBegin);
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testInequality() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "/{id}");
		ResourceState different = new ResourceState(ENTITY_NAME, "begin", "/{id}/different");
		String PARENT_ENTITY_NAME = "PARENT";
		ResourceState parentBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", "/notes");
		ResourceState parentDiffBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", "/notes1");
		NewCommandController cc = mock(NewCommandController.class);
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(cc, new ResourceStateMachine(parentBegin));
		HTTPHypermediaRIM rim1 = new HTTPHypermediaRIM(parent, cc, new ResourceStateMachine(begin), begin);
		HTTPHypermediaRIM rim2 = new HTTPHypermediaRIM(parent, cc, new ResourceStateMachine(different), different);
		HTTPHypermediaRIM rim3 = new HTTPHypermediaRIM(cc, new ResourceStateMachine(parentDiffBegin));
		HTTPHypermediaRIM rim4 = new HTTPHypermediaRIM(cc, new ResourceStateMachine(begin));

		// both with parent (different)
		assertFalse(rim1.equals(rim2));
		assertFalse(rim1.hashCode() == rim2.hashCode());

		// both without parent (different)
		assertFalse(parent.equals(rim3));
		assertFalse(parent.hashCode() == rim3.hashCode());

		// one with parent
		assertFalse(rim1.equals(rim4));
		assertFalse(rim1.hashCode() == rim4.hashCode());
	}

}
