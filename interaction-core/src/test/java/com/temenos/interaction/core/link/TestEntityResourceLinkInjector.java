package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import javax.ws.rs.core.GenericEntity;

import org.junit.Test;

import com.temenos.interaction.core.resource.EntityResource;

public class TestEntityResourceLinkInjector {

	@Test
	public void testCanInjectRESTResource() {

		EntityResourceLinkInjector injector = new EntityResourceLinkInjector();
		EntityResource<Object> resource = new EntityResource<Object>(null);
		GenericEntity<?> ge = resource.getGenericEntity();

		assertTrue(injector.canInject(ge));
	}

}
