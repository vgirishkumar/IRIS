package com.temenos.interaction.core.state;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.temenos.interaction.core.RESTResponse;

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
		
		RESTResponse rr = new RESTResponse(Response.Status.OK, null, validNextStates);

		Response r = HeaderHelper.allowHeader(Response.ok(), rr).build();
		assertEquals("AUTHORISE, DELETE, GET, HEAD, HISTORY, INPUT, OPTIONS, REVERSE, SEE", r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testOptionsNoAllowHeader() {
		RESTResponse rr = new RESTResponse(Response.Status.OK, null, null);
		Response r = HeaderHelper.allowHeader(Response.ok(), rr).build();
		assertNull(r.getMetadata().getFirst("Allow"));
	}

	@Test
	public void testOptionsNoValidStates() {
		RESTResponse rr = new RESTResponse(Response.Status.OK, null, new HashSet<String>());
		Response r = HeaderHelper.allowHeader(Response.ok(), rr).build();
		assertEquals("", r.getMetadata().getFirst("Allow"));
	}

}
