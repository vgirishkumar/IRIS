package com.temenos.interaction.core.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.junit.Test;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;

public class TestInteractionContext {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testResolveIdDefault() {
		MultivaluedMap<String, String> pathParameters = new MultivaluedMapImpl();
		pathParameters.add("id", "123");
		InteractionContext ctx = new InteractionContext(pathParameters, new MultivaluedMapImpl(), new ResourceState("entity", "initial_state", new ArrayList<Action>(), "/resource"), mock(Metadata.class));
		assertEquals("123", ctx.getId());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testResolveIdNoDefaultNull() {
		MultivaluedMap<String, String> pathParameters = new MultivaluedMapImpl();
		pathParameters.add("test", "123");
		InteractionContext ctx = new InteractionContext(pathParameters, new MultivaluedMapImpl(), new ResourceState("entity", "initial_state", new ArrayList<Action>(), "/resource"), mock(Metadata.class));
		assertNull(ctx.getId());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testResolveIdParameter() {
		MultivaluedMap<String, String> pathParameters = new MultivaluedMapImpl();
		pathParameters.add("TheTestParameterKey", "123");
		ResourceState state = new ResourceState("entity", "initial_state", new ArrayList<Action>(), "/resource", "TheTestParameterKey");
		InteractionContext ctx = new InteractionContext(pathParameters, new MultivaluedMapImpl(), state, mock(Metadata.class));
		assertEquals("123", ctx.getId());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testResolveSimpleIdFromMetadata() {
		// create metadata with an entity that declares a field 'myId' as its id
		Metadata metadata = new Metadata("SimpleModel");
		EntityMetadata vocs = new EntityMetadata("entity");
		Vocabulary vocName = new Vocabulary();
		vocName.setTerm(new TermComplexType(false));
		vocName.setTerm(new TermIdField(true));
		vocs.setPropertyVocabulary("myId", vocName);
		metadata.setEntityMetadata(vocs);
		
		MultivaluedMap<String, String> pathParameters = new MultivaluedMapImpl();
		pathParameters.add("myId", "123");
		InteractionContext ctx = new InteractionContext(pathParameters, 
				new MultivaluedMapImpl(), 
				new ResourceState("entity", "initial_state", new ArrayList<Action>(), "/resource"),
				metadata);
		assertEquals("123", ctx.getId());
	}

	@Test
	public void testAttributes() {
		InteractionContext ctx = new InteractionContext(new MultivaluedMapImpl<String, String>(), new MultivaluedMapImpl<String, String>(), new ResourceState("entity", "initial_state", new ArrayList<Action>(), "/resource"), mock(Metadata.class));
		ctx.setAttribute("abc", "test");
		
		assertEquals(ctx.getAttribute("abc"), "test");
	}
}
