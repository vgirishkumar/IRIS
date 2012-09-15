package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.odata4j.core.OLink;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEventReader2;

import com.temenos.interaction.core.hypermedia.ResourceRegistry;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
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
