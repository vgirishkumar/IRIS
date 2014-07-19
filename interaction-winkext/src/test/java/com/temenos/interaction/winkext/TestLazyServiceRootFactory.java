package com.temenos.interaction.winkext;

/*
 * #%L
 * interaction-winkext
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.Event;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.ResourceStateProvider;
import com.temenos.interaction.core.rim.HTTPResourceInteractionModel;

public class TestLazyServiceRootFactory {

	@Test
	public void testPath() {
		Properties beanMap = new Properties();
		beanMap.put("SimpleModel_Home_home", "GET /test");
		LazyServiceRootFactory factory = new LazyServiceRootFactory();
		factory.setBeanMap(beanMap);
		Set<HTTPResourceInteractionModel> serviceRoots = factory.getServiceRoots();
		assertEquals(1, serviceRoots.size());
		assertEquals("/test", serviceRoots.iterator().next().getResourcePath());
	}

	@Test
	public void testRegister() {
		Properties beanMap = new Properties();
		beanMap.put("SimpleModel_Home_home", "GET /test");
		LazyServiceRootFactory factory = new LazyServiceRootFactory();
		factory.setResourceStateProvider(mockResourceStateProvider(beanMap));
		factory.setBeanMap(beanMap);
		factory.setCommandController(mock(NewCommandController.class));
		factory.setMetadata(mockMetadata());
		Set<HTTPResourceInteractionModel> serviceRoots = factory.getServiceRoots();
		assertEquals(1, serviceRoots.size());
		// force initialise
		serviceRoots.iterator().next().getCurrentState();
		
		ResourceStateMachine rsm = factory.getHypermediaEngine();
		assertEquals("GET", rsm.getInteractionByPath().get("/test").iterator().next());
	}

	@Test
	public void testRegisterMultiple() {
		Properties beanMap = new Properties();
		beanMap.put("SimpleModel_Home_home", "GET /test");
		beanMap.put("SimpleModel_Home_test", "PUT /test");
		LazyServiceRootFactory factory = new LazyServiceRootFactory();
		factory.setResourceStateProvider(mockResourceStateProvider(beanMap));
		factory.setBeanMap(beanMap);
		factory.setCommandController(mock(NewCommandController.class));
		factory.setMetadata(mockMetadata());
		Set<HTTPResourceInteractionModel> serviceRoots = factory.getServiceRoots();
		assertEquals(1, serviceRoots.size());
		// force initialise
		serviceRoots.iterator().next().getCurrentState();
		
		ResourceStateMachine rsm = factory.getHypermediaEngine();
		assertTrue(rsm.getInteractionByPath().get("/test").contains("GET"));
		assertTrue(rsm.getInteractionByPath().get("/test").contains("PUT"));
	}

	private ResourceStateProvider mockResourceStateProvider(Properties beanMap) {
		final Map<String, Set<String>> resourceStatesByPath = new LazyServiceRootFactory().getResourceStatesByPath(beanMap);
		final Map<String, ResourceState> statesByName = new HashMap<String, ResourceState>();
		for (Object key : beanMap.keySet()) {
			String stateName = key.toString();
			String binding = beanMap.getProperty(stateName);
			// split into methods and path
			String[] strs = binding.split(" ");
			String path = strs[1];
			statesByName.put(stateName, new ResourceState("mock", stateName, new ArrayList<Action>(), path));
		}
		return new ResourceStateProvider() {
			@Override
			public Map<String, Set<String>> getResourceStatesByPath() {
				return resourceStatesByPath;
			}
			
			@Override
			public ResourceState getResourceState(String name) {
				return statesByName.get(name);
			}
			
			@Override
			public ResourceState determineState(Event event, String resourcePath) {
				return null;
			}
		};
	}
	
	private Metadata mockMetadata() {
		Metadata metadata = new Metadata("MOCKMODEL");
		EntityMetadata entityMetadata = new EntityMetadata("mock");
		metadata.setEntityMetadata(entityMetadata);
		return metadata;
	}
}
