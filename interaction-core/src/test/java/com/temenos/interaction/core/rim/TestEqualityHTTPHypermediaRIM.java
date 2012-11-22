package com.temenos.interaction.core.rim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class TestEqualityHTTPHypermediaRIM {

	private List<Action> mockActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action("DO", Action.TYPE.ENTRY));
		return actions;
	}
	
	private NewCommandController mockCommandController() {
		NewCommandController cc = mock(NewCommandController.class);
		when(cc.fetchCommand("DO")).thenReturn(mock(InteractionCommand.class));
		return cc;
	}
	
	@Test
	public void testEqualSamePathDifferentStateNames() {
		String ENTITY_NAME = "NOTE";
		// use same resource path, vital to this equality test
		String resourcePath = "/notes";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", mockActions(), resourcePath);
		String OTHER_ENTITY_NAME = "DIFFERENT";
		ResourceState begin2 = new ResourceState(OTHER_ENTITY_NAME, "begin2", mockActions(), resourcePath);
		HTTPHypermediaRIM rim1 = new HTTPHypermediaRIM(mockCommandController(), new ResourceStateMachine(begin), mock(Metadata.class));
		HTTPHypermediaRIM rim2 = new HTTPHypermediaRIM(mockCommandController(), new ResourceStateMachine(begin2), mock(Metadata.class));
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testEqualityParent() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", mockActions(), "/{id}");
		String PARENT_ENTITY_NAME = "PARENT";
		ResourceState parentBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", mockActions(), "/notes");
		String DIFFERENT_ENTITY_NAME = "DIFFERENT";
		ResourceState differentBegin = new ResourceState(DIFFERENT_ENTITY_NAME, "begin", mockActions(), "/{id}");
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(mockCommandController(), new ResourceStateMachine(parentBegin), mock(Metadata.class));
		HTTPHypermediaRIM rim1 = new HTTPHypermediaRIM(parent, mockCommandController(), new ResourceStateMachine(begin), begin, mock(Metadata.class));
		HTTPHypermediaRIM rim2 = new HTTPHypermediaRIM(parent, mockCommandController(), new ResourceStateMachine(differentBegin), differentBegin, mock(Metadata.class));
		
		// the only thing used to compare equality is the path as the URI must be unique
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testInequality() {
		String ENTITY_NAME = "NOTE";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", mockActions(), "/{id}");
		ResourceState different = new ResourceState(ENTITY_NAME, "begin", mockActions(), "/{id}/different");
		String PARENT_ENTITY_NAME = "PARENT";
		ResourceState parentBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", mockActions(), "/notes");
		ResourceState parentDiffBegin = new ResourceState(PARENT_ENTITY_NAME, "begin", mockActions(), "/notes1");
		HTTPHypermediaRIM parent = new HTTPHypermediaRIM(mockCommandController(), new ResourceStateMachine(parentBegin), mock(Metadata.class));
		HTTPHypermediaRIM rim1 = new HTTPHypermediaRIM(parent, mockCommandController(), new ResourceStateMachine(begin), begin, mock(Metadata.class));
		HTTPHypermediaRIM rim2 = new HTTPHypermediaRIM(parent, mockCommandController(), new ResourceStateMachine(different), different, mock(Metadata.class));
		HTTPHypermediaRIM rim3 = new HTTPHypermediaRIM(mockCommandController(), new ResourceStateMachine(parentDiffBegin), mock(Metadata.class));
		HTTPHypermediaRIM rim4 = new HTTPHypermediaRIM(mockCommandController(), new ResourceStateMachine(begin), mock(Metadata.class));

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
