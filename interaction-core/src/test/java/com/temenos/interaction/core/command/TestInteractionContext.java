package com.temenos.interaction.core.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.junit.Test;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;

public class TestInteractionContext {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testResolveIdDefault() {
		MultivaluedMap<String, String> pathParameters = new MultivaluedMapImpl();
		pathParameters.add("id", "123");
		InteractionContext ctx = new InteractionContext(pathParameters, new MultivaluedMapImpl(), new ResourceState("entity", "initial_state", new ArrayList<Action>(), "/resource"));
		assertEquals("123", ctx.getId());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testResolveIdNoDefaultNull() {
		MultivaluedMap<String, String> pathParameters = new MultivaluedMapImpl();
		pathParameters.add("test", "123");
		InteractionContext ctx = new InteractionContext(pathParameters, new MultivaluedMapImpl(), new ResourceState("entity", "initial_state", new ArrayList<Action>(), "/resource"));
		assertNull(ctx.getId());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testResolveIdParameter() {
		MultivaluedMap<String, String> pathParameters = new MultivaluedMapImpl();
		pathParameters.add("TheTestParameterKey", "123");
		ResourceState state = new ResourceState("entity", "initial_state", new ArrayList<Action>(), "/resource", "TheTestParameterKey");
		InteractionContext ctx = new InteractionContext(pathParameters, new MultivaluedMapImpl(), state);
		assertEquals("123", ctx.getId());
	}

}
