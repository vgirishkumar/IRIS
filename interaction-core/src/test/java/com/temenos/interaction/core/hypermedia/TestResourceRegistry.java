package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.odata4j.edm.EdmDataServices;

import com.temenos.interaction.core.rim.HTTPResourceInteractionModel;
import com.temenos.interaction.core.rim.ResourceInteractionModel;

public class TestResourceRegistry {

	@Test
	public void testNoResources() {
		ResourceRegistry rr = new ResourceRegistry(mock(EdmDataServices.class), new HashSet<HTTPResourceInteractionModel>());
		Set<ResourceInteractionModel> set = rr.getResourceInteractionModels();
		assertNotNull(set);
		assertEquals(0, set.size());
	}

}
