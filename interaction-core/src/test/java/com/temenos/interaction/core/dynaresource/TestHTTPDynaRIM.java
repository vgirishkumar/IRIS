package com.temenos.interaction.core.dynaresource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.Test;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.CommandSpec;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.state.ResourceInteractionModel;


public class TestHTTPDynaRIM {

	@Test
	public void testRIMsCRUD() {
		ResourceState begin = new ResourceState("begin");
		ResourceState exists = new ResourceState("{id}");
		ResourceState end = new ResourceState("end");
	
		begin.addTransition(new CommandSpec("create", "PUT"), exists);
		begin.addTransition(new CommandSpec("update", "PUT"), exists);
		exists.addTransition(new CommandSpec("delete", "DELETE"), end);
		
		CommandController cc = mock(CommandController.class);
		HTTPDynaRIM parent = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc);
		verify(cc).fetchGetCommand();
		Collection<ResourceInteractionModel> resources = parent.createChildResources();
		assertEquals(1, resources.size());
		verify(cc, times(2)).fetchGetCommand();
		verify(cc).fetchStateTransitionCommand("PUT", "{id}");
		verify(cc).fetchStateTransitionCommand("DELETE", "{id}");
	}

	/*
	 * TODO implement if required
	@Test
	public void testEquality() {
		ResourceState begin = new ResourceState("begin");
		CommandController cc = new CommandController("");
		HTTPDynaRIM rim1 = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc);
		assertEquals(rim1, rim2);
		assertEquals(rim1.hashCode(), rim2.hashCode());
	}

	@Test
	public void testInequality() {
		ResourceState begin = new ResourceState("begin");
		ResourceState begin2 = new ResourceState("begin2");
		CommandController cc = new CommandController("");
		CommandController cc2 = new CommandController("");
		HTTPDynaRIM rim1 = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc);
		HTTPDynaRIM rim2 = new HTTPDynaRIM(null, "NOTE1", "/notes", begin, null, cc);
		HTTPDynaRIM rim3 = new HTTPDynaRIM(null, "NOTE", "/notes1", begin, null, cc);
		HTTPDynaRIM rim4 = new HTTPDynaRIM(null, "NOTE", "/notes", begin2, null, cc);
		HTTPDynaRIM rim5 = new HTTPDynaRIM(null, "NOTE", "/notes", begin, null, cc2);

		assertFalse(rim1.equals(rim2));
		assertFalse(rim1.hashCode() == rim2.hashCode());

		assertFalse(rim1.equals(rim3));
		assertFalse(rim1.hashCode() == rim3.hashCode());

		assertFalse(rim1.equals(rim4));
		assertFalse(rim1.hashCode() == rim4.hashCode());

		assertFalse(rim1.equals(rim5));
		assertFalse(rim1.hashCode() == rim5.hashCode());
	}
*/
}
