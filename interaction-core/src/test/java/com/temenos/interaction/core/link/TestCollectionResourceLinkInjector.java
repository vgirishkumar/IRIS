package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import javax.ws.rs.core.GenericEntity;

import org.junit.Test;

import com.temenos.interaction.core.resource.CollectionResource;

public class TestCollectionResourceLinkInjector {

	@Test
	public void testCanInjectRESTResource() {

		CollectionResourceLinkInjector injector = new CollectionResourceLinkInjector();
		CollectionResource<Object> resource = new CollectionResource<Object>("EntitySet", null);
		GenericEntity<?> ge = resource.getGenericEntity();

		assertTrue(injector.canInject(ge));
	}

}
