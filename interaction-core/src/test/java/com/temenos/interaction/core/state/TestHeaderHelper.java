package com.temenos.interaction.core.state;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.core.Response;

import org.junit.Test;

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
		
		Response r = HeaderHelper.allowHeader(Response.ok(), validNextStates).build();
		assertEquals("AUTHORISE, DELETE, GET, HEAD, HISTORY, INPUT, OPTIONS, REVERSE, SEE", r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testOptionsNoAllowHeader() {
		Response r = HeaderHelper.allowHeader(Response.ok(), null).build();
		assertNull(r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testOptionsNoValidStates() {
		Response r = HeaderHelper.allowHeader(Response.ok(), new HashSet<String>()).build();
		assertEquals("", r.getMetadata().getFirst("Allow"));
	}

}
