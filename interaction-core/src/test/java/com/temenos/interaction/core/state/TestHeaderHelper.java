package com.temenos.interaction.core.state;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.temenos.interaction.core.command.ResourceGetCommand;

public class TestHeaderHelper {

	@Test
	public void testOptionsAllowHeader() {
		SortedSet<String> validNextStates = new TreeSet<String>();
		validNextStates.add("SEE");
		validNextStates.add("HISTORY");
		validNextStates.add("AUTHORISE");
		validNextStates.add("REVERSE");
		validNextStates.add("DELETE");
		validNextStates.add("INPUT");
		validNextStates.add("GET");
		validNextStates.add("HEAD");
		validNextStates.add("OPTIONS");
		
		ResourceGetCommand getCommand = mock(ResourceGetCommand.class);
		doReturn(validNextStates).when(getCommand).getValidNextStates();

		Response r = HeaderHelper.allowHeader(Response.ok(), getCommand).build();
		assertEquals("AUTHORISE, DELETE, GET, HEAD, HISTORY, INPUT, OPTIONS, REVERSE, SEE", r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testOptionsNoAllowHeader() {
		ResourceGetCommand getCommand = mock(ResourceGetCommand.class);
		doReturn(null).when(getCommand).getValidNextStates();
		
		Response r = HeaderHelper.allowHeader(Response.ok(), getCommand).build();
		assertNull(r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testOptionsNoValidStates() {
		ResourceGetCommand getCommand = mock(ResourceGetCommand.class);
		doReturn(new HashSet<String>()).when(getCommand).getValidNextStates();

		Response r = HeaderHelper.allowHeader(Response.ok(), getCommand).build();
		assertEquals("", r.getMetadata().getFirst("Allow"));
	}

}
